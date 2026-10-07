package ua.rental.model;

import java.time.LocalDate;
import java.util.Objects;

import ua.rental.util.RentalUtils;

public class Payment extends ua.common.BaseEntity {
    private String paymentId;
    private Rental rental;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;

    public Payment(String paymentId, Rental rental, double amount, LocalDate paymentDate, String paymentMethod) {
        super();
        RentalUtils.requireNotEmpty(paymentId, "payment id");
        RentalUtils.requireNotNull(rental, "rental");
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0, got: " + amount);
        }
        if (paymentDate == null || paymentDate.isAfter(rental.getStartDate())) {
            throw new IllegalArgumentException("Payment date cannot be null or after the rental start date, got: "
                + paymentDate);
        }
        RentalUtils.requireNotEmpty(paymentMethod, "payment method");
        paymentMethod = RentalUtils.normalize(paymentMethod);
        RentalUtils.validateMethod(paymentMethod);
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
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Payment payment = (Payment) o;
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
            + rental.getBranch().getName() + "\namount: " + RentalUtils.formatMoney(amount) + "\npaymentDate: " + paymentDate
            + "\npaymentMethod: " + paymentMethod + "\ncreatedAt: " + createdAt;
    }
}
