package domain.payment;

public class CreditCardPayment extends Payment {

    private static final double fee_PERCENTAGE = 0.025;     // Taxa de processamento de 2.5%
    private String cardHolderName;     // Nome do titular do cartão
    private int installments;          // Número de parcelas
    private String lastCardNumbers;    // Últimos quatro dígitos do cartão

    // Construtor //
    /* Super para atribuir os campos necessários da superclasse e mapeamento dos dados do cartão */
    public CreditCardPayment(double amount, String cardHolderName, String lastCardNumbers, int installments) {
        super(amount);
        this.cardHolderName = cardHolderName;
        this.lastCardNumbers = lastCardNumbers;
        this.installments = installments;
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
        return String.format("Tipo: Crédito | Titular: %s | Cartão final: %s | Parcelas: %dx",
                this.cardHolderName,
                this.lastCardNumbers,
                this.installments);
    }

    /*
    @ getPaymentMethod
    @ Objetivo: Retornar o tipo de pagamento para exibir no relatorio financeiro
    */
    @Override
    public String getPaymentMethodName() { return "Credit Card"; }
}