package validators;

// Classe responsável por validar um CPF
public class CpfValidator {

    // Método principal que valida o CPF completo, verifica se tem 11 dígitos ou se não é um CPF com todos os números iguais
    public boolean isValidCpf(String cpf) {

        //verifica se o CPF tem exatamente 11 dígitos e se não é uma sequencia de numeros iguais
        if (!hasValidLength(cpf) || isRepeatedCpf(cpf)) {
            return false;
        }

        // Calcula os dígitos verificadores
        int firstDigit = calculateDigit(cpf, 9);
        int secondDigit = calculateDigit(cpf, 10);

        // Compara com os digitos informados no CPF, cpf.charAt(9) → 10º dígito e o cpf.charAt(10) → 11º dígito
        return firstDigit == (cpf.charAt(9) - '0') && secondDigit == (cpf.charAt(10) - '0');
    }

    // Verifica se o CPF tem exatamente 11 caracteres
    private  boolean hasValidLength(String cpf) {
        return cpf != null && cpf.length() == 11;
    }

    // Verifica se todos os números do CPF são iguais
    private boolean isRepeatedCpf(String cpf) {
        return cpf.matches("(\\d)\\1{10}");
    }

    // Método que calcula os digitos verificadores do CPF
    private  int calculateDigit(String cpf, int length) {
        int sum = 0;
        int weight = length + 1;  // Peso começa em length + 1

        // Percorre os dígitos do CPF

        for (int i = 0; i < length; i++) {
            sum += (cpf.charAt(i) - '0') * (weight - i); // Converte char para número e multiplica pelo peso do descendente
        }

        // Regra oficial do CPF, se o resultado for 10 ou 11, o dígito vira 0
        int digit = 11 - (sum % 11);
        if (digit >= 10) {
            return 0;
        } else {
            return digit;
        }
    }
}