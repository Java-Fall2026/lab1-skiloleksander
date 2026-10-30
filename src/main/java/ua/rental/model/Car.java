package ua.rental.model;

import java.util.Objects;

import ua.rental.util.RentalUtils;

public class Car {
    private String licensePlate;
    private String model;
    private int year;
    private int mileage;
    private CarStatus status;

    private Car(String licensePlate, String model, int year, int mileage, CarStatus status) {
        super();
        RentalUtils.requireNotEmpty(licensePlate, "license plate");
        RentalUtils.requireNotEmpty(model, "model");
        RentalUtils.requireInRange(year, 1900, java.time.Year.now().getValue(), "year");
        this.licensePlate = RentalUtils.normalize(licensePlate);
        this.model = model;
        this.year = year;
        setMileage(mileage);
        setStatus(status);
    }

    public static Car of(String licensePlate, String model, int year, int mileage, CarStatus status) {
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

    public CarStatus getStatus() {
        return status;
    }

    public final void setMileage(int mileage) {
        RentalUtils.requireInRange(mileage, 0, 2000000, "mileage");
        this.mileage = mileage;
    }

    public final void setStatus(CarStatus status) {
        RentalUtils.requireNotNull(status, "status");
        this.status = status;
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Car car = (Car) o;
        return licensePlate.equals(car.licensePlate);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(licensePlate);
    }

    @Override
    public String toString() {
        return "Class: " + getClass().getSimpleName() + "\nlicensePlate: " + licensePlate + "\nmodel: " + model
            + "\nyear: " + year + "\nmileage: " + mileage + "\nstatus: " + status;
    }

}
