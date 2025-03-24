package org.jocata.WalletService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jocata.Utils.CommonConstants;
import org.jocata.Utils.UserIdentifier;
import org.jocata.WalletService.model.Wallet;
import org.jocata.WalletService.repository.WalletRepository;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedConsumer {

    @Autowired
    private WalletRepository walletRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = CommonConstants.USER_CREATION_TOPIC, groupId = "wallet-group")
    public void createWallet(String msg) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);
        String name = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_NAME);
        String email = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_EMAIL);
        String contact = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_PHONE_NO);
        Integer userId = (Integer) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_ID);
        String userIdentifier = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_USER_IDENTIFIER);
        String userIdentifierValue = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_USER_IDENTIFIER_VALUE);

        Wallet wallet = Wallet.builder().userId(userId).
                                        contact(contact).
                                        userIdentifier(UserIdentifier.valueOf(userIdentifier)).
                                        userIdentifierValue(userIdentifierValue).
                                        balance(50.00).
                                        build();
        walletRepository.save(wallet);
    }


}
