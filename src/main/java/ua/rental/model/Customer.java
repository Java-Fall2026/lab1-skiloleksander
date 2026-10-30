package ua.rental.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import ua.rental.util.RentalUtils;

public record Customer(String driverLicense, String firstName, String lastName, LocalDate birthDate) {
    public Customer {
        RentalUtils.requireNotEmpty(driverLicense, "driver's license");
        RentalUtils.requireNotEmpty(firstName, "first name");
        RentalUtils.requireNotEmpty(lastName, "last name");
        if (birthDate == null || ChronoUnit.YEARS.between(birthDate, LocalDate.now()) < 18) {
            throw new IllegalArgumentException("Birth date cannot be null or age less than 18, got: " + birthDate);
        }
    }
}
