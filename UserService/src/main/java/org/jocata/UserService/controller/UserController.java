package org.jocata.UserService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.jocata.UserService.model.MyUser;
import org.jocata.UserService.request.UserRequest;
import org.jocata.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public MyUser createUser(@RequestBody @Valid UserRequest userRequest) throws JsonProcessingException {
        //if(userRequest.getDob() != null && !userRequest.getDob().isEmpty()) instead of this I will use custom annotation
        return userService.createUser(userRequest);
    }
    @GetMapping("getUser")
    public MyUser findUser(@RequestParam("contact") String contact){
        return (MyUser) userService.loadUserByUsername(contact);
    }
}
