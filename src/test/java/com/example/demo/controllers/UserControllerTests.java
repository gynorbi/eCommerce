package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserControllerTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private UserController userController;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        when(encoder.encode(any())).thenReturn("thisIsHashed");
    }

    @Test
    public void create_user_happy_path(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200", 200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull("Created user should not be null", user);
        assertEquals("Created user id should be 0",0,user.getId());
        assertEquals("Username should be test","test",user.getUsername());
        assertEquals("Password should be hashed", "thisIsHashed",user.getPassword());
    }

    @Test
    public void create_user_short_password(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("short");
        createUserRequest.setConfirmPassword("short");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Response should be Bad Request", 400, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull("No user should have been created", u);
    }

    @Test
    public void create_user_not_matching_passwords(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword1");
        createUserRequest.setConfirmPassword("testPassword2");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull("Response should not be null", response);
        assertEquals("Response should be Bad Request", 400, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull("No user should have been created", u);
    }

    @Test
    public void find_by_id_for_existing_user(){
        Long expectedId = 1L;
        User expected = getUser(expectedId);
        when(userRepository.findById(expectedId)).thenReturn(Optional.of(expected));

        ResponseEntity<User> response = userController.findById(expectedId);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200",200,response.getStatusCodeValue());
        User actualUser = response.getBody();
        assertNotNull("Returned user should not be null", actualUser);
        assertEquals("Id should be expected one", expected.getId(), actualUser.getId());
    }

    @Test
    public void find_by_id_for_non_existing_user(){
        Long expectedId = 1L;
        User expected = getUser(expectedId);
        when(userRepository.findById(expectedId)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(expectedId);

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404",404,response.getStatusCodeValue());
        User actualUser = response.getBody();
        assertNull("Returned user should be null", actualUser);
    }

    @Test
    public void find_by_username_for_existing_user(){
        User expected = getUser(1L);
        when(userRepository.findByUsername(expected.getUsername())).thenReturn(expected);

        ResponseEntity<User> response = userController.findByUserName(expected.getUsername());

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 200",200,response.getStatusCodeValue());
        User actualUser = response.getBody();
        assertNotNull("Returned user should not be null", actualUser);
        assertEquals("Name should be expected one", expected.getUsername(), actualUser.getUsername());
    }

    @Test
    public void find_by_username_for_non_existing_user(){
        Long expectedId = 1L;
        User expected = getUser(expectedId);
        when(userRepository.findByUsername(expected.getUsername())).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName(expected.getUsername());

        assertNotNull("Response should not be null", response);
        assertEquals("Status code should be 404",404,response.getStatusCodeValue());
        User actualUser = response.getBody();
        assertNull("Returned user should be null", actualUser);
    }

    private User getUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user"+id.toString());
        user.setPassword("hashedPassword"+id.toString());
        user.setCart(mock(Cart.class));
        return user;
    }

}