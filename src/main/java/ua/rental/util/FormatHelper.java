package ua.rental.util;

class FormatHelper {
    private FormatHelper() {}

    public static String normalize(String str) {
        if (str == null) {
            return null;
        }
        return str.trim().toUpperCase();
    }

    public static String capitalize(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        return str.trim().substring(0, 1).toUpperCase() + str.trim().substring(1).toLowerCase();
    }

    public static String formatMoney(double amount) {
        return String.format("₴%.2f", amount);
    }
}
