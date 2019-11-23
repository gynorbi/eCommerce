package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.example.demo.util.TestHelper.generateItem;
import static com.example.demo.util.TestHelper.generateUser;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTests {
    @InjectMocks
    private CartController cartController;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    public void add_to_cart_happy_path(){
        User expectedUser = generateUser(1L);
        Item expectedItem = generateItem(1L,"new-item-");
        int expectedQty = 10;
        int initialQty = expectedUser.getCart().getItems().size();
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);
        when(itemRepository.findById(any())).thenReturn(Optional.of(expectedItem));
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(expectedUser.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(expectedQty);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200",200, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNotNull("Resulting cart should not be null", actualCart);
        assertEquals("User of cart should be correct", expectedUser.getUsername(),actualCart.getUser().getUsername());
        assertEquals("Number of items in cart should be correct", expectedQty+initialQty, actualCart.getItems().size());
    }

    @Test
    public void add_to_cart_of_non_existing_user(){
        User expectedUser = generateUser(1L);
        int expectedQty = 10;
        when(userRepository.findByUsername(any())).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(expectedUser.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(expectedQty);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404",404, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNull("Resulting cart should be null", actualCart);
    }

    @Test
    public void add_to_cart_non_existing_item(){
        User expectedUser = generateUser(1L);
        int expectedQty = 10;
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(expectedUser.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(expectedQty);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404",404, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNull("Resulting cart should be null", actualCart);
    }

    @Test
    public void remove_from_cart_happy_path(){
        User expectedUser = generateUser(1L);
        Item expectedItem = generateItem(1L,"new-item-");
        Cart cartWithSameItem = new Cart();
        cartWithSameItem.setId(2L);
        cartWithSameItem.setUser(expectedUser);
        cartWithSameItem.addItem(expectedItem);
        cartWithSameItem.addItem(expectedItem);
        cartWithSameItem.addItem(expectedItem);
        expectedUser.setCart(cartWithSameItem);
        int removedQty = 2;
        int initialQty = expectedUser.getCart().getItems().size();
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);
        when(itemRepository.findById(any())).thenReturn(Optional.of(expectedItem));
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(expectedUser.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(removedQty);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200",200, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNotNull("Resulting cart should not be null", actualCart);
        assertEquals("User of cart should be correct", expectedUser.getUsername(),actualCart.getUser().getUsername());
        assertEquals("Number of items in cart should be correct", initialQty-removedQty, actualCart.getItems().size());
    }

    @Test
    public void remove_from_cart_of_non_existing_user(){
        User expectedUser = generateUser(1L);
        int expectedQty = 10;
        when(userRepository.findByUsername(any())).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(expectedUser.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(expectedQty);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404",404, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNull("Resulting cart should be null", actualCart);
    }

    @Test
    public void remove_from_cart_non_existing_item(){
        User expectedUser = generateUser(1L);
        int expectedQty = 10;
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(expectedUser.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(expectedQty);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404",404, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNull("Resulting cart should be null", actualCart);
    }

}
