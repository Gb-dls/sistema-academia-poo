package domain;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import util.CpfValidator;
import util.ContactValidator;

public class Student {

    private String name;
    private String cpf;
    private String contact;
    private String email;
    private LocalDate birthDate;
    private boolean active;

    // CONSTRUTOR
    public Student(String name, String cpf, String contact, String email, LocalDate birthDate) {

        // valida nome
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome inválido!");
        }

        // valida CPF (formato básico)
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF inválido!");
        }

        // limpa CPF
        cpf = cpf.replaceAll("\\D", "");

        if (!CpfValidator.isValidCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido!");
        }

        // valida data
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento inválida!");
        }

        // limpa telefone
        String cleanedContact = null;

        if (contact != null) {
            cleanedContact = contact.replaceAll("\\D", "");
        }

        // valida telefone
        if (cleanedContact != null && !ContactValidator.isValidContact(cleanedContact)) {
            throw new IllegalArgumentException("Telefone inválido!");
        }

        // atribuições
        this.name = name;
        this.cpf = cpf;
        this.contact = cleanedContact;
        this.email = email;
        this.birthDate = birthDate;
        this.active = true;
    }

    // ================= FORMATADORES =================

    public String getCpfFormatted() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public String getContactFormatted() {
        if (contact == null) {
            return "Não informado";
        }

        if (contact.length() == 11) {
            return contact.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else {
            return contact.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
    }

    public String getBirthDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return birthDate.format(formatter);
    }

    public String getStatus() {
        if (active) {
            return "Ativo";
        }
        return "Inativo";
    }

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

    public boolean isActive() {
        return this.active;
    }

    // ================= PRINT =================

    public void printStudent() {
        System.out.println("===== DADOS DO ALUNO =====");
        System.out.println("Nome: " + name);
        System.out.println("CPF: " + getCpfFormatted());
        System.out.println("Contato: " + getContactFormatted());
        System.out.println("Email: " + email);
        System.out.println("Data de nascimento: " + getBirthDateFormatted());
        System.out.println("Idade: " + calculateAge() + " anos");
        System.out.println("Status: " + getStatus());
        System.out.println("==========================");
    }

    // ================= SETTERS =================

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome inválido!");
        }
        this.name = name;
    }

    public void setContact(String contact) {
        if (contact == null) {
            this.contact = null;
            return;
        }

        String cleanedContact = contact.replaceAll("\\D", "");

        if (!ContactValidator.isValidContact(cleanedContact)) {
            throw new IllegalArgumentException("Telefone inválido!");
        }

        this.contact = cleanedContact;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email inválido!");
        }
        this.email = email;
    }

    public void setBirthDate(LocalDate birthDate) {
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento inválida!");
        }
        this.birthDate = birthDate;
    }
}