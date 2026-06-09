package domain.plan;

import domain.Enrollment;

public class MonthlyPlan extends Plan {

    public MonthlyPlan(String name, String description, int minDurationMonths, double pricePerMonth) {
        super(name, description, minDurationMonths, pricePerMonth);
    }

    //Plano mensal não é aplicado nenhum desconto e não há taxa de cancelamento
    @Override
    public double calculateTotalPrice(int months) {
        return getPricePerMonth() * months;
    }

    @Override
    public double getCancellationFee(Enrollment enrollment) {
        return 0.0;
    }

    @Override
    public String getPlanTypeName() { return "Monthly"; }
}