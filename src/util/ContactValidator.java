package util;

public class ContactValidator {

    public static boolean isValidContact(String contact) {

        if (contact == null) return false;

        // remove tudo que não for número
        contact = contact.replaceAll("\\D", "");

        // verifica se tem 10 ou 11 dígitos
        if (contact.length() < 10 || contact.length() > 11) {
            return false;
        }

        // verifica se todos os números são iguais
        if (contact.matches("(\\d)\\1+")) {
            return false;
        }

        return true;
    }
}