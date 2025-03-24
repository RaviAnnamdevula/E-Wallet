package org.jocata.WalletService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jocata.Utils.CommonConstants;
import org.jocata.WalletService.model.Wallet;
import org.jocata.WalletService.repository.WalletRepository;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TxnInitiatedConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = CommonConstants.TXN_INITIATED_TOPIC, groupId = "wallet-group")
    public void updateWallet(String msg) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);
        String txnId = (String) jsonObject.get(CommonConstants.TXN_INITIATED_TOPIC_TXN_ID);
        String sender = (String) jsonObject.get(CommonConstants.TXN_INITIATED_TOPIC_SENDER);
        String receiver = (String) jsonObject.get(CommonConstants.TXN_INITIATED_TOPIC_RECEIVER);
        Double amount = (Double) jsonObject.get(CommonConstants.TXN_INITIATED_TOPIC_AMOUNT);

        Wallet senderWallet = walletRepository.findByContact(sender);
        Wallet receiverWallet = walletRepository.findByContact(receiver);
        String message = String.format("Transaction %s initiated from %s to %s", txnId, sender, receiver);
        String status = "PENDING";
        if(senderWallet == null) {
            message = "Sender wallet not found";
            status = "FAILED";
        }else if(receiverWallet == null){
            message = "receiver wallet not found";
            status = "FAILURE";
        }else if(senderWallet.getBalance() < amount){
            message = "Money not enough in the sender wallet to make a transaction";
            status = "FAILURE";
        }else{


            walletRepository.updateWallet(sender, -amount);
            walletRepository.updateWallet(receiver, amount);
            message = "Transaction initiated successfully";
            status = "SUCCESS";
        }

        // kafka publish
        JSONObject response = new JSONObject();
        response.put("message", message);
        response.put("status", status );
        response.put("txnId", txnId);

        kafkaTemplate.send(CommonConstants.TXN_UPDATED_TOPIC, response);


    }
}
