package ua.rental.repository;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

import ua.rental.exception.DuplicateIdentityException;
import ua.rental.util.RentalUtils;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericRepository<T> {
    private final String name;
    private final IdentityExtractor<T> extractor;
    private final Map<String, T> storage;

    private static final Logger log = LoggerFactory.getLogger(GenericRepository.class);

    public GenericRepository(String name, IdentityExtractor<T> extractor) {
        RentalUtils.requireNotNull(extractor, "extractor");
        RentalUtils.requireNotEmpty(name, "name");
        this.name = name;
        this.extractor = extractor;
        this.storage = new LinkedHashMap<>();
    }

    private String getIdentity(T item) {
        RentalUtils.requireNotNull(item, "item");
        String identity = extractor.identityOf(item);
        RentalUtils.requireNotEmpty(identity, "identity");
        return identity;
    }

    public void add(T item) {
        String identity = getIdentity(item);
        if (storage.containsKey(identity)) {
            log.error("Duplicate identity '{}' in repository '{}'", identity, name);
            throw new DuplicateIdentityException("Duplicate identity " + identity + " in repository " + name);
        }
        storage.put(identity, item);
        log.info("Item '{}' is added successfully to repository '{}'", identity, name);
    }

    public boolean addIfAbsent(T item) {
        String identity = getIdentity(item);
        if (storage.containsKey(identity)) {
            log.warn("Skipping duplicate of '{}' in repository '{}'", identity, name);
            return false;
        }
        storage.put(identity, item);
        log.info("Item '{}' is added successfully to repository '{}'", identity, name);
        return true;
    }

    public void addAll(Collection<? extends T> items) {
        RentalUtils.requireNotNull(items, "items");
        for (T item : items) {
            addIfAbsent(item);
        }
    }

    public boolean removeByIdentity(String identity) {
        if (storage.remove(identity) != null) {
            log.info("Item '{}' is removed successfully from repository '{}'", identity, name);
            return true;
        }
        log.warn("Item '{}' is not found in repository '{}'", identity, name);
        return false;
    }

    public Optional<T> findByIdentity(String identity) {
        T item = storage.get(identity);
        if (item != null) {
            log.info("Item '{}' is found successfully in repository '{}'", identity, name);
        } else {
            log.warn("Item '{}' is not found in repository '{}'", identity, name);
        }
        return Optional.ofNullable(item);
    }

    public List<T> findAll() {
        return List.copyOf(storage.values());
    }

    public int size() {
        return storage.size();
    }
}
