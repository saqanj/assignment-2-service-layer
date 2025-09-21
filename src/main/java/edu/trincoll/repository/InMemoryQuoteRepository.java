package edu.trincoll.repository;

import edu.trincoll.model.Quote;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * TODO: Rename this class to match your domain
 * 
 * In-memory implementation of the repository using Java collections.
 * Uses ConcurrentHashMap for thread-safety.
 */
@Repository
public class InMemoryQuoteRepository implements QuoteRepository {
    
    private final Map<Long, Quote> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Quote save(Quote entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Quote> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public List<Quote> findAll() {
        // TODO: Return defensive copy
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
    
    @Override
    public long count() {
        return storage.size();
    }
    
    @Override
    public void deleteAll() {
        storage.clear();
        idGenerator.set(1);
    }
    
    @Override
    public List<Quote> saveAll(List<Quote> entities) {
        return entities.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Quote> findByStatus(Quote.Status status) {
        // TODO: Implement using streams
        return storage.values().stream()
                .filter(item -> item.getStatus() == status)
                .collect(Collectors.toList());
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean equalsIgnoreCaseSafe(String a, String b) {
        return norm(a).equalsIgnoreCase(norm(b));
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        final String h = norm(haystack).toLowerCase(Locale.ROOT);
        final String n = norm(needle).toLowerCase(Locale.ROOT);
        return !n.isEmpty() && h.contains(n);
    }
    
    @Override
    public List<Quote> findByCategory(String category) {
        return storage.values().stream()
                .filter(Objects::nonNull)
                .filter(q -> equalsIgnoreCaseSafe(q.getCategory(), category))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Quote> findByTag(String tag) {
        final String target = norm(tag);
        if (target.isEmpty()) return Collections.emptyList();

        return storage.values().stream()
                .filter(Objects::nonNull)
                .filter(q -> {
                    Collection<String> tags = q.getTags();
                    if (tags == null || tags.isEmpty()) return false;
                    // match case-insensitively on exact tag OR substring (common UX)
                    // If you want exact-only, replace anyMatch with equalsIgnoreCase.
                    return tags.stream().filter(Objects::nonNull)
                            .anyMatch(t -> equalsIgnoreCaseSafe(t, target));
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Quote> findByTitleContaining(String searchTerm) {
        if (norm(searchTerm).isEmpty()) return Collections.emptyList();

        return storage.values().stream()
                .filter(Objects::nonNull)
                .filter(q -> containsIgnoreCase(q.getTitle(), searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Quote> findByAuthor(String author){
        if (norm(author).isEmpty()) return Collections.emptyList();

        return storage.values().stream()
                .filter(Objects::nonNull)
                // exact match; switch to containsIgnoreCase for partial author searches
                .filter(q -> equalsIgnoreCaseSafe(q.getAuthor(), author))
                .collect(Collectors.toList());
    }

    @Override
    public List<Quote> findBySource(String source){
        if (norm(source).isEmpty()) return Collections.emptyList();

        return storage.values().stream()
                .filter(Objects::nonNull)
                .filter(q -> equalsIgnoreCaseSafe(q.getSource(), source))
                .collect(Collectors.toList());
    }

    @Override
    public List<Quote> findByPublisher(String publisher){
        if (norm(publisher).isEmpty()) return Collections.emptyList();

        return storage.values().stream()
                .filter(Objects::nonNull)
                .filter(q -> equalsIgnoreCaseSafe(q.getPublisher(), publisher))
                .collect(Collectors.toList());
    }
}