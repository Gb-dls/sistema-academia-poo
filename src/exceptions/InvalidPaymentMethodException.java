package exceptions;

public class InvalidPaymentMethodException extends ValidationException {

    public InvalidPaymentMethodException(String metodo) {
        super("Opção de pagamento inválida: O método '" + metodo + "' não é aceito pelo sistema. Escolha uma das opções disponíveis (1 a 4).");
    }
}