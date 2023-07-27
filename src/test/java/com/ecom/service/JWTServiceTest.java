package com.ecom.service;

import com.ecom.model.LocalUser;
import com.ecom.model.dao.LocalUserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDao localUserDao;

    @Test
    public void testVerificationTokenNotUsableForLogin(){
        LocalUser user  = localUserDao.findByUserNameIgnoreCase("UserA").get();
        String token = jwtService.generateVerificationJWT(user);
        Assertions.assertNull(jwtService.getUserName(token), "Verification token shoulkd  contain username");
    }

    @Test
    public void testAuthTokenReturnsUserName(){
        LocalUser user = localUserDao.findByUserNameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUserName(), jwtService.getUserName(token),"token should contain username");


    }
}