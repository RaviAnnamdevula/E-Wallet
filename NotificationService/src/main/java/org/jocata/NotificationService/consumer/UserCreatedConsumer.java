package org.jocata.NotificationService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jocata.Utils.CommonConstants;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedConsumer {

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private JavaMailSender sender;

        @KafkaListener(topics = CommonConstants.USER_CREATION_TOPIC, groupId = "notification-group")
        public void sendNotification(String message) throws JsonProcessingException {
            // Add logic to process the message
                JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);
                String name = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_NAME);
                String email = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_EMAIL);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(email);
                mailMessage.setFrom("wallet@gmail.com");
                mailMessage.setSubject("Welcome to E-Wallet");
                mailMessage.setText("Welcome " + name + " to E-Wallet");
                sender.send(mailMessage);
        }
}
