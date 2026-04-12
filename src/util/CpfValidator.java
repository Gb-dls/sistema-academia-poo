package util;

public class CpfValidator {

    public static boolean isValidCpf(String cpf) {
        if (!hasValidLength(cpf) || isRepeatedCpf(cpf)) {
            return false;
        }

        int firstDigit = calculateDigit(cpf, 9);
        int secondDigit = calculateDigit(cpf, 10);

        return firstDigit == (cpf.charAt(9) - '0') && secondDigit == (cpf.charAt(10) - '0');
    }

    private static boolean hasValidLength(String cpf) {
        return cpf != null && cpf.length() == 11;
    }

    private static boolean isRepeatedCpf(String cpf) {
        return cpf.matches("(\\d)\\1{10}");
    }

    private static int calculateDigit(String cpf, int length) {
        int sum = 0;
        int weight = length + 1;

        for (int i = 0; i < length; i++) {
            sum += (cpf.charAt(i) - '0') * (weight - i);
        }

        int digit = 11 - (sum % 11);
        return (digit >= 10) ? 0 : digit;
    }
}