package com.ecom.model.dao;

import com.ecom.model.Address;
import com.ecom.model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressDao extends ListCrudRepository<Address, Long> {


    List<Address> findByUser_UserId(Long id);

}
