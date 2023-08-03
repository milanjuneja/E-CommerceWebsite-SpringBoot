package com.ecom.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.ecom.model.LocalUser;
import com.ecom.model.dao.LocalUserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
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
    @Test
    public void testLoginJWTNotGeneratedByUs() {
        String token =
                JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256(
                        "NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class,
                () -> jwtService.getUserName(token));
    }

    @Test
    public void testLoginJWTCorrectlySignedNoIssuer() {
        String token =
                JWT.create().withClaim("USERNAME", "UserA")
                        .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class,
                () -> jwtService.getUserName(token));
    }
    @Test
    public void testResetPasswordJWTNotGeneratedByUs() {
        String token =
                JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256(
                        "NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class,
                () -> jwtService.getResetPasswordEmail(token));
    }

    @Test
    public void testResetPasswordJWTCorrectlySignedNoIssuer() {
        String token =
                JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com")
                        .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class,
                () -> jwtService.getResetPasswordEmail(token));
    }

    @Test
    public void testPasswordResetToken(){
        LocalUser user = localUserDao.findByUserNameIgnoreCase("UserA").get();


        String token = jwtService.generatePasswordResetJWT(user);
        Assertions.assertEquals(user.getEmail(),
                jwtService.getResetPasswordEmail(token), "Email should match inside");

    }
}