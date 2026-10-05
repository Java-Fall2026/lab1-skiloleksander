package ua.rental.util;

class ValidationHelper {
    private ValidationHelper() {}

    public static void requireNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotEmpty(String str, String message) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requirePositive (int number, String message) {
        if (number <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireInRange(int number, int min, int max, String fieldName) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max + ", got: " + number);
        }
    }
}
