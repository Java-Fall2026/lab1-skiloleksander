package ua.rental.model;

import java.time.LocalDate;

import ua.rental.util.RentalUtils;

public record Payment(String paymentId, Rental rental, double amount, LocalDate paymentDate, PaymentMethod paymentMethod) {
    public Payment {
        RentalUtils.requireNotEmpty(paymentId, "payment id");
        RentalUtils.requireNotNull(rental, "rental");
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0, got: " + amount);
        }
        if (paymentDate == null || paymentDate.isBefore(rental.startDate())) {
            throw new IllegalArgumentException("Payment date cannot be null or before the rental start date, got: "
                + paymentDate);
        }
        RentalUtils.requireNotNull(paymentMethod, "payment method");
    }
}
