package ua.rental.util;

import ua.rental.model.Rental;
import ua.rental.model.Car;
import ua.rental.model.CarStatus;
import ua.rental.model.PaymentMethod;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalUtils {
    private RentalUtils() {}

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

    public static String normalize(String str) {
        return FormatHelper.normalize(str);
    }

    public static String capitalize(String str) {
        return FormatHelper.capitalize(str);
    }

    public static String formatMoney(double amount) {
        return FormatHelper.formatMoney(amount);
    } 

    public static int rentalDays(Rental rental) {
        ValidationHelper.requireNotNull(rental, "rental");
        int result = (int) ChronoUnit.DAYS.between(rental.startDate(), rental.endDate());
        ValidationHelper.requirePositive(result, "rental days");
        return result;
    }

    public static int carAgeYears(Car car) {
        ValidationHelper.requireNotNull(car, "car");
        int result = LocalDate.now().getYear() - car.getYear();
        ValidationHelper.requirePositive(result, "age of car");
        return result;
    }

    public static String getCarStatusAction(CarStatus status) {
        ValidationHelper.requireNotNull(status, "car status");
        return switch (status) {
            case AVAILABLE -> "Авто готове до видачі клієнту";
            case RENTED -> "Авто в поїздці, очікується на повернення";
            case MAINTENANCE -> "Авто на ТО або ремонті";
            case RESERVED -> "Авто заброньовано, очікує клієнта";
        };
    }

    public static double calculateFee(PaymentMethod method, double amount) {
        ValidationHelper.requireNotNull(method, "payment method");
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative, got: " + amount);
        }
        return switch (method) {
            case CASH -> 0.0;
            case DEBIT_CARD, CREDIT_CARD -> amount * (method.getFeePercent() / 100.0);
            case ONLINE -> amount * (method.getFeePercent() / 100.0) + 5.0;
        };
    }
}
