package ua.rental.util;

class ValidationHelper {
    private ValidationHelper() {}

    public static void requireNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(FormatHelper.capitalize(fieldName) + " cannot be null, got: " + obj);
        }
    }

    public static void requireNotEmpty(String str, String fieldName) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException(FormatHelper.capitalize(fieldName) + " cannot be null or empty, got: "
                + str);
        }
    }

    public static void requirePositive (int number, String fieldName) {
        if (number < 0) {
            throw new IllegalArgumentException(FormatHelper.capitalize(fieldName) + " must be positive, got: "
                + number);
        }
    }

    public static void requireInRange(int number, int min, int max, String fieldName) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(FormatHelper.capitalize(fieldName) + " must be between " + min
                + " and " + max + ", got: " + number);
        }
    }

    public static void requireInList(String str, String[] list, String fieldName) {
        for (String valid : list) {
            if (valid.equals(str)) {
                return;
            }
        }
        String listStr = "";
        for (String el : list) {
            if (el.equals(list[list.length - 1])) {
                listStr += el + ".";
            }
            else {
                listStr += el + ", ";
            }
        }
        throw new IllegalArgumentException("Valid " + fieldName.trim().toLowerCase() + " are: " + listStr + " Got: "
            + str);
    }
}
