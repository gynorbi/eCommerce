package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private Logger log = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("Trying to find user with id '{}'", id);
		Optional<User> user = userRepository.findById(id);
		user.ifPresentOrElse(
				username -> log.info("User with id '{}' was found successfully", id),
				() -> log.info("User with id '{}' was not found", id)
		);
		return ResponseEntity.of(user);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("Trying to find user with name '{}'", username);
		User user = userRepository.findByUsername(username);
		if(user == null){
			log.info("User '{}' was not found", username);
			return ResponseEntity.notFound().build();
		}
		else{
			log.info("User '{}' found successfully", username);
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		log.info("CreateUserRequest received for user '{}'", createUserRequest.getUsername());
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if(createUserRequest.getPassword().length() < 7 ||
		!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.warn("Password not matching criteria. Cannot create user '{}'", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		log.debug("Encoding password");
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		log.debug("Password encoded");

		userRepository.save(user);
		log.info("CreateUserRequest successful for user '{}'", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
