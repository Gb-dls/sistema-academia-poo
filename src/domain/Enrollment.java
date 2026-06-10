package domain;

import domain.plan.Plan;
import domain.payment.Payment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.temporal.ChronoUnit;
import java.io.Serializable;

public class Enrollment  implements Serializable{
    private static final long serialVersionUID = 1L;
    private int code;
    private Student student;
    private Plan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationMonths;
    private double totalPrice;
    private EnrollmentStatus status;
    private ArrayList<Payment> payments;

    // Construtor //
    public Enrollment(int code, Student student, Plan plan, LocalDate startDate, int durationMonths) {
        this.code = code;
        this.student = student;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(durationMonths);
        this.durationMonths = durationMonths;
        this.totalPrice = plan.calculateTotalPrice(durationMonths);
        this.status = EnrollmentStatus.ACTIVE;
        this.payments = new ArrayList<>();
    }

    // Getters //
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

    // Métodos //

    /*
    @ registerPayment
    @ Objetivo: Adicionar um pagamento a lista
    */
    public void registerPayment(Payment payment) {
        this.payments.add(payment);
    }

    /*
    @ calculateTotalPaid
    @ Objetivo: Calcula o valor total efetivamente abatido da dívida, descontando taxas de processamento
    */
    public double calculateTotalPaid() {
        double total = 0;
        for (Payment p : payments) {
            total += (p.getAmount() - p.getProcessingFee());
        }
        return total;
    }

    /*
    @ calculateBalance
    @ Objetivo: Calcula o restante a ser pago de acordo com o total do contrato
    */
    public double calculateBalance() {
        double balance = this.totalPrice;
        double totalPaid = calculateTotalPaid();

        return Math.max(0.0, balance - totalPaid);
    }

    /*
    @ cancel
    @ Objetivo: Cancela a matrícula ativa, ajusta o preço total para o proporcional dos meses utilizados e aplica a multa rescisória do plano.
    */
    public void cancel() {
        if (this.status == EnrollmentStatus.ACTIVE) {
            this.status = EnrollmentStatus.CANCELLED;

            // Calcula o valor de 1 mês avulso do plano
            double pricePerMonth = this.plan.getPricePerMonth();

            // Descobre quantos meses ele usou de verdade (mínimo 1 mês para não zerar se ele cancelar no primeiro dia)
            int monthsUsed = Math.max(1, getMonthsActive());

            // Se ele usou mais meses do que a duração original por algum motivo, limita ao total do contrato
            if (monthsUsed > durationMonths) monthsUsed = durationMonths;

            // Calcula o proporcional usado
            double proportionalPrice = pricePerMonth * monthsUsed;

            // Pega a multa de carência do plano
            double penaltyFee = this.plan.getCancellationFee(this);

            // O novo preço total é a proporcional aos meses que usou + multa
            this.totalPrice = proportionalPrice + penaltyFee;

            // Ajusta a data de término para o dia do cancelamento real
            this.endDate = LocalDate.now();
        }
    }

    /*
    @ toString
    @ Objetivo: Produzir uma representação textual completa dos detalhes da matrícula e seu histórico financeiro
    */
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
                // Aqui o polimorfismo do Payment.toString() brilha!
                result += "  " + (i + 1) + ". " + payments.get(i) + "\n";
            }
        }

        return result;
    }

    // Calcula quantos meses inteiros se passaram desde o startDate até HOJE
    public int getMonthsActive() {
        long months = ChronoUnit.MONTHS.between(this.startDate, LocalDate.now());
        return (int) months;
    }
}
