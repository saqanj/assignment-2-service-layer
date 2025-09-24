package edu.trincoll.repository;

import edu.trincoll.model.Quote;

import java.util.List;

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