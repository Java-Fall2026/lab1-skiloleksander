package ua.rental.model;

import java.util.Objects;

public class Car extends ua.common.BaseEntity {
    private String licensePlate;
    private String model;
    private int year;
    private int mileage;
    private String status;

    private Car(String licensePlate, String model, int year, int mileage, String status) {
        super();
        if (licensePlate == null || licensePlate.isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty, got: " + licensePlate);
        }
        if (model == null || model.isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty, got: " + model);
        }
        if (year < 1900 || year > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Year must be between 1900 and current year, got: " + year);
        }
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.model = model;
        this.year = year;
        setMileage(mileage);
        setStatus(status);
    }

    public static Car of(String licensePlate, String model, int year, int mileage, String status) {
        return new Car(licensePlate, model, year, mileage, status);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getMileage() {
        return mileage;
    }

    public String getStatus() {
        return status;
    }

    public final void setMileage(int mileage) {
        if (mileage < 0 || mileage > 2000000) {
            throw new IllegalArgumentException("Mileage must be between 0 and 2,000,000, got: " + mileage);
        }
        this.mileage = mileage;
    }

    public final void setStatus(String status) {
        status = status.trim().toUpperCase();
        String[] validStatuses = {"AVAILABLE", "RENTED", "MAINTENANCE", "RESERVED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status)) {
                this.status = status;
                return;
            }
        }
        throw new IllegalArgumentException("Valid statuses are: AVAILABLE, RENTED, MAINTENANCE, RESERVED. Got: "
            + status);
    }

    @Override 
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Car car)) return false;
        return licensePlate.equals(car.licensePlate);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(licensePlate);
    }

    @Override
    public String toString() {
        return "Class: " + getClass().getSimpleName() + "\nlicensePlate: " + licensePlate + "\nmodel: " + model
            + "\nyear: " + year + "\nmileage: " + mileage + "\nstatus: " + status + "\ncreatedAt: " + createdAt;
    }

}
