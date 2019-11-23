package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.example.demo.util.TestHelper.generateUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTests {

    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private UserRepository userRepository;

    private User existingUser;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init(){
        userDetailsService = new UserDetailsServiceImpl(userRepository);
        existingUser = generateUser(1L);
        when(userRepository.findByUsername(existingUser.getUsername())).thenReturn(existingUser);
        when(userRepository.findByUsername("noUser")).thenReturn(null);
    }

    @Test
    public void load_user_by_name_happy_path(){
        UserDetails actualUserDetails = userDetailsService.loadUserByUsername(existingUser.getUsername());

        assertNotNull("UserDetails should not be null", actualUserDetails);
        assertEquals("Username should be correct",existingUser.getUsername(),actualUserDetails.getUsername());
    }

    @Test
    public void load_user_by_name_no_such_user(){
        thrown.expect(UsernameNotFoundException.class);
        UserDetails actualUserDetails = userDetailsService.loadUserByUsername("noUser");
    }
}
