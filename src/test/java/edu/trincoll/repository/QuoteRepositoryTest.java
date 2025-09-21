package edu.trincoll.repository;

import edu.trincoll.model.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the repository layer.
 * These tests should pass when the repository is properly implemented.
 */
class QuoteRepositoryTest {
    
    private QuoteRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryQuoteRepository();
        repository.deleteAll();
    }
    
    @Test
    @DisplayName("Should save and retrieve item by ID")
    void testSaveAndFindById() {
        Quote quote = new Quote("Test Item", "Description");
        quote.setCategory("Test Category");
        
        Quote saved = repository.save(quote);
        
        assertThat(saved.getId()).isNotNull();
        
        Optional<Quote> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Item");
    }
    
    @Test
    @DisplayName("Should return empty Optional for non-existent ID")
    void testFindByIdNotFound() {
        Optional<Quote> found = repository.findById(999L);
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("Should find all items")
    void testFindAll() {
        repository.save(new Quote("Item 1", "Desc 1"));
        repository.save(new Quote("Item 2", "Desc 2"));
        repository.save(new Quote("Item 3", "Desc 3"));
        
        List<Quote> all = repository.findAll();
        
        assertThat(all).hasSize(3);
        assertThat(all).extracting(Quote::getTitle)
                .containsExactlyInAnyOrder("Item 1", "Item 2", "Item 3");
    }
    
    @Test
    @DisplayName("Should delete item by ID")
    void testDeleteById() {
        Quote quote = repository.save(new Quote("To Delete", "Will be deleted"));
        Long id = quote.getId();
        
        assertThat(repository.existsById(id)).isTrue();
        
        repository.deleteById(id);
        
        assertThat(repository.existsById(id)).isFalse();
        assertThat(repository.findById(id)).isEmpty();
    }
    
    @Test
    @DisplayName("Should check if item exists")
    void testExistsById() {
        Quote quote = repository.save(new Quote("Exists", "Test"));
        
        assertThat(repository.existsById(quote.getId())).isTrue();
        assertThat(repository.existsById(999L)).isFalse();
    }
    
    @Test
    @DisplayName("Should count items correctly")
    void testCount() {
        assertThat(repository.count()).isZero();
        
        repository.save(new Quote("Item 1", "Desc"));
        repository.save(new Quote("Item 2", "Desc"));
        
        assertThat(repository.count()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should delete all items")
    void testDeleteAll() {
        repository.save(new Quote("Item 1", "Desc"));
        repository.save(new Quote("Item 2", "Desc"));
        
        assertThat(repository.count()).isEqualTo(2);
        
        repository.deleteAll();
        
        assertThat(repository.count()).isZero();
        assertThat(repository.findAll()).isEmpty();
    }
    
    @Test
    @DisplayName("Should save multiple items")
    void testSaveAll() {
        List<Quote> quotes = List.of(
                new Quote("Item 1", "Desc 1"),
                new Quote("Item 2", "Desc 2"),
                new Quote("Item 3", "Desc 3")
        );
        
        List<Quote> saved = repository.saveAll(quotes);
        
        assertThat(saved).hasSize(3);
        assertThat(saved).allMatch(item -> item.getId() != null);
        assertThat(repository.count()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("Should find items by status")
    void testFindByStatus() {
        Quote active = new Quote("Active", "Active item");
        active.setStatus(Quote.Status.ACTIVE);
        
        Quote inactive = new Quote("Inactive", "Inactive item");
        inactive.setStatus(Quote.Status.INACTIVE);
        
        Quote archived = new Quote("Archived", "Archived item");
        archived.setStatus(Quote.Status.ARCHIVED);
        
        repository.save(active);
        repository.save(inactive);
        repository.save(archived);
        
        List<Quote> activeQuotes = repository.findByStatus(Quote.Status.ACTIVE);
        assertThat(activeQuotes).hasSize(1);
        assertThat(activeQuotes.get(0).getTitle()).isEqualTo("Active");
        
        List<Quote> archivedQuotes = repository.findByStatus(Quote.Status.ARCHIVED);
        assertThat(archivedQuotes).hasSize(1);
    }
    
    @Test
    @DisplayName("Should find items by category")
    void testFindByCategory() {
        Quote quote1 = new Quote("Item 1", "Desc");
        quote1.setCategory("Work");
        
        Quote quote2 = new Quote("Item 2", "Desc");
        quote2.setCategory("Personal");
        
        Quote quote3 = new Quote("Item 3", "Desc");
        quote3.setCategory("Work");
        
        repository.save(quote1);
        repository.save(quote2);
        repository.save(quote3);
        
        List<Quote> workQuotes = repository.findByCategory("Work");
        
        // This test will fail until students implement the method
        assertThat(workQuotes).hasSize(2);
        assertThat(workQuotes).extracting(Quote::getTitle)
                .containsExactlyInAnyOrder("Item 1", "Item 3");
    }
    
    @Test
    @DisplayName("Should find items by tag")
    void testFindByTag() {
        Quote quote1 = new Quote("Item 1", "Desc");
        quote1.addTag("urgent");
        quote1.addTag("bug");
        
        Quote quote2 = new Quote("Item 2", "Desc");
        quote2.addTag("feature");
        
        Quote quote3 = new Quote("Item 3", "Desc");
        quote3.addTag("urgent");
        
        repository.save(quote1);
        repository.save(quote2);
        repository.save(quote3);
        
        List<Quote> urgentQuotes = repository.findByTag("urgent");
        
        // This test will fail until students implement the method
        assertThat(urgentQuotes).hasSize(2);
        assertThat(urgentQuotes).extracting(Quote::getTitle)
                .containsExactlyInAnyOrder("Item 1", "Item 3");
    }
    
    @Test
    @DisplayName("Should find items by title containing search term")
    void testFindByTitleContaining() {
        repository.save(new Quote("Java Programming", "Book"));
        repository.save(new Quote("Python Programming", "Book"));
        repository.save(new Quote("JavaScript Guide", "Book"));
        repository.save(new Quote("Data Structures", "Course"));
        
        List<Quote> programmingQuotes = repository.findByTitleContaining("Programming");
        
        // This test will fail until students implement the method
        assertThat(programmingQuotes).hasSize(2);
        assertThat(programmingQuotes).extracting(Quote::getTitle)
                .containsExactlyInAnyOrder("Java Programming", "Python Programming");
    }
}