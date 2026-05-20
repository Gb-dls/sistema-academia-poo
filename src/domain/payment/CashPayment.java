package domain.payment;

public class CashPayment extends Payment {

    private double amountReceived; // Valor entregue em dinheiro

    // Construtor //
    /* Super para atribuir os campos necessários da superclasse */
    public CashPayment(double amount, double cashAmount) {
        super(amount);
        this.amountReceived = cashAmount;
    }

    // Métodos da subclasse //

    /*
    @ getChange
    @ Objetivo: calcular o troco do cliente
    */
    public double getChange() {
        return amountReceived - amount;
    }


    // Métodos da superclasse //

    /*
    @ getProcessingFee
    @ Objetivo: calcular a taxa de processamento do pagamento - Não há taxa para dinheiro
     */
    @Override
    public double getProcessingFee() {
        return 0.0;
    }

    /*
    @ getPaymentSummary
    @ Objetivo: Exibição de mensagem personalizada para pagamento em dinheiro
    */
    @Override
    public String getPaymentSummary() {
        return String.format("Tipo: Dinheiro | Valor Entregue: R$ %.2f | Troco: R$ %.2f",
                this.amountReceived,
                this.getChange());
    }
}
