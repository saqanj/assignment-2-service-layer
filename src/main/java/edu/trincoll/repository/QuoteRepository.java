package edu.trincoll.repository;

import edu.trincoll.model.Quote;

import java.util.List;

/**
 * TODO: Rename this interface to match your domain
 * Done??
 * Examples: BookmarkRepository, QuoteRepository, etc.
 * 
 * Add domain-specific query methods that make sense for your use case.
 */
public interface QuoteRepository extends Repository<Quote, Long> {
    
    /**
     * Find all items with a specific status
     */
    List<Quote> findByStatus(Quote.Status status);
    
    /**
     * Find all items in a category
     */
    List<Quote> findByCategory(String category);
    
    /**
     * Find all items containing a specific tag
     */
    List<Quote> findByTag(String tag);
    
    /**
     * Find items with title containing search term (case-insensitive)
     */
    List<Quote> findByTitleContaining(String searchTerm);
    
    /**
     * TODO: Add at least 3 more domain-specific query methods
     * Done??
     * Examples:
     * - findByAuthor(String author) for quotes
     * - findByUrl(String url) for bookmarks  
     * - findOverdue() for habits
     * - findByIngredient(String ingredient) for recipes
     */
    List<Quote> findByAuthor(String author);
    List<Quote> findBySource(String source);
    List<Quote> findByPublisher(String publisher);

}