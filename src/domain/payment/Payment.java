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
        // Formata os números separados e já troca o ponto por vírgula
        String formattedAmount = String.format("%.2f", this.amount).replace(".", ",");
        String formattedFee = String.format("%.2f", this.getProcessingFee()).replace(".", ",");

        return String.format("Data: %s | Valor: R$ %s | Taxa: R$ %s | %s",
                this.date,
                formattedAmount,
                formattedFee,
                this.getPaymentSummary());
    }
}
