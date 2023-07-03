package com.ecom.service;

import com.ecom.api.model.LoginBody;
import com.ecom.api.model.RegistrationBody;
import com.ecom.exception.UserAlreadyExistsException;
import com.ecom.model.LocalUser;
import com.ecom.model.dao.LocalUserDao;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private LocalUserDao localUserDao;
    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JWTService jwtService;
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if(this.localUserDao.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || this.localUserDao.findByUserNameIgnoreCase(registrationBody.getUserName()).isPresent()){
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setUserName(registrationBody.getUserName());
        user.setPassword(this.encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        return this.localUserDao.save(user);

    }

    public String loginUser(LoginBody loginBody){
        Optional<LocalUser> opUser = this.localUserDao.findByUserNameIgnoreCase(loginBody.getUserName());

        if(opUser.isPresent()){

            LocalUser user = opUser.get();
            if(this.encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                return jwtService.generateJWT(user);
            }

        }
        return null;
    }

}
