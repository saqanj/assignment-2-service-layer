package edu.trincoll.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.trincoll.model.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the complete application stack.
 * Tests controller → service → repository integration.
 */
@SpringBootTest
@AutoConfigureMockMvc
class QuoteIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() throws Exception {
        // Clear all items before each test
        mockMvc.perform(get("/api/quotes"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    Quote[] quotes = objectMapper.readValue(content, Quote[].class);
                    for (Quote quote : quotes) {
                        mockMvc.perform(delete("/api/quotes/" + quote.getId()));
                    }
                });
    }
    
    @Test
    @DisplayName("Should create item via REST API")
    void testCreateItem() throws Exception {
        Quote quote = new Quote("Test Item", "Test Description");
        quote.setCategory("Test");
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.category").value("Test"));
    }
    
    @Test
    @DisplayName("Should reject invalid item creation")
    void testCreateInvalidItem() throws Exception {
        Quote quote = new Quote("", "Description"); // Invalid: empty title
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should get all items")
    void testGetAllItems() throws Exception {
        // Create test items
        Quote quote1 = new Quote("Item 1", "Desc 1");
        Quote quote2 = new Quote("Item 2", "Desc 2");
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote2)))
                .andExpect(status().isCreated());
        
        // Get all items
        mockMvc.perform(get("/api/quotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Item 1", "Item 2")));
    }
    
    @Test
    @DisplayName("Should get item by ID")
    void testGetItemById() throws Exception {
        // Create item
        Quote quote = new Quote("Test Item", "Test Description");
        
        String response = mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Quote created = objectMapper.readValue(response, Quote.class);
        
        // Get by ID
        mockMvc.perform(get("/api/quotes/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.title").value("Test Item"));
    }
    
    @Test
    @DisplayName("Should return 404 for non-existent item")
    void testGetNonExistentItem() throws Exception {
        mockMvc.perform(get("/api/quotes/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should update item")
    void testUpdateItem() throws Exception {
        // Create item
        Quote quote = new Quote("Original Title", "Original Description");
        
        String response = mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Quote created = objectMapper.readValue(response, Quote.class);
        
        // Update item
        created.setTitle("Updated Title");
        created.setDescription("Updated Description");
        
        mockMvc.perform(put("/api/quotes/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }
    
    @Test
    @DisplayName("Should delete item")
    void testDeleteItem() throws Exception {
        // Create item
        Quote quote = new Quote("To Delete", "Will be deleted");
        
        String response = mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Quote created = objectMapper.readValue(response, Quote.class);
        
        // Delete item
        mockMvc.perform(delete("/api/quotes/" + created.getId()))
                .andExpect(status().isNoContent());
        
        // Verify deleted
        mockMvc.perform(get("/api/quotes/" + created.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should get items by status")
    void testGetItemsByStatus() throws Exception {
        // Create items with different statuses
        Quote active = new Quote("Active Item", "Active");
        active.setStatus(Quote.Status.ACTIVE);
        
        Quote inactive = new Quote("Inactive Item", "Inactive");
        inactive.setStatus(Quote.Status.INACTIVE);
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(active)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactive)))
                .andExpect(status().isCreated());
        
        // Get active items
        mockMvc.perform(get("/api/quotes/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Active Item"));
    }
    
    @Test
    @DisplayName("Should get items grouped by category")
    void testGetItemsGroupedByCategory() throws Exception {
        // Create items in different categories
        Quote work1 = new Quote("Work 1", "Work item");
        work1.setCategory("Work");
        
        Quote work2 = new Quote("Work 2", "Another work item");
        work2.setCategory("Work");
        
        Quote personal = new Quote("Personal", "Personal item");
        personal.setCategory("Personal");
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(work1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(work2)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personal)))
                .andExpect(status().isCreated());
        
        // Get grouped items (will fail until implemented)
        mockMvc.perform(get("/api/quotes/grouped"))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("Should search items")
    void testSearchItems() throws Exception {
        // Create searchable items
        Quote quote1 = new Quote("Java Programming", "Learn Java");
        Quote quote2 = new Quote("Python Guide", "Learn Python");
        Quote quote3 = new Quote("JavaScript Tutorial", "Learn JS");
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote2)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote3)))
                .andExpect(status().isCreated());
        
        // Search (will return empty until implemented)
        mockMvc.perform(get("/api/quotes/search")
                        .param("query", "Java"))
                .andExpect(status().isOk());
    }
}