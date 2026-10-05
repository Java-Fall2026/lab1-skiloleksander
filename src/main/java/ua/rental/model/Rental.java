package ua.rental.model;

import java.time.LocalDate;
import java.util.Objects;

public class Rental extends ua.common.BaseEntity {
    private Car car;
    private Customer customer;
    private Branch branch;
    private LocalDate startDate;
    private LocalDate endDate;

    private Rental(Car car, Customer customer, Branch branch, LocalDate startDate, LocalDate endDate) {
        super();
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null, got: " + car);
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null, got: " + customer);
        }
        if (branch == null) {
            throw new IllegalArgumentException("Branch cannot be null, got: " + branch);
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date, got: " + startDate + " to "
            + endDate);
        }
        this.car = car;
        this.customer = customer;
        this.branch = branch;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Rental of(Car car, Customer customer, Branch branch, LocalDate startDate, LocalDate endDate) {
        return new Rental(car, customer, branch, startDate, endDate);
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Branch getBranch() {
        return branch;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rental rental)) return false;
        return car.equals(rental.car) && customer.equals(rental.customer) && branch.equals(rental.branch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(car, customer, branch);
    }

    @Override 
    public String toString() {
        return "Class: " + getClass().getSimpleName() + "\ncar: " + car.getLicensePlate() + "\ncustomer: "
        + customer.getDriverLicense() + "\nbranch: " + branch.getName() + "\nstartDate: " + startDate
        + "\nendDate: " + endDate + "\ncreatedAt: " + createdAt;
    }
}
