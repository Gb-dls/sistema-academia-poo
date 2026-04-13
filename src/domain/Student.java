package domain;

import java.time.LocalDate;
import java.time.Period;

public class Student {
    private String name;
    private String cpf;
    private String contact;
    private String email;
    private LocalDate birthDate;
    private boolean active;

    public Student(String name, String cpf, String contact, String email, LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.contact = contact;
        this.email = email;
        this.birthDate = birthDate;
        this.active = true;
    }

    // metodo que calcula a idade do aluno
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



}
