package org.jocata.UserService.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.jocata.UserService.model.MyUser;
import org.jocata.UserService.model.UserStatus;
import org.jocata.UserService.model.UserType;
import org.jocata.UserService.repository.UserRepository;
import org.jocata.UserService.request.UserRequest;
import org.jocata.Utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Value("${user.authority}")
    private String userAuthority;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public MyUser createUser(UserRequest userRequest) throws JsonProcessingException {
        MyUser myUser = userRequest.toUser();
        myUser.setAuthorities(userAuthority);
        myUser.setPassword(encoder.encode(userRequest.getPassword()));
        myUser.setUserStatus(UserStatus.ACTIVE);
        myUser.setUserType(UserType.USER);
        userRepository.save(myUser);
        // once user is created I have to send the notification
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_NAME, StringUtils.isEmpty(myUser.getName())?"USER": myUser.getName());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_EMAIL, myUser.getEmail());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_PHONE_NO, myUser.getPhoneNo());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_USER_IDENTIFIER, myUser.getUserIdentifier());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_USER_IDENTIFIER_VALUE, myUser.getUserIdentifierValue());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_ID, myUser.getId());
        kafkaTemplate.send(CommonConstants.USER_CREATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
        return myUser;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        return userRepository.findByPhoneNo(phoneNo);
    }
}
