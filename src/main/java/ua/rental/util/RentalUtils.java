package ua.rental.util;

import ua.rental.model.Rental;
import ua.rental.model.Car;

import java.time.LocalDate;
import java.time.Period;

public class RentalUtils {
    private RentalUtils() {}

    public static int rentalDays(Rental rental) {
        ValidationHelper.requireNotNull(rental, "Rental cannot be null");
        int result = Period.between(rental.getStartDate(), rental.getEndDate()).getDays();
        ValidationHelper.requirePositive(result, "End date cannot be before start date");
        return result;
    }

    public static int carAgeYears(Car car) {
        ValidationHelper.requireNotNull(car, "Car cannot be null");
        int result = LocalDate.now().getYear() - car.getCreatedAt().getYear();
        if (result < 0) throw new IllegalArgumentException("Age of car cannot be less than 0");
        return result;
    }
}
