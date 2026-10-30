package ua.rental.model;

public enum PaymentMethod {
    CREDIT_CARD(1.5),
    DEBIT_CARD(0.5),
    CASH(0.0),
    ONLINE(2.0);

    private final double feePercent;

    PaymentMethod(double feePercent) {
        this.feePercent = feePercent;
    }

    public double getFeePercent() {
        return feePercent;
    }
}
