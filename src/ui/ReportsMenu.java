package ui;



import application.FitManager;
import application.OperationResult;
import java.util.ArrayList;
import domain.Student;
import domain.Enrollment;
import domain.plan.Plan;


// Classe responsável pelo menu de gerenciamento dos relatorios
public class ReportsMenu {

    // 'Interface' responsável pela comunicação com o utilizador (entrada/saída)
    private final UserInterface ui;

    // Classe principal de regras de negócio do sistema
    private final FitManager fitManager;

    // Construtor: recebe as dependências necessárias para o menu funcionar
    public ReportsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // Metodo principal do menu de alunos
    public void start() {

        int option;

        // Looping que mantém o menu ativo até o utilizador escolher sair
        do {
            ui.showMenu(
                    "RELATÓRIOS",
                    """
                    1 - Alunos com matrícula ativa
                    2 - Matrículas com saldo pendente
                    3 - Todas as matrículas
                    4 - Todos os planos
                    5 - Consultar aluno por CPF
                    6 - Consultar plano por nome
                    7 - Matrícula ativa de um aluno por CPF
                    8 - Relatório Financeiro Mensal
                    9 - Voltar
                    """
            );

            option = ui.getInt("");       // Le a opção do utilizador

            if (option == -1) {
                return;
            }

            switch (option) {
                case 1 -> listActiveStudents();
                case 2 -> listPendingEnrollments();
                case 3 -> listAllEnrollments();
                case 4 -> listAllPlans();
                case 5 -> findStudentByCpf();
                case 6 -> findPlanByName();
                case 7 -> findActiveEnrollmentByCpf();
                case 8 -> generateFinancialReport();
                case 9 -> ui.showMessage("Voltando ao menu principal...");
                default -> ui.showError("Opção inválida!");
            }

        } while (option != 9);
    }


    // Lista todos os alunos que possuem matrícula ativa
    private void listActiveStudents() {

        ArrayList<Student> activeStudents = fitManager.listActiveStudents();

        if (activeStudents.isEmpty()) {
            ui.showError("Nenhum aluno com matrícula ativa encontrado.");
            return;
        }

        ui.showMessage("===== ALUNOS COM MATRÍCULA ATIVA =====");

        for (int i = 0; i < activeStudents.size(); i++) {
            Student s = activeStudents.get(i);
            ui.showMessage("Nome: " + s.getName() + " | CPF: " + s.getCpf());
        }

        ui.showMessage("Total: " + activeStudents.size() + " aluno(s).");

    }

    // Lista matrículas que possuem saldo pendente
    private void listPendingEnrollments() {
        OperationResult<ArrayList<Enrollment>> result = fitManager.listPendingEnrollments();
        if(!result.isSuccess()){
            ui.showError(result.getMessage());
            return;
        }
        ArrayList<Enrollment> pending = result.getData();
        ui.showMessage("===== MATRÍCULAS PENDENTES =====");

        for(int i = 0; i < pending.size(); i++){
            ui.showMessage(pending.get(i).toString());
        }
        ui.showMessage("Total: " + pending.size() + " matrícula(s) pendente(s).");
    }

    // Lista todas as matrículas cadastradas no sistema
    private void listAllEnrollments() {

        OperationResult<ArrayList<Enrollment>> result = fitManager.listEnrollments();
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }

        ArrayList<Enrollment> enrollments = result.getData();
        ui.showMessage("===== TODAS AS MATRÍCULAS =====");
        for(int i = 0; i < enrollments.size(); i++){
            ui.showMessage(enrollments.get(i).toString());
        }
        ui.showMessage("Total: " + enrollments.size());
    }

    // Lista todos os planos disponíveis no sistema
    private void listAllPlans() {
        OperationResult<ArrayList<Plan>> result = fitManager.listPlans();
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }
        ArrayList<Plan> plans = result.getData();
        ui.showMessage("===== TODOS OS PLANOS =====");
        for(int i = 0; i < plans.size(); i++){
            Plan p = plans.get(i);
            ui.showMessage(p.toString());
        }
        ui.showMessage("Total: " + plans.size());
    }



    // Busca um aluno pelo CPF informado pelo utilizador
    private void findStudentByCpf() {

        String cpf = ui.getInput("Digite o CPF do aluno:");
        if(cpf.isEmpty()){
            ui.showError("CPF não informado. Consulta cancelada.");
            return;
        }

        OperationResult<Student> result = fitManager.findStudentByCpf(cpf);
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }

        Student student = result.getData();

        ui.showMessage(student.toString());
    }

    // Busca um plano pelo nome informado pelo utilizador
    private void findPlanByName() {

        String name = ui.getInput("Digite o nome do plano:");
        if(name.isEmpty()){
            ui.showError("Nome não informado. Consulta cancelada.");
            return;
        }

        OperationResult<Plan> result = fitManager.findPlanByName(name);
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }
        Plan plan = result.getData();
        ui.showMessage(plan.toString());
    }

    // Busca matrícula ativa de um aluno pelo CPF
    private void findActiveEnrollmentByCpf() {

        String cpf = ui.getInput("Digite o CPF do aluno:");
        if(cpf.isEmpty()){
            ui.showError("CPF não informado. Consulta cancelada.");
            return;
        }

        OperationResult<Enrollment> result = fitManager.findActiveEnrollmentByStudent(cpf);
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }
        Enrollment enrollment = result.getData();
        ui.showMessage(enrollment.toString());
    }

    // =========================================
    // RELATÓRIO FINANCEIRO MENSAL
    // =========================================
    private void generateFinancialReport() {
        ui.showMessage("===== GERADOR DE RELATÓRIO FINANCEIRO =====");

        int month = 0;
        int year = 0;

        // 1. Validação do Mês
        while (true) {
            String monthStr = ui.getInput("Digite o Mês (1 a 12) ou deixe vazio para cancelar:");
            if (monthStr == null || monthStr.isEmpty()) return;

            try {
                month = Integer.parseInt(monthStr);
                if (month >= 1 && month <= 12) {
                    break;
                } else {
                    ui.showError("Mês inválido. Digite um número entre 1 e 12.");
                }
            } catch (NumberFormatException e) {
                ui.showError("Formato inválido. Digite apenas números inteiros.");
            }
        }

        // 2. Validação do Ano
        while (true) {
            String yearStr = ui.getInput("Digite o Ano (Ex: 2024) ou deixe vazio para cancelar:");
            if (yearStr == null || yearStr.isEmpty()) return;

            try {
                year = Integer.parseInt(yearStr);
                if (year > 2000) {
                    break;
                } else {
                    ui.showError("Ano inválido. Digite um ano recente (ex: 2024).");
                }
            } catch (NumberFormatException e) {
                ui.showError("Formato inválido. Digite apenas números inteiros.");
            }
        }

        // 3. Busca os dados e gera o objeto de relatório
        domain.FinancialReport report = fitManager.generateFinancialReport(month, year);

        // 4. Monta a visualização em texto
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("\n============================================\n");
        relatorio.append(String.format("   RELATÓRIO FINANCEIRO - %02d/%04d\n", month, year));
        relatorio.append("============================================\n");

        if (!report.hasFinancialActivity()) {
            relatorio.append("\n[ INFORMATIVO ]: Nenhum pagamento ou movimentação de matrícula foi registrado neste período.\n");
        }

        relatorio.append("\n1. MÉTRICAS GERAIS\n");
        relatorio.append(String.format("Receita Total do Período:      R$ %.2f\n", report.getTotalRevenue()));
        relatorio.append(String.format("Total de Taxas Processamento:  R$ %.2f\n", report.getTotalProcessingFees()));
        relatorio.append(String.format("Matrículas Iniciadas:          %d\n", report.getStartedEnrollmentsCount()));
        relatorio.append(String.format("Matrículas Canceladas:         %d\n", report.getCancelledEnrollmentsCount()));

        relatorio.append("\n2. RECEITA POR TIPO DE PLANO\n");
        if (report.getRevenueByPlanType().isEmpty()) {
            relatorio.append("   Sem dados para exibir.\n");
        } else {
            for (java.util.Map.Entry<String, Double> entry : report.getRevenueByPlanType().entrySet()) {
                relatorio.append(String.format("   - %-15s R$ %.2f\n", entry.getKey() + ":", entry.getValue()));
            }
        }

        relatorio.append("\n3. RECEITA POR FORMA DE PAGAMENTO\n");
        if (report.getRevenueByPaymentMethod().isEmpty()) {
            relatorio.append("   Sem dados para exibir.\n");
        } else {
            for (java.util.Map.Entry<String, Double> entry : report.getRevenueByPaymentMethod().entrySet()) {
                relatorio.append(String.format("   - %-15s R$ %.2f\n", entry.getKey() + ":", entry.getValue()));
            }
        }

        relatorio.append("\n4. RANKING DE PLANOS MAIS CONTRATADOS\n");
        java.util.List<String> topPlans = report.getTopContractedPlans();
        if (topPlans.isEmpty()) {
            relatorio.append("   Nenhuma matrícula contratada no período.\n");
        } else {
            for (int i = 0; i < topPlans.size(); i++) {
                relatorio.append(String.format("   %dº Lugar: %s\n", (i + 1), topPlans.get(i)));
            }
        }
        relatorio.append("============================================\n");

        ui.showMessage(relatorio.toString());

        // 5. Opção de exportar para arquivo de texto
        String exportar = ui.getInput("Deseja exportar este relatório para um arquivo .txt? (S/N):");
        if (exportar != null && exportar.trim().equalsIgnoreCase("s")) {
            String fileName = String.format("RelatorioFinanceiro_%02d_%04d.txt", month, year);
            try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
                writer.write(relatorio.toString());
                ui.showMessage("[SUCESSO] Relatório exportado na pasta raiz do projeto: " + fileName);
            } catch (java.io.IOException e) {
                ui.showError("Falha crítica ao tentar salvar o arquivo no disco: " + e.getMessage());
            }
        }
    }

}