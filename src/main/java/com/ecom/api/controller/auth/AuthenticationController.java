package com.ecom.api.controller.auth;

import com.ecom.api.model.RegistrationBody;
import com.ecom.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void registerUser(@RequestBody RegistrationBody registrationBody){
        this.userService.registerUser(registrationBody);

    }
}
