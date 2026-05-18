package domain.payment;

public class CreditCardPayment extends Payment {

    private static final double fee_PERCENTAGE = 0.025;     // Taxa de processamento de 2.5%
    private int installments;   // Número de parcelas
    private String lastCardNumbers;    // Últimos quatro dígitos do cartão


    // Construtor //
    /* Super para atribuir os campos necessários da superclasse */
    public CreditCardPayment(double amount, int installments, String lastCardNumbers) {
        super(amount);
        this.installments = installments;
        this.lastCardNumbers = lastCardNumbers;
    }

    // Métodos obrigatórios da superclasse //

    /*
    @ getProcessingFee
    @ Objetivo: cálculo da taxa de processamento do pagamento - Taxa para pagamento no crédito
     */
    @Override
    public double getProcessingFee() {
        double afterTaxValue = this.amount / (1 + fee_PERCENTAGE);
        return this.amount - afterTaxValue;
    }

    /*
    @ getPaymentSummary
    @ Objetivo: Exibição de mensagem personalizada para pagamento no crédito
    */
    @Override
    public String getPaymentSummary() {
        return String.format("Tipo: Crédito | Cartão final: %s | Parcelas: %dx",
                this.lastCardNumbers,
                this.installments);
    }
}
