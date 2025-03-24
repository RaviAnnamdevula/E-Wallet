package org.jocata.TxnService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jocata.TxnService.model.Txn;
import org.jocata.TxnService.model.TxnStatus;
import org.jocata.TxnService.repository.TxnRepository;
import org.jocata.Utils.CommonConstants;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TxnService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TxnRepository txnRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

   @Autowired
   private ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("txn-service", "txn-service");
        HttpEntity request = new HttpEntity(headers);
        JSONObject jsonObject = restTemplate.exchange("http://localhost:9091/user/getUser?contact=" + username, HttpMethod.GET, request, JSONObject.class).getBody();
        assert jsonObject != null;
        List<LinkedHashMap<String, String>> authorities = (List<LinkedHashMap<String,String>>) jsonObject.get("authorities");
       List<GrantedAuthority> list = authorities.stream().map(authority -> authority.get("authority")).map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toList());
        User user = new User(jsonObject.get("name").toString(), (String) jsonObject.get("password"), list);
        return user;
    }

    public String initTxn(String username, String receiver, String amount, String purpose) {
        Txn txn = Txn.builder().
                txnId(UUID.randomUUID().toString()).
                senderId(username).
                txnAmount(amount).
                receiverId(receiver).
                purpose(purpose).
                txnStatus(TxnStatus.INITIATED).
                build();
        txnRepository.save(txn);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.TXN_INITIATED_TOPIC_AMOUNT, amount);
        jsonObject.put(CommonConstants.TXN_INITIATED_TOPIC_TXN_ID, txn.getTxnId());
        jsonObject.put(CommonConstants.TXN_INITIATED_TOPIC_RECEIVER, receiver);
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_NAME, username);

        kafkaTemplate.send(CommonConstants.TXN_INITIATED_TOPIC, jsonObject.toString());
        return txn.toString();
    }

    @KafkaListener(topics = CommonConstants.TXN_UPDATED_TOPIC, groupId = "txn-group")
    public void updateTxn(String msg) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);
        String txnId = (String) jsonObject.get("txnId");
        String status = (String) jsonObject.get("status");
        String message = (String) jsonObject.get("message");

        txnRepository.updateTxnStatus(txnId, TxnStatus.valueOf(status), message);
    }

    public List<Txn> getTxns(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Txn> pageData = txnRepository.findBySenderId(username,pageable);
        return pageData.getContent();
    }
}
