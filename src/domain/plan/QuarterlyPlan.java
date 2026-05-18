package domain.plan;

import domain.Enrollment;

public class QuarterlyPlan extends Plan {

    public QuarterlyPlan(String name, String description, int minDurationMonths, double pricePerMonth) {
        super(name, description, minDurationMonths, pricePerMonth);
    }

    @Override
    public double calculateTotalPrice(int months) {
        double basePrice = getPricePerMonth() * months;

        // Regra: desconto de 5% apenas se contratado MAIS que o mínimo
        if (months > getMinDurationMonths()) {
            return basePrice * 0.95;
        }

        return basePrice; // Retorna valor bruto
    }

    // Se o plano ainda está no período de carência (tempo mínimo) ao cancelar será cobrado uma taxa de cancelamento equivalente ao desconto + 5% de multa
    @Override
    public double getCancellationFee(Enrollment enrollment) {
        return calculatePercentageFee(enrollment, 0.10);
    }
}