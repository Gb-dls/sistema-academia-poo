package application;

import domain.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class EnrollmentService {
    private static int nextCode = 1;    // Gerador automatico de codigo
    private ArrayList<Enrollment> enrollments;

    // Construtor
    public EnrollmentService() {
        this.enrollments = new ArrayList<>();   // Inicializando o ArrayList
    }

    // Metodos

    // Metodo enroll para cadastro de matricula
    public OperationResult enroll(Student student, Plan plan, LocalDate startDate,
                                  int durationMonths, double initialPayment, PaymentType paymentType) {
        // Verifica se o aluno possui matricula ativa
        if (hasActiveEnrollment(student.getCpf())) {
            return new OperationResult(false, "O aluno já possui uma matrícula ativa no sistema.");
        }

        // Verifica se a duration de meses é menor que o minimo do plano
        if (durationMonths < plan.getMinDurationMonths()) {
            return new OperationResult(false, "A duração escolhida é menor que o mínimo exigido pelo plano (" + plan.getMinDurationMonths() + " meses).");
        }
        // Pagamento inicial é a primeira mensalidade cheia
        double valuePerMonth = plan.calculateTotalPrice(durationMonths) / durationMonths;

        if (initialPayment < valuePerMonth) {
            //  String.format para deixar o dinheiro bonitinho com duas casas decimais
            return new OperationResult(false, String.format("O pagamento inicial mínimo exigido é de R$ %.2f.", valuePerMonth));
        }
        // --- Efetivacao da Matricula ---

        // Criando Matricula
        Enrollment newEnrollment = new Enrollment(nextCode, student, plan, startDate, durationMonths);

        // Registrando o pagamento
        Payment firstPayment = new Payment(initialPayment, paymentType, "Pagamento da 1ª Mensalidade");
        newEnrollment.registerPayment(firstPayment);

        // Adicionando matricula no ArrayList
        this.enrollments.add(newEnrollment);

        // Subindo gerador de codigo automatico
        nextCode++;

        return new OperationResult(true, "Matrícula efetivada com sucesso!");
    }

    // Metodo registerPayment
    public OperationResult registerPayment(int enrollmentCode, double amount, PaymentType type, String description) {

        // Checando se o valor e valido
        if (amount <= 0) {
            return new OperationResult(false, "O valor do pagamento deve ser maior que zero.");
        }

        // Procurando a matricula com o codigo digitado
        Enrollment flagEnrollment = findByCode(enrollmentCode);

        // Se a variavel continua null, nao achou
        if (flagEnrollment == null) {
            return new OperationResult(false, "Matrícula de código " + enrollmentCode + " não encontrada no sistema.");
        }
        // Nao e possivel fazer pagaento de uma matricula cancelada
        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            return new OperationResult(false, "Não é possível registrar pagamentos em uma matrícula cancelada.");
        }

        // Não deixa o aluno pagar mais do que deve
        if (amount > flagEnrollment.calculateBalance()) {
            return new OperationResult(false, "O valor pago supera o saldo devedor. Pagamento máximo permitido: R$ " + flagEnrollment.calculateBalance());
        }

        // --- Efetivando pagamento ---

        Payment newPayment = new Payment(amount, type, description);

        // Guarda o pagamento na lista da matricula correspondente
        flagEnrollment.registerPayment(newPayment);

        // Calculos para verificar se a matricula foi quitada ou o que esta pendente
        double balance = flagEnrollment.calculateBalance();
        String statusFinanceiro;
        if (balance > 0) {
            statusFinanceiro = String.format(" Saldo pendente: R$ %.2f", balance);
        } else {
            statusFinanceiro = " Matrícula quitada!";
        }
        // Retorna a mensagem de sucesso e uma mensagem ( Saldo pendente: R$ xx,xx
        return new OperationResult(true, "Pagamento registrado com sucesso!" + statusFinanceiro);
    }

    public OperationResult cancel(int code) {

        Enrollment flagEnrollment = findByCode(code);

        // Não encontrou matricula flag == null
        if (flagEnrollment == null) {
            return new OperationResult(false, "Matrícula não encontrada no sistema.");
        }
        // Verificando se a matricula esta ativa para poder cancelar
        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            return new OperationResult(false, "A matrícula informada já está cancelada.");
        }
        // Cancelando Matricula
        flagEnrollment.cancel();

        // Montando relatorio de cancelamento para o operationResult
        double totalContract = flagEnrollment.getTotalPrice();
        double totalPaid = flagEnrollment.calculateTotalPaid();
        double balance = flagEnrollment.calculateBalance();

        String resumo = String.format(
                "Matrícula cancelada com sucesso!\n" +
                        "--- RESUMO FINANCEIRO ---\n" +
                        "Valor Total do Contrato: R$ %.2f\n" +
                        "Total Já Pago: R$ %.2f\n",
                totalContract, totalPaid);

        if (balance > 0) {
            resumo += String.format("Valor Pendente: R$ %.2f", balance);
        } else {
            resumo += "O valor foi pago corretamente.";
        }
        return new OperationResult(true, resumo);
    }

    // Metodo de busca por codigo se achar devolve a matricula senao devolve null
    public Enrollment findByCode(int code) {
        for (Enrollment e : enrollments) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    // Metodo que verifica se uma matricula esta ativa de acordo com o cpf do student (true ativa - false cancelada)
    public boolean hasActiveEnrollment(String cpf) {

        // Busca para achar a matricula
        Enrollment matriculaEncontrada = findActiveByStudent(cpf);

        // Checando se encontrou
        if (matriculaEncontrada != null) {
            return true;
        } else {
            return false;
        }
    }

    // Metodo que devolve a matricula por uma busca por cpf do student
    public Enrollment findActiveByStudent(String cpf) {
        for (Enrollment e : enrollments) {
            // Se o CPF bater E a matrícula estiver ativa...
            if (e.getStudent().getCpf().equals(cpf) && e.getStatus() == EnrollmentStatus.ACTIVE) {
                return e;
            }
        }
        return null;
    }

    // Metodo que devolve uma copia da lista enrollments (copia para nao permitir alteracao da lista)
    public ArrayList<Enrollment> listEnrollments() {
        return new ArrayList<>(this.enrollments);
    }
}
