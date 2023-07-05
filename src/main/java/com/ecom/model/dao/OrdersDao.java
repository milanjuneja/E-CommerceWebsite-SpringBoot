package com.ecom.model.dao;

import com.ecom.model.LocalUser;
import com.ecom.model.Orders;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrdersDao extends ListCrudRepository<Orders, Long> {

    List<Orders> findByUser(LocalUser user);

}
