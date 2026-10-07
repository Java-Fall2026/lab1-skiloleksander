package ua.rental.util;

import ua.rental.model.Rental;
import ua.rental.model.Car;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalUtils {
    private RentalUtils() {}

    static final String[] VALID_STATUSES = {"AVAILABLE", "RENTED", "MAINTENANCE", "RESERVED"};
    static final String[] VALID_METHODS = {"CREDIT_CARD", "DEBIT_CARD", "CASH", "ONLINE"};

    public static void requireNotNull(Object obj, String fieldName) {
        ValidationHelper.requireNotNull(obj, fieldName);
    }

    public static void requireNotEmpty(String str, String fieldName) {
        ValidationHelper.requireNotEmpty(str, fieldName);
    }

    public static void requirePositive (int number, String fieldName) {
        ValidationHelper.requirePositive(number, fieldName);
    }

    public static void requireInRange(int number, int min, int max, String fieldName) {
        ValidationHelper.requireInRange(number, min, max, fieldName);
    }

    public static void validateStatus(String status) {
        ValidationHelper.requireInList(status, VALID_STATUSES, "statuses");
    }

    public static void validateMethod(String method) {
        ValidationHelper.requireInList(method, VALID_METHODS, "methods");
    }

    public static String normalize(String str) {
        return FormatHelper.normalize(str);
    }

    public static String capitalize(String str) {
       return FormatHelper.normalize(str);
    }

    public static String formatMoney(double amount) {
        return FormatHelper.formatMoney(amount);
    } 

    public static int rentalDays(Rental rental) {
        ValidationHelper.requireNotNull(rental, "rental");
        int result = (int) ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
        ValidationHelper.requirePositive(result, "rental days");
        return result;
    }

    public static int carAgeYears(Car car) {
        ValidationHelper.requireNotNull(car, "car");
        int result = LocalDate.now().getYear() - car.getYear();
        ValidationHelper.requirePositive(result, "age of car");
        return result;
    }
}
