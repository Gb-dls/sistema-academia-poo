package domain;

public abstract class Plan {
     private String name;
     private String description;
     private PlanType type;
     private int minDurationMonths;
     private double pricePerMonth;

     public Plan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth) {
          this.name = name;
          this.description = description;
          this.type = type;
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

     @Override
     public String toString() {
          return "Nome: " + name + "\n" +
                  "Descrição: " + description + "\n" +
                  "Tipo: " + type + "\n" +
                  "Duração mínima: " + minDurationMonths + " meses\n" +
                  "Preço mensal: R$ " + String.format("%.2f", pricePerMonth) + "\n" +
                  "Preço total (com desconto): R$ " + String.format("%.2f", calculateTotalPrice(minDurationMonths)) + "\n";
     }

}
