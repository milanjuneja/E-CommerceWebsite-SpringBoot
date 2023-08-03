package com.ecom.api.controller.user;

import com.ecom.model.Address;
import com.ecom.model.LocalUser;
import com.ecom.model.dao.AddressDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AddressDao addressDao;


    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user,  @PathVariable Long userId){
//        System.out.println(user.getUserId());
        if(!userHasPermission(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(addressDao.findByUser_UserId(userId));
    }

    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user,
                                              @PathVariable Long userId,
                                              @RequestBody Address address){
        if(!userHasPermission(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setAddressId(null);

        LocalUser refUser = new LocalUser();
        refUser.setUserId(userId);
        address.setUser(refUser);
        return ResponseEntity.ok(addressDao.save(address));
    }


    @PatchMapping("{userId}/address/{addressId}")
    public ResponseEntity<Address> pathAddress(@AuthenticationPrincipal LocalUser user,
                                               @PathVariable Long userId,
                                               @PathVariable Long addressId,
                                               @RequestBody Address address){

        if(!userHasPermission(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(address.getAddressId() == addressId){
            Optional<Address> opAddress = addressDao.findById(addressId);
            if(opAddress.isPresent()){
                LocalUser originalUser = opAddress.get().getUser();
                if(originalUser.getUserId() == userId){
                    address.setUser(originalUser);
                    return ResponseEntity.ok(addressDao.save(address));
                }
            }


        }

        return ResponseEntity.badRequest().build();
    }

    private boolean userHasPermission(LocalUser user, Long id){
//        System.out.println(user);
        return user.getUserId() == id;
    }


}
