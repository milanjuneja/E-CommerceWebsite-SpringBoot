package com.ecom.api.controller.user;

import com.ecom.model.Address;
import com.ecom.model.dao.AddressDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AddressDao addressDao;


    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@PathVariable Long userId){
        return ResponseEntity.ok(addressDao.findByUser_UserId(userId));
    }


}
