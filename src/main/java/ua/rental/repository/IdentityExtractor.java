package ua.rental.repository;

@FunctionalInterface 
public interface IdentityExtractor<T> {
    String identityOf(T item);
}
