package domain;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Student {
    private String name;
    private String cpf;
    private String contact;
    private String email;
    private LocalDate birthDate;
    private boolean active;

    //CONSTRUTOR

    public Student(String name, String cpf, String contact, String email, LocalDate birthDate) {

        //limpa dados
        cpf = cpf.replaceAll("\\D", ""); //remove tudo que não for numero
        contact = contact.replaceAll("\\D", ""); // remove tudo que não for numero

        //validaçoes
        if (!isValidCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido!");
        }
        if (!isValidContact(contact)) {
            throw new IllegalArgumentException("Telefone inválido!");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento inválida!");
        }

        //atribuiçoes
        this.name = name;
        this.cpf = cpf;
        this.contact = contact;
        this.email = email;
        this.birthDate = birthDate;
        this.active = true;
    }

    //VALIDAÇÕES

    // metodo que valida o CPF
    private boolean isValidCpf(String cpf) {
        int sum;
        // verifica se tem exatamente 11 digitos
        if (cpf.length() != 11) {
            return false;
        }

        // verifica se todos os numeros sao iguais, se for é um CPF invalido
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        sum = 0;
        for (int i = 0; i < 9; i++) {
            // pega o caractere e converte para número usando - '0'
            sum = sum +  (cpf.charAt(i) - '0') * (10 - i); //pega o numero do CPF e multiplica pelo peso decrescente
        }

        // calcula o primeiro digito verificador
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {    // regra do CPF: se resultado for 10 ou 11 vira 0
            firstDigit = 0;
        }

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum = sum + (cpf.charAt(i) - '0') * (11 - i);
        }

        // calcula o segundo dígito verificador
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {  // regra do CPF: se resultado for 10 ou 11 vira 0
            secondDigit = 0;
        }

        if (firstDigit == (cpf.charAt(9) - '0') && secondDigit == (cpf.charAt(10) - '0')) { // compara os digitos calculados com os digitos reais do CPF
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidContact(String contact) {

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


    // FORMATADORES

    // Metodo que formata o CPF para print
    public String getCpfFormatted() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})","$1.$2.$3-$4");
    }

    // Metodo que formata o telefone para print
    public String getContactFormatted() {
        if (contact.length() == 11) {
            return contact.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else {
            return contact.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
    }

    // Metodo que formata o data para print
    public String getBirthDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return birthDate.format(formatter);
    }

    //metodo que define o status
    public String getStatus() {
        return active ? "Ativo" : "Inativo";
    }

    // calcula a idade do aluno
    public int calculateAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }

    // aluno ativo
    public void activate() {
        this.active = true;
    }

    //aluno inativo
    public void deactivate() {
        this.active = false;
    }


    // GETTERS
    public String getName() {
        return this.name;
    }

    public String getCpf() {
        return this.cpf;
    }

    public String getContact() {
        return this.contact;
    }

    public String getEmail() {
        return this.email;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public boolean isActive() {
        return this.active;
    }

    //PRINT

    // metodo para printar todos os dados so aluno
    public void printStudent() {
        System.out.println("===== DADOS DO ALUNO =====");
        System.out.println("Nome: " + name);
        System.out.println("CPF: " + getCpfFormatted());
        System.out.println("Contato: " + getContactFormatted());
        System.out.println("Email: " + email);
        System.out.println("Data de nascimento: " + getBirthDateFormatted());
        System.out.println("Idade: " + calculateAge() + " anos");
        //  imprime o status do aluno (ativo ou inativo), chama o metodo que retorna true ou false para a situacao do aluno e faz a comparacao direta
        System.out.println("Status: " + getStatus());
        System.out.println("==========================");
    }

}
