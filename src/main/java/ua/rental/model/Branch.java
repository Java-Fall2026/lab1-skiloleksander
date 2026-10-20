package ua.rental.model;

import ua.rental.util.RentalUtils;

public record Branch(String name, String location) {
    public Branch {
        RentalUtils.requireNotEmpty(name, "branch name");
        RentalUtils.requireNotEmpty(location, "location name");
    }
}
