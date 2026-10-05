package ua.rental.model;

import java.time.LocalDate;
import java.util.Objects;

public class Payment extends ua.common.BaseEntity {
    private String paymentId;
    private Rental rental;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;

    public Payment(String paymentId, Rental rental, double amount, LocalDate paymentDate, String paymentMethod) {
        super();
        if (paymentId == null || paymentId.isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be null or empty, got: " + paymentId);
        }
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null, got: " + rental);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0, got: " + amount);
        }
        if (paymentDate == null || paymentDate.isAfter(rental.getStartDate())) {
            throw new IllegalArgumentException("Payment date cannot be null or after the rental start date, got: "
                + paymentDate);
        }
        paymentMethod = paymentMethod.trim().toUpperCase();
        String[] validMethods = {"CREDIT_CARD", "DEBIT_CARD", "CASH", "ONLINE"};
        boolean isValidMethod = false;
        for (String method : validMethods) {
            if (method.equals(paymentMethod)) {
                isValidMethod = true;
                break;
            }
        }
        if (!isValidMethod) {
            throw new IllegalArgumentException("Valid methods are: CREDIT_CARD, DEBIT_CARD, CASH, ONLINE. Got: "
                + paymentMethod);
        }
        this.paymentId = paymentId;
        this.rental = rental;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentId() {
        return paymentId;
    }

    public Rental getRental() {
        return rental;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override 
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Payment payment)) return false;
        return paymentId.equals(payment.paymentId);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override 
    public String toString() {
        return "Class: " + getClass().getSimpleName() + "\npaymentId: " + paymentId + "\nrental: "
            + rental.getCar().getLicensePlate() + ", " + rental.getCustomer().getDriverLicense() + ", "
            + rental.getBranch().getName() + "\namount: " + amount + "\npaymentDate: " + paymentDate
            + "\npaymentMethod: " + paymentMethod + "\ncreatedAt: " + createdAt;
    }
}
