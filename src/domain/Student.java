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

    // CONSTRUTOR
    public Student(String name, String cpf, String contact, String email, LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.contact = contact;
        this.email = email;
        this.birthDate = birthDate;
        this.active = true;
    }

    // ================= FORMATADORES =================
    //Formata a saida do CPF
    public String getCpfFormatted() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    //Formata a saida do contato
    public String getContactFormatted() {


        if (contact.length() == 11) {
            return contact.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else {
            return contact.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
    }

    //Formata a data de aniversario
    public String getBirthDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return birthDate.format(formatter);
    }

    //Retorna a situação do aluno
    public String getStatus() {
        if (active) {
            return "Ativo";
        }
        return "Inativo";
    }

    //Calcula a idade do estudante
    public int calculateAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }
    // ================= STATUS =================

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    // ================= GETTERS =================

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

    // ================= PRINT =================
    @Override
    public String toString() {
        return "Nome: " + name + "\n" +
                "CPF: " + getCpfFormatted() + "\n" +
                "Email: " + email + "\n" +
                "Contato: " + getContactFormatted() + "\n" +
                "Nascimento: " + getBirthDateFormatted() + "\n" +
                "Idade: " + calculateAge() + "\n" +
                "Status: " + getStatus() + "\n";
    }

    // ================= SETTERS =================

    //Altera o nome do estudante
    public void setName(String name) {
        this.name = name;

    }

    //Altera o contato
    public void setContact(String contact) {
        this.contact = contact;
    }

    //Altera o email do estudante
    public void setEmail(String email) {

        this.email = email;

    }

    //Altera o dia do nascimento do estudante
    public void setBirthDate(LocalDate birthDate) {

        this.birthDate = birthDate;

    }

}