package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private Logger log = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		log.info("Received request to add to cart: '{}'", request);
		log.debug("Trying to find user '{}'",request.getUsername());
		User user = userRepository.findByUsername(request.getUsername());
		log.debug("Found user '{}'",request.getUsername());
		if(user == null) {
			log.warn("Unable to find user '{}'. Cannot perform add to cart request.", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		log.debug("Trying to find item with id '{}'", request.getItemId());
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.warn("Unable to find item with id '{}'. Cannot perform add to cart request.", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		log.debug("Found item with id '{}'", request.getItemId());
		Cart cart = user.getCart();
		log.debug("Adding found item '{}' time(s) to the cart of the user", request.getQuantity());
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		log.debug("Saving cart of the user");
		cartRepository.save(cart);
		log.info("Cart updated successfully for user '{}'", request.getUsername());
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		log.info("Received request to remove from cart: '{}'", request);
		log.debug("Trying to find user '{}'",request.getUsername());
		User user = userRepository.findByUsername(request.getUsername());
		log.debug("Found user '{}'",request.getUsername());
		if(user == null) {
			log.warn("Unable to find user '{}'. Cannot perform remove from cart request.", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.warn("Unable to find item with id '{}'. Cannot perform remove from cart request.", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		log.debug("Found item with id '{}'", request.getItemId());
		Cart cart = user.getCart();
		log.debug("Removing found item '{}' time(s) from the cart of the user", request.getQuantity());
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		log.debug("Saving cart of the user");
		cartRepository.save(cart);
		log.info("Cart updated successfully for user '{}'", request.getUsername());
		return ResponseEntity.ok(cart);
	}
		
}
