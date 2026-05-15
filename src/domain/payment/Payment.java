package domain.payment;

import java.time.LocalDate;

public abstract class Payment {

    protected LocalDate date;
    protected double amount;

    // Construtor //
    public Payment(double amount) {
        this.date = LocalDate.now();
        this.amount = amount;
    }

    // Getters //
    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }


    // Métodos abstratos //

    /* Contrato Com Subclasses - Calcula a Taxa de processamento do tipo de pagamento */
    public abstract double getProcessingFee();

    /* Contrato Com Subclasses - Produz uma representação textual relevante para o tipo de pagamento */
    public abstract String getPaymentSummary();

    // toString Payment //
    /* Exibição de detalhes do pagamento */
    @Override
    public String toString() {
        return String.format("Data: %s | Valor: R$ %.2f | Taxa: R$ %.2f | %s",
                this.date,
                this.amount,
                this.getProcessingFee(),
                this.getPaymentSummary());
    }
}
