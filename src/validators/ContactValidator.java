package validators;

// Classe responsável por validar um número de contato (telefone)
public class ContactValidator {

    // Metodo principal que valida o contato
    public boolean isValidContact(String contact) {

        if (contact == null){
            return false;
        }

        // verifica se tem 10 ou 11 dígitos
        if (contact.length() < 10 || contact.length() > 11) {
            return false;
        }

        // verifica se todos os números são iguais.
        if (contact.matches("(\\d)\\1+"))  {
            return false;
        }

        return true;
    }
}

