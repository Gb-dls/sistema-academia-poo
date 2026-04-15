package domain;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;


// Classe que representa a entidade Aluno no sistema
public class Student {

    // ================= ATRIBUTOS =================
    private String name;                // Nome do aluno
    private String cpf;                 // CPF
    private String contact;             // Telefone/contato
    private String email;               // Email do aluno
    private LocalDate birthDate;         // Data de nascimento
    private boolean active;             // Status do aluno (ativo/inativo)

    // ================= CONSTRUTOR =================
    // Inicializa um aluno com os dados informados e por padrão, todo aluno já nasce ativo
    public Student(String name, String cpf, String contact, String email, LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.contact = contact;
        this.email = email;
        this.birthDate = birthDate;
        this.active = true;
    }

    // ================= FORMATADORES =================

    //Formata a saida do CPF  no padrão xxx.xxx.xxx-xx
    public String getCpfFormatted() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    //Formata a saida do contato no padrão (xx) xxxx-xxxx ou (xx) xxxxx-xxxx
    public String getContactFormatted() {
        if (contact.length() == 11) {
            return contact.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else {
            return contact.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
    }

    //Formata a data de aniversario para dd/MM/yyyy
    public String getBirthDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return birthDate.format(formatter);
    }

    // Retorna o status do aluno
    public String getStatus() {
        if (active) {
            return "Ativo";
        }
        return "Inativo";
    }

    // Retorna se o aluno está ativo ou não
    public boolean isActive() {
        return active;
    }

    //Calcula a idade do estudante com base na data de nascimento
    public int calculateAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }


    // ================= STATUS =================

    // Ativa o aluno
    public void activate() {
        this.active = true;
    }

    // Inativa o aluno
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

    // ================= SETTERS =================

    // Altera o nome do estudante
    public void setName(String name) {
        this.name = name;
    }

    // Altera o contato
    public void setContact(String contact) {
        this.contact = contact;
    }

    // Altera o email do estudante
    public void setEmail(String email) {
        this.email = email;
    }

    // Altera a data de nascimento do estudante
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // ================= REPRESENTAÇÃO EM TEXTO =================
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
}