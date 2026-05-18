package domain.plan;

import domain.Enrollment;

public abstract class Plan {
     private String name;
     private String description;
     private int minDurationMonths;
     private double pricePerMonth;

     public Plan(String name, String description, int minDurationMonths, double pricePerMonth) {
          this.name = name;
          this.description = description;
          this.minDurationMonths = minDurationMonths;
          this.pricePerMonth = pricePerMonth;
     }

     public String getName(){
          return this.name;
     }

     public String getDescription(){
          return this.description;
     }

     public int getMinDurationMonths(){
          return this.minDurationMonths;
     }

     public double getPricePerMonth(){
          return this.pricePerMonth;
     }

     // Atualiza o preço mensal do plano.
     // Matrículas já existentes não são afetadas, pois armazenam
     // o totalPrice calculado no momento da criação.
     public void updatePrice(double newPrice){
          this.pricePerMonth = newPrice;
     }

     public abstract double calculateTotalPrice(int months);

     public abstract double getCancellationFee(Enrollment enrollment);

     // Calcula a taxa baseada em porcentagem se o tempo mínimo de contrato não foi atingido
     protected double calculatePercentageFee(Enrollment enrollment, double percentage) {
          if (enrollment.getMonthsActive() < getMinDurationMonths()) {
               return enrollment.getTotalPrice() * percentage;
          }
          return 0.0;
     }

     @Override
     public String toString() {
          return "Nome: " + name + "\n" +
                  "Descrição: " + description + "\n" +
                  "Duração mínima: " + minDurationMonths + " meses\n" +
                  "Preço mensal: R$ " + String.format("%.2f", pricePerMonth) + "\n" +
                  "Preço total (com desconto): R$ " + String.format("%.2f", calculateTotalPrice(minDurationMonths)) + "\n";
     }

}
