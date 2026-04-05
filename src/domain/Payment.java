package domain;

import java.time.LocalDate;

public class Payment {

    private LocalDate date;
    private double amount;
    private PaymentType type;
    private String description;

    // Enum Payment Type
    public enum PaymentType {
        PIX, CREDIT_CARD, DEBIT_CARD, CASH;
    }

    // Constructor
    public Payment(double amount, PaymentType type, String description) {
        this.date = LocalDate.now();
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
