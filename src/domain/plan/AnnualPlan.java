package domain.plan;

import domain.Enrollment;

public class AnnualPlan extends Plan {

    public AnnualPlan(String name, String description, int minDurationMonths, double pricePerMonth) {
        super(name, description, minDurationMonths, pricePerMonth);
    }

    @Override
    public double calculateTotalPrice(int months) {
        double basePrice = getPricePerMonth() * months;

        // Regra: desconto de 15% apenas se contratado MAIS que o mínimo
        if (months > getMinDurationMonths()) {
            return basePrice * 0.85;
        }
        return basePrice;
    }

    // Se o plano ainda está no período de carência (tempo mínimo) ao cancelar será cobrado uma taxa de cancelamento de 20%
    @Override
    public double getCancellationFee(Enrollment enrollment) {
        if (enrollment.getMonthsActive() >= (enrollment.getDurationMonths() / 2.0)) {
            return calculatePercentageFee(enrollment, 0.0); // Isento se cumpriu metade ou mais
        }
        // Cobra multa de 20% se cancelou antes da metade
        return calculatePercentageFee(enrollment, 0.20);
    }

    @Override
    public String getPlanTypeName() { return "Annual"; }
}