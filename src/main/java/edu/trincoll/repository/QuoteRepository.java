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
     * Find all quotes with a specific status
     */
    List<Quote> findByStatus(Quote.Status status);
    
    /**
     * Find all quotes in a category
     */
    List<Quote> findByCategory(String category);
    
    /**
     * Find all quotes containing a specific tag
     */
    List<Quote> findByTag(String tag);
    
    /**
     * Find quotes with title containing search term (case-insensitive)
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

    /**
     * Find quotes by a certain author
     */
    List<Quote> findByAuthor(String author);

    /**
     * Find quotes from a specific source
     */
    List<Quote> findBySource(String source);

    /**
     * Find quotes from a specific publisher
     */
    List<Quote> findByPublisher(String publisher);

}