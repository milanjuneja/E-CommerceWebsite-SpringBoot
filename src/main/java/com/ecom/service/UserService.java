package com.ecom.service;

import com.ecom.api.model.RegistrationBody;
import com.ecom.model.LocalUser;
import com.ecom.model.dao.LocalUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private LocalUserDao localUserDao;

    public LocalUser registerUser(RegistrationBody registrationBody){
        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setUserName(registrationBody.getUserName());
        user.setPassword(registrationBody.getPassword());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        return this.localUserDao.save(user);

    }

}
