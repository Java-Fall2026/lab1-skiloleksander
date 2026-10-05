package ua.rental.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Customer extends ua.common.BaseEntity {
    private String driverLicense;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    public Customer(String driverLicense, String firstName, String lastName, LocalDate birthDate) {
        super();
        if (driverLicense == null || driverLicense.isEmpty()) {
            throw new IllegalArgumentException("Driver license cannot be null or empty, got: " + driverLicense);
        }
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty, got: " + firstName);
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty, got: " + lastName);
        }
        if (birthDate == null || Period.between(birthDate, LocalDate.now()).getYears() < 18) {
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
        if (!(o instanceof Customer customer)) return false;
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
