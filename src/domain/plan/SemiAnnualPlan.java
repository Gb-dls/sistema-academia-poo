package domain.plan;

import domain.Enrollment;

public class SemiAnnualPlan extends Plan {

    public SemiAnnualPlan(String name, String description, int minDurationMonths, double pricePerMonth) {
        super(name, description, minDurationMonths, pricePerMonth);
    }

    @Override
    public double calculateTotalPrice(int months) {
        double basePrice = getPricePerMonth() * months;

        // Regra: desconto de 10%
        if (months > getMinDurationMonths()) {
            return basePrice * 0.90;
        }
        return basePrice;
    }

    // Se o plano ainda está no período de carência (tempo mínimo) ao cancelar será cobrado uma taxa de cancelamento equivalente ao valor dado como desconto
    @Override
    public double getCancellationFee(Enrollment enrollment) {
        return calculatePercentageFee(enrollment, 0.15);
    }


    @Override
    public String getPlanTypeName() { return "SemiAnnual"; }
}