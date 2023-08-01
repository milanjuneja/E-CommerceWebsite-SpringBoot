package com.ecom.api.security;

import com.ecom.model.LocalUser;
import com.ecom.model.dao.LocalUserDao;
import com.ecom.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDao localUserDao;

    private static final String AUTHENTICATED_PATH = "/auth/me";

    @Test
    public void testUnAuthenticatedRequest() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testBadToken() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization","BadTokenThatIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer BadTokenThatIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

    }
    @Test
    public void testUnverifiedUser() throws Exception{
        LocalUser user = localUserDao.findByUserNameIgnoreCase("UserB").get();
        String token = jwtService.generateJWT(user);

        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer" + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}