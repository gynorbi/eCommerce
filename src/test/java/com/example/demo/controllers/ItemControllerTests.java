package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.TestHelper.generateItem;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTests {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @Test
    public void get_items_happy_path(){
        when(itemRepository.findAll()).thenReturn(Arrays.asList(generateItem(1L,""), generateItem(2L,"")));

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull("Response should not be null",response);
        assertEquals("Status code should be 200", 200, response.getStatusCodeValue());
        List<Item> actualItems = response.getBody();
        assertNotNull("Body of response should not be null", actualItems);
        assertEquals("Number of items shold be 2", 2, actualItems.size());
    }

    @Test
    public void get_item_by_id_happy_path(){
        Long expectedId = 1L;
        when(itemRepository.findById(any())).thenReturn(Optional.of(generateItem(expectedId,"")));

        ResponseEntity<Item> response = itemController.getItemById(expectedId);

        assertNotNull("Response should not be null",response);
        assertEquals("Status code should be 200", 200, response.getStatusCodeValue());
        Item actualItem = response.getBody();
        assertNotNull("Body of response should not be null", actualItem);
        assertEquals("Id of item should be correct", expectedId, actualItem.getId());
    }

    @Test
    public void get_item_by_id_no_such_item(){
        Long expectedId = 1L;
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(expectedId);

        assertNotNull("Response should not be null",response);
        assertEquals("Status code should be 404", 404, response.getStatusCodeValue());
        Item actualItem = response.getBody();
        assertNull("Body of response should be null", actualItem);
    }

    @Test
    public void get_items_by_name_happy_path(){
        String expectedName = "cool_item";
        Item item1 = generateItem(1L, "");
        Item item2 = generateItem(2L, "");
        item1.setName(expectedName);
        item2.setName(expectedName);
        when(itemRepository.findByName(expectedName)).thenReturn(Arrays.asList(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItemsByName(expectedName);

        assertNotNull("Response should not be null",response);
        assertEquals("Status code should be 200", 200, response.getStatusCodeValue());
        List<Item> actualItems = response.getBody();
        assertNotNull("Body of response should not be null", actualItems);
        assertEquals("Number of items shold be 2", 2, actualItems.size());
    }

    @Test
    public void get_items_by_name_none_found(){
        String expectedName = "cool_item";
        when(itemRepository.findByName(expectedName)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Item>> response = itemController.getItemsByName(expectedName);

        assertNotNull("Response should not be null",response);
        assertEquals("Status code should be 404", 404, response.getStatusCodeValue());
        List<Item> actualItems = response.getBody();
        assertNull("Body of response should be null", actualItems);
    }
}
