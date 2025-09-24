package edu.trincoll.controller;

import edu.trincoll.model.Quote;
import edu.trincoll.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller - should ONLY handle HTTP concerns.
 * All business logic should be in the service layer.
 */
@RestController
@RequestMapping("/api/quotes")
public class QuoteController {
    
    private final QuoteService service;
    
    public QuoteController(QuoteService service) {
        this.service = service;
    }
    
    @GetMapping
    public List<Quote> getAllQuotes() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote) {
        try {
            Quote saved = service.save(quote);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Quote> updateQuote(@PathVariable Long id, @RequestBody Quote quote) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        quote.setId(id);
        try {
            Quote updated = service.save(quote);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        try {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Additional endpoints for collections operations
    
    @GetMapping("/status/{status}")
    public List<Quote> getQuotesByStatus(@PathVariable Quote.Status status) {
        return service.findByStatus(status);
    }
    
    @GetMapping("/category/{category}")
    public List<Quote> getQuotesByCategory(@PathVariable String category) {
        return service.findByCategory(category);
    }
    
    @GetMapping("/grouped")
    public Map<String, List<Quote>> getQuotesGroupedByCategory() {
        return service.groupByCategory();
    }
    
    @GetMapping("/tags")
    public Set<String> getAllTags() {
        return service.getAllUniqueTags();
    }
    
    @GetMapping("/stats/status")
    public Map<Quote.Status, Long> getStatusStatistics() {
        return service.countByStatus();
    }
    
    @GetMapping("/search")
    public List<Quote> searchQuotes(@RequestParam String query) {
        return service.search(query);
    }
}