package com.ecom.api.controller.auth;

import com.ecom.api.model.LoginBody;
import com.ecom.api.model.LoginResponse;
import com.ecom.api.model.RegistrationBody;
import com.ecom.exception.EmailFailureException;
import com.ecom.exception.UserAlreadyExistsException;
import com.ecom.exception.UserNotVerifiedException;
import com.ecom.model.LocalUser;
import com.ecom.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;



    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationBody registrationBody) {

        try {
            this.userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        }
        catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = this.userService.loginUser(loginBody);
        } catch (UserNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED_EMAIL_RESENT";

            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else{
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }

    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String  token){
        if(userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }
}
