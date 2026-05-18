package domain.payment;

public class PixPayment extends Payment {

    private String key;     // Chave Pix
    private String transationId;    // Id de transação do pix

    // Construtor //
    /* Super para atribuir os campos necessários da superclasse */
    public PixPayment(double amount, String key, String transationId) {
        super(amount);
        this.key = key;
        this.transationId = transationId;
    }

    // Métodos obrigatórios da Superclasse //

    /*
    @ getProcessingFee
    @ Objetivo: cálculo da taxa de processamento do pagamento - Não há taxa para pagamento no Pix
    */
    @Override
    public double getProcessingFee() {
        return 0.0;
    }

    /*
    @ getPaymentSummary
    @ Objetivo: Exibição de mensagem personalizada para pagamento no Pix
    */
    @Override
    public String getPaymentSummary() {
        return String.format("Tipo: PIX | Chave destino: %s | Comprovante: %s",
                this.key,
                this.transationId);
    }
}
