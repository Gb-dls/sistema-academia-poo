package domain.plan;

import domain.Enrollment;
import java.io.Serializable;
public abstract class Plan implements Serializable{
     private static final long serialVersionUID = 1L;
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
          if (enrollment.getMonthsActive() <= enrollment.getDurationMonths()) {
               return enrollment.getTotalPrice() * percentage;
          }
          return 0.0;
     }

     @Override
     public String toString() {
          // Calcula o valor bruto total sem nenhum desconto
          double totalWithoutDiscount = pricePerMonth * minDurationMonths;

          // Chama o metodo polimorfico que trara o valor com desconto da subclasse
          double totalWithDiscount = (calculateTotalPrice(minDurationMonths + 1)) / (minDurationMonths + 1);

          return "Nome: " + name + "\n" +
                  "Descrição: " + description + "\n" +
                  "Duração mínima: " + minDurationMonths + " meses\n" +
                  "Preço mensal: R$ " + String.format("%.2f", pricePerMonth) + "\n" +
                  "Preço total (sem desconto): R$ " + String.format("%.2f", totalWithoutDiscount) + "\n" +
                  "Preço mensal (**com desconto): R$ " + String.format("%.2f", totalWithDiscount) + "\n" +
                  "**Desconto válido apenas para matriculas realizadas com período ACIMA do mínimo";
     }

}
