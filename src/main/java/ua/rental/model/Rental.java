package ua.rental.model;

import java.time.LocalDate;

import ua.rental.util.RentalUtils;

public record Rental(Car car, Customer customer, Branch branch, LocalDate startDate, LocalDate endDate) {
    public Rental {
        RentalUtils.requireNotNull(car, "car");
        RentalUtils.requireNotNull(customer, "customer");
        RentalUtils.requireNotNull(branch, "branch");
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                "Both dates cannot be null or start date must be before end date, got: " + startDate + " to "
                + endDate);
        }
    }

    public static Rental of(Car car, Customer customer, Branch branch, LocalDate startDate, LocalDate endDate) {
        return new Rental(car, customer, branch, startDate, endDate);
    }
}
