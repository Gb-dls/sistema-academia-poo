package domain;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// Data Transfer Object (DTO) focado exclusivamente em transportar os dados consolidados para a UI
public class FinancialReport {
    private final int month;
    private final int year;

    private double totalRevenue;
    private double totalProcessingFees;
    private int startedEnrollmentsCount;
    private int cancelledEnrollmentsCount;

    // Agrupamentos usando os métodos polimórficos das entidades
    private final Map<String, Double> revenueByPlanType;
    private final Map<String, Double> revenueByPaymentMethod;
    private final Map<String, Integer> planTypeContractionCount;

    public FinancialReport(int month, int year) {
        this.month = month;
        this.year = year;

        // Tudo nasce zerado - O Padrão "Null Object" para ausência de dados
        this.totalRevenue = 0.0;
        this.totalProcessingFees = 0.0;
        this.startedEnrollmentsCount = 0;
        this.cancelledEnrollmentsCount = 0;
        this.revenueByPlanType = new HashMap<>();
        this.revenueByPaymentMethod = new HashMap<>();
        this.planTypeContractionCount = new HashMap<>();
    }

    // ================= MÉTODOS DE AGREGAÇÃO =================

    public void addRevenue(double amount) {
        this.totalRevenue += amount;
    }

    public void addProcessingFee(double fee) {
        this.totalProcessingFees += fee;
    }

    public void addRevenueByPlanType(String planType, double amount) {
        this.revenueByPlanType.put(planType, this.revenueByPlanType.getOrDefault(planType, 0.0) + amount);
    }

    public void addRevenueByPaymentMethod(String paymentMethod, double amount) {
        this.revenueByPaymentMethod.put(paymentMethod, this.revenueByPaymentMethod.getOrDefault(paymentMethod, 0.0) + amount);
    }

    public void incrementStartedEnrollment() {
        this.startedEnrollmentsCount++;
    }

    public void incrementCancelledEnrollment() {
        this.cancelledEnrollmentsCount++;
    }

    public void recordPlanContraction(String planType) {
        this.planTypeContractionCount.put(planType, this.planTypeContractionCount.getOrDefault(planType, 0) + 1);
    }

    // ================= GETTERS =================

    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getTotalProcessingFees() { return totalProcessingFees; }
    public int getStartedEnrollmentsCount() { return startedEnrollmentsCount; }
    public int getCancelledEnrollmentsCount() { return cancelledEnrollmentsCount; }

    public Map<String, Double> getRevenueByPlanType() { return revenueByPlanType; }
    public Map<String, Double> getRevenueByPaymentMethod() { return revenueByPaymentMethod; }
    public Map<String, Integer> getPlanTypeContractionCount() { return planTypeContractionCount; }

    // Verifica se houve alguma movimentação real neste mês
    public boolean hasFinancialActivity() {
        return this.totalRevenue > 0 || this.startedEnrollmentsCount > 0 || this.cancelledEnrollmentsCount > 0;
    }

    // ================= LÓGICA DE EXIBIÇÃO =================

    // Retorna uma lista com os nomes dos planos mais contratados em ordem decrescente
    public List<String> getTopContractedPlans() {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(planTypeContractionCount.entrySet());

        // Ordena em ordem decrescente pelo valor (quantidade de contratos)
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<String> sortedPlans = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortedPlans.add(entry.getKey() + " (" + entry.getValue() + " contratos)");
        }
        return sortedPlans;
    }
}