package exceptions;

public class PaymentValueMismatchException extends BusinessException {
    public PaymentValueMismatchException(double expected, double received) {
        super("Erro no pagamento: O valor digitado (R$ " + received + ") nao bate com o valor exigido pelo plano (R$ " + expected + ").");
    }
}
