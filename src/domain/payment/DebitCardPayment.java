package domain.payment;

public class DebitCardPayment extends Payment {

    private String cardHolderName;     // Nome do titular
    private String lastCardNumbers;    // Últimos quatro dígitos do cartão

    // Construtor atualizado para receber os 3 parâmetros //
    public DebitCardPayment(double amount, String cardHolderName, String lastCardNumbers) {
        super(amount);
        this.cardHolderName = cardHolderName;
        this.lastCardNumbers = lastCardNumbers;
    }

    // Métodos obrigatórios da superclasse //

    /*
    @ getProcessingFee
    @ Objetivo: cálculo da taxa de processamento do pagamento - Não há taxa para pagamento no débito
     */
    @Override
    public double getProcessingFee() {
        return 0.0;
    }

    /*
    @ getPaymentSummary
    @ Objetivo: Exibição de mensagem personalizada para pagamento no débito
    */
    @Override
    public String getPaymentSummary() {
        return String.format("Tipo: Débito | Titular: %s | Cartão com final: %s",
                this.cardHolderName, this.lastCardNumbers);
    }

    /*
    @ getPaymentMethod
    @ Objetivo: Retornar o tipo de pagamento para exibir no relatorio financeiro
    */
    @Override
    public String getPaymentMethodName() { return "Debit Card"; }
}