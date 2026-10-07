package ua.rental.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import ua.rental.util.RentalUtils;

public class Customer extends ua.common.BaseEntity {
    private String driverLicense;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    public Customer(String driverLicense, String firstName, String lastName, LocalDate birthDate) {
        super();
        RentalUtils.requireNotEmpty(firstName, "first name");
        RentalUtils.requireNotEmpty(lastName, "last name");
        if (birthDate == null || ChronoUnit.YEARS.between(birthDate, LocalDate.now()) < 18) {
            throw new IllegalArgumentException("Birth date cannot be null or age less than 18, got: " + birthDate);
        }
        this.driverLicense = driverLicense;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Customer customer = (Customer) o;
        return driverLicense.equals(customer.driverLicense);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(driverLicense);
    }

    @Override
    public String toString() {
        return "Class: " + getClass().getSimpleName() + "\ndriverLicense: " + driverLicense + "\nfirstName: "
            + firstName + "\nlastName: " + lastName + "\nlocalDate" + birthDate + "\ncreatedAt: " + createdAt;
    }
}
