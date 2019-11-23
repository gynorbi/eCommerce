package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.demo.util.TestHelper.generateCart;
import static com.example.demo.util.TestHelper.generateUser;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTests {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void submit_existing_user(){
        User expectedUser = generateUser(1L);
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);

        ResponseEntity<UserOrder> response = orderController.submit(expectedUser.getUsername());

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200", 200, response.getStatusCodeValue());
        UserOrder actualUserOrder = response.getBody();
        assertNotNull("UserOrder should not be null", actualUserOrder);
        assertEquals("User should be the same", expectedUser.getUsername(), actualUserOrder.getUser().getUsername());
        assertEquals("Number of items should be the same", expectedUser.getCart().getItems().size(), actualUserOrder.getItems().size());
        assertEquals("Total ammount of order shoudl be correct", expectedUser.getCart().getTotal(),actualUserOrder.getTotal());
    }

    @Test
    public void submit_non_existing_user(){
        User expectedUser = generateUser(1L);
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(expectedUser.getUsername());

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404", 404, response.getStatusCodeValue());
        UserOrder actualUserOrder = response.getBody();
        assertNull("UserOrder should be null", actualUserOrder);
    }

    @Test
    public void get_orders_for_user_existing_user(){
        User expectedUser = generateUser(1L);
        UserOrder userOrder1 = UserOrder.createFromCart(generateCart(1L, expectedUser));
        UserOrder userOrder2 = UserOrder.createFromCart(generateCart(2L, expectedUser));
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);
        when(orderRepository.findByUser(expectedUser)).thenReturn(Arrays.asList(userOrder1,userOrder2));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(expectedUser.getUsername());

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200", 200, response.getStatusCodeValue());
        List<UserOrder> actualUserOrderList = response.getBody();
        assertNotNull("UserOrder list should not be null", actualUserOrderList);
        assertEquals("User should be the same", expectedUser.getUsername(), actualUserOrderList.get(0).getUser().getUsername());
        assertEquals("Number of orders should be 2", 2, actualUserOrderList.size());
    }

    @Test
    public void get_orders_for_non_existing_user(){
        User expectedUser = generateUser(1L);
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(expectedUser.getUsername());

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404", 404, response.getStatusCodeValue());
        List<UserOrder> actualUserOrderList = response.getBody();
        assertNull("UserOrder list should be null", actualUserOrderList);
    }

    @Test
    public void get_no_orders_for_user_existing_user(){
        User expectedUser = generateUser(1L);
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);
        when(orderRepository.findByUser(expectedUser)).thenReturn(Collections.EMPTY_LIST);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(expectedUser.getUsername());

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200", 200, response.getStatusCodeValue());
        List<UserOrder> actualUserOrderList = response.getBody();
        assertNotNull("UserOrder list should not be null", actualUserOrderList);
        assertEquals("Number of orders should be 0", 0, actualUserOrderList.size());
    }
}
