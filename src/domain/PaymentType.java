package domain;

public enum PaymentType {
    PIX(1), CREDIT_CARD(2), DEBIT_CARD(3), CASH(4);

    private final int value;

    PaymentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PaymentType fromInt(int option) {
        for (PaymentType type : PaymentType.values()) {
            if (type.getValue() == option) {
                return type;
            }
        }
        return null;
    }
}
