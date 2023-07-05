package com.ecom.service;

import com.ecom.model.LocalUser;
import com.ecom.model.Orders;
import com.ecom.model.dao.OrdersDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrdersDao ordersDao;


    public List<Orders> getOrders(LocalUser user){
        return ordersDao.findByUser(user);
    }

}
