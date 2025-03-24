package org.jocata.UserService;

import org.jocata.UserService.model.MyUser;
import org.jocata.UserService.model.UserType;
import org.jocata.UserService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class,args);
    }


    @Override
    public void run(String... args) throws Exception {
        MyUser myUser = MyUser.builder().phoneNo("txn-service").
                password(encoder.encode("txn-service")).
                authorities("SERVICE").userType(UserType.SERVICE)
                .build();
        userRepository.save(myUser);
    }
}