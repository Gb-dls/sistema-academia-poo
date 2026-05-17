package domain.payment;

public class DebitCardPayment extends Payment {

    private String lastCardNumbers;    // Últimos quatro dígitos do cartão

    // Construtor //
    /* Super para atribuir os campos necessários da superclasse */
    public DebitCardPayment(double amount, String lastCardNumbers) {
        super(amount);
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
        return String.format("Tipo: Débito | Cartão com final: %s",
                this.lastCardNumbers);
    }
    }
}
