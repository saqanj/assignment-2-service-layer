package edu.trincoll.service;

import edu.trincoll.model.Quote;
import edu.trincoll.repository.InMemoryQuoteRepository;
import edu.trincoll.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the service layer.
 * Tests both inherited BaseService functionality and ItemService-specific methods.
 */
class QuoteServiceTest {
    
    private QuoteService service;
    private QuoteRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryQuoteRepository();
        service = new QuoteService(repository);
        repository.deleteAll();
    }
    
    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {
        
        @Test
        @DisplayName("Should reject null item")
        void testValidateNullItem() {
            assertThatThrownBy(() -> service.validateEntity(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }
        
        @Test
        @DisplayName("Should reject item without title")
        void testValidateNoTitle() {
            Quote quote = new Quote();
            quote.setDescription("Description");
            
            assertThatThrownBy(() -> service.validateEntity(quote))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Title is required");
        }
        
        @Test
        @DisplayName("Should reject item with empty title")
        void testValidateEmptyTitle() {
            Quote quote = new Quote("   ", "Description");
            
            assertThatThrownBy(() -> service.validateEntity(quote))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Title is required");
        }
        
        @Test
        @DisplayName("Should reject item with title too long")
        void testValidateTitleTooLong() {
            String longTitle = "a".repeat(101);
            Quote quote = new Quote(longTitle, "Description");
            
            assertThatThrownBy(() -> service.validateEntity(quote))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot exceed 100 characters");
        }
        
        @Test
        @DisplayName("Should accept valid item")
        void testValidateValidItem() {
            Quote quote = new Quote("Valid Title", "Valid Description");
            quote.setCategory("Test");
            
            assertThatNoException().isThrownBy(() -> service.validateEntity(quote));
        }
    }
    
    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudTests {
        
        @Test
        @DisplayName("Should save item with validation")
        void testSave() {
            Quote quote = new Quote("Test Item", "Description");
            
            Quote saved = service.save(quote);
            
            assertThat(saved.getId()).isNotNull();
            assertThat(service.count()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("Should not save invalid item")
        void testSaveInvalid() {
            Quote quote = new Quote("", "Description");
            
            assertThatThrownBy(() -> service.save(quote))
                    .isInstanceOf(IllegalArgumentException.class);
            
            assertThat(service.count()).isZero();
        }
        
        @Test
        @DisplayName("Should find item by ID")
        void testFindById() {
            Quote quote = service.save(new Quote("Test", "Desc"));
            
            assertThat(service.findById(quote.getId())).isPresent();
            assertThat(service.findById(999L)).isEmpty();
        }
        
        @Test
        @DisplayName("Should delete item by ID")
        void testDeleteById() {
            Quote quote = service.save(new Quote("To Delete", "Desc"));
            Long id = quote.getId();
            
            service.deleteById(id);
            
            assertThat(service.findById(id)).isEmpty();
        }
        
        @Test
        @DisplayName("Should throw exception when deleting non-existent item")
        void testDeleteNonExistent() {
            assertThatThrownBy(() -> service.deleteById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }
    
    @Nested
    @DisplayName("Collection Operations Tests")
    class CollectionTests {
        
        @BeforeEach
        void setUpTestData() {
            Quote quote1 = new Quote("Work Task 1", "Important work");
            quote1.setCategory("Work");
            quote1.setStatus(Quote.Status.ACTIVE);
            quote1.addTag("urgent");
            quote1.addTag("project-a");
            
            Quote quote2 = new Quote("Personal Task", "Personal stuff");
            quote2.setCategory("Personal");
            quote2.setStatus(Quote.Status.ACTIVE);
            quote2.addTag("home");
            
            Quote quote3 = new Quote("Work Task 2", "More work");
            quote3.setCategory("Work");
            quote3.setStatus(Quote.Status.INACTIVE);
            quote3.addTag("project-b");
            
            Quote quote4 = new Quote("Archived Task", "Old task");
            quote4.setCategory("Work");
            quote4.setStatus(Quote.Status.ARCHIVED);
            quote4.addTag("urgent");
            
            service.save(quote1);
            service.save(quote2);
            service.save(quote3);
            service.save(quote4);
        }
        
        @Test
        @DisplayName("Should group items by category")
        void testGroupByCategory() {
            Map<String, List<Quote>> grouped = service.groupByCategory();
            
            // This test will fail until students implement the method
            assertThat(grouped).hasSize(2);
            assertThat(grouped.get("Work")).hasSize(3);
            assertThat(grouped.get("Personal")).hasSize(1);
        }
        
        @Test
        @DisplayName("Should get all unique tags")
        void testGetAllUniqueTags() {
            Set<String> tags = service.getAllUniqueTags();
            
            // This test will fail until students implement the method
            assertThat(tags).hasSize(4);
            assertThat(tags).containsExactlyInAnyOrder(
                    "urgent", "project-a", "home", "project-b"
            );
        }
        
        @Test
        @DisplayName("Should count items by status")
        void testCountByStatus() {
            Map<Quote.Status, Long> counts = service.countByStatus();
            
            // This test will fail until students implement the method
            assertThat(counts).hasSize(3);
            assertThat(counts.get(Quote.Status.ACTIVE)).isEqualTo(2);
            assertThat(counts.get(Quote.Status.INACTIVE)).isEqualTo(1);
            assertThat(counts.get(Quote.Status.ARCHIVED)).isEqualTo(1);
        }
        
        @Test
        @DisplayName("Should find items with all specified tags")
        void testFindByAllTags() {
            Quote quote5 = new Quote("Multi-tag Task", "Has multiple tags");
            quote5.addTag("urgent");
            quote5.addTag("project-a");
            service.save(quote5);
            
            List<Quote> results = service.findByAllTags(Set.of("urgent", "project-a"));
            
            // This test will fail until students implement the method
            assertThat(results).hasSize(2);
            assertThat(results).extracting(Quote::getTitle)
                    .containsExactlyInAnyOrder("Work Task 1", "Multi-tag Task");
        }
        
        @Test
        @DisplayName("Should find items with any of specified tags")
        void testFindByAnyTag() {
            List<Quote> results = service.findByAnyTag(Set.of("home", "project-b"));
            
            // This test will fail until students implement the method
            assertThat(results).hasSize(2);
            assertThat(results).extracting(Quote::getTitle)
                    .containsExactlyInAnyOrder("Personal Task", "Work Task 2");
        }
        
        @Test
        @DisplayName("Should get most popular tags")
        void testGetMostPopularTags() {
            List<String> popular = service.getMostPopularTags(2);
            
            // This test will fail until students implement the method
            assertThat(popular).hasSize(2);
            assertThat(popular.get(0)).isEqualTo("urgent"); // appears twice
        }
        
        @Test
        @DisplayName("Should search items by query")
        void testSearch() {
            List<Quote> results = service.search("work");
            
            // This test will fail until students implement the method
            assertThat(results).hasSize(3);
            assertThat(results).extracting(Quote::getTitle)
                    .contains("Work Task 1", "Work Task 2");
        }
        
        @Test
        @DisplayName("Should archive inactive items")
        void testArchiveInactiveItems() {
            int archived = service.archiveInactiveItems();
            
            // This test will fail until students implement the method
            assertThat(archived).isEqualTo(1);
            
            List<Quote> inactiveQuotes = service.findByStatus(Quote.Status.INACTIVE);
            assertThat(inactiveQuotes).isEmpty();
            
            List<Quote> archivedQuotes = service.findByStatus(Quote.Status.ARCHIVED);
            assertThat(archivedQuotes).hasSize(2);
        }
    }
}