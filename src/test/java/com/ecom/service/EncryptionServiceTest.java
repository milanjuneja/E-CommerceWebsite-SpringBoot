package com.ecom.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;



    @Test
    void testEncryptPassword() {

        String password = "PasswordIsASecret!123";
        String hash = encryptionService.encryptPassword(password);
        Assertions.assertTrue(encryptionService.verifyPassword(password, hash), "Hashed password should match original");
        Assertions.assertFalse(encryptionService.verifyPassword(password + "123",hash), "Should not be valid");
    }

    @Test
    void testVerifyPassword() {
    }
}