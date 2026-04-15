package domain;

// Imports
import java.time.LocalDate;
import java.util.ArrayList;


// Class Enrollment
public class Enrollment {

    private int code;
    private Student student;
    private Plan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationMonths;
    private double totalPrice;
    private EnrollmentStatus status;
    private ArrayList<Payment> payments;

    // Constructor
    public Enrollment(int code, Student student, Plan plan, LocalDate startDate, int durationMonths) {
        this.code = code;   // vem de nextCode de Enrollment Service
        this.student = student;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(durationMonths);
        this.durationMonths = durationMonths;
        this.totalPrice = plan.calculateTotalPrice(durationMonths);
        this.status = EnrollmentStatus.ACTIVE;
        this.payments = new ArrayList<>();
    }

    // Methods

    // Method registerPayment - Add payment no ArrayList
    public void registerPayment(Payment payment) {
        this.payments.add(payment);
    }

    // Method calculateTotalPaid - Calcula o valor total pago ate o momento
    public double calculateTotalPaid() {
        double total = 0;
        for (Payment p : payments) {
            total += p.getAmount();
        }

        return total;
    }

    // Method calculateBalance - Calcula o que o falta a ser pago de acordo com o total do contrato
    public double calculateBalance() {
        double balance = this.totalPrice;
        double totalPaid = calculateTotalPaid();

        return (balance - totalPaid);
    }

    // Method cancel - Cancela uma matrícula ativa
    public void cancel() {
        if(this.status == EnrollmentStatus.ACTIVE) {
           this.status = EnrollmentStatus.CANCELLED;
        }
    }

    // Getters
    public int getCode() {
        return code;
    }

    public Student getStudent() {
        return student;
    }

    public Plan getPlan() {
        return plan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }
    @Override
    public String toString() {

        // Calcula o total já pago pelo aluno
        double totalPaid = calculateTotalPaid();

        // Calcula o saldo restante do quanto ainda falta pagar
        double balance   = calculateBalance();

        // Armazena todas as informações formatadas
        String result =

                "Código:       " + code + "\n" +                  // Código do contrato
                        "Aluno:        " + student.getName() + "\n" +     // Nome do aluno
                        "CPF:          " + student.getCpf() + "\n" +      // CPF do aluno
                        "Plano:        " + plan.getName() + "\n" +        // Nome do plano contratado
                        "Início:       " + startDate + "\n" +             // Data de início
                        "Término:      " + endDate + "\n" +               // Data de término
                        "Duração:      " + durationMonths + " meses\n" +  // Duração em meses
                        "Status:       " + status + "\n" +                // Status atual do contrato

                        // Valores financeiros
                        "Total:        R$ " + String.format("%.2f", totalPrice) + "\n" +
                        "Total pago:   R$ " + String.format("%.2f", totalPaid) + "\n";

        // Verifica se ainda existe saldo pendente
        if (balance > 0) {

            result += "Saldo:        R$ " + String.format("%.2f", balance) + " (pendente)\n";
        } else {
           result += "Saldo:        Quitado\n";
        }

        // Verifica se existem pagamentos registrados na lista
        if (!payments.isEmpty()) {
            result += "Pagamentos:\n"; // Título da seção de pagamentos

            for (int i = 0; i < payments.size(); i++) {
                result += "  " + (i + 1) + ". " + payments.get(i) + "\n";
            }
        }

        return result;
    }
}
