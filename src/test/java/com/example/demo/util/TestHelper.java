package com.example.demo.util;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;

public class TestHelper {
    public static Item generateItem(Long id, String prefix){
        Item item = new Item();
        item.setId(id);
        item.setName(prefix+"name"+id.toString());
        item.setDescription(prefix+"description"+id.toString());
        item.setPrice(new BigDecimal(1.1*id));

        return item;
    }

    public static Cart generateCart(Long id, User user){
        Cart cart = new Cart();
        cart.setId(id);
        cart.setUser(user);
        cart.addItem(generateItem(1L, "cart"+id.toString()));
        cart.addItem(generateItem(2L, "cart"+id.toString()));
        cart.addItem(generateItem(3L, "cart"+id.toString()));
        return cart;
    }

    public static User generateUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user"+id.toString());
        user.setPassword("hashedPassword"+id.toString());
        user.setCart(generateCart(id, user));
        return user;
    }
}
