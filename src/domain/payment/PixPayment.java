package domain.payment;

public class PixPayment extends Payment {

    private String Key; // Chave PIX utilizada

    // Construtor //
    public PixPayment(double amount, String pixKey) {
        super(amount);
        this.Key = pixKey;
    }

    // Métodos obrigatórios da superclasse //

    /*
    @ getProcessingFee
    @ Objetivo: cálculo da taxa de processamento do pagamento - PIX geralmente não tem taxa
     */
    @Override
    public double getProcessingFee() {
        return 0.0;
    }

    /*
    @ getPaymentSummary
    @ Objetivo: Exibição de mensagem personalizada para pagamento via PIX
    */
    @Override
    public String getPaymentSummary() {
        return String.format("Tipo: PIX | Chave utilizada: %s", this.Key);
    }

    /*
    @ getPaymentMethod
    @ Objetivo: Retornar o tipo de pagamento para exibir no relatorio financeiro
    */
    @Override
    public String getPaymentMethodName() { return "Pix"; }

}