package edu.trincoll.repository;

import edu.trincoll.model.Item;
import java.util.List;

/**
 * TODO: Rename this interface to match your domain
 * Done??
 * Examples: BookmarkRepository, QuoteRepository, etc.
 * 
 * Add domain-specific query methods that make sense for your use case.
 */
public interface QuoteRepository extends Repository<Item, Long> {
    
    /**
     * Find all items with a specific status
     */
    List<Item> findByStatus(Item.Status status);
    
    /**
     * Find all items in a category
     */
    List<Item> findByCategory(String category);
    
    /**
     * Find all items containing a specific tag
     */
    List<Item> findByTag(String tag);
    
    /**
     * Find items with title containing search term (case-insensitive)
     */
    List<Item> findByTitleContaining(String searchTerm);
    
    /**
     * TODO: Add at least 3 more domain-specific query methods
     * Done??
     * Examples:
     * - findByAuthor(String author) for quotes
     * - findByUrl(String url) for bookmarks  
     * - findOverdue() for habits
     * - findByIngredient(String ingredient) for recipes
     */
    List<Item> findByAuthor(String author);
    List<Item> findBySource(String source);
    List<Item> findByPublisher(String publisher);

}