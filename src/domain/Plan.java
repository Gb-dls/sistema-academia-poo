package domain;

public class Plan {
     private String name;
     private String description;
     private PlanType plan;
     private int minDurationMonths;
     private double pricePerMonth;

     public Plan(String name, String description, PlanType plan, int minDurationMonths, double pricePerMonth) {
          this.name = name;
          this.description = description;
          this.plan = plan;
          this.minDurationMonths = minDurationMonths;
          this.pricePerMonth = pricePerMonth;
     }

     public String getName(){
          return this.name;
     }

     public String getDescription(){
          return this.description;
     }

     public PlanType getPlan(){
          return this.plan;
     }

     public int getMinDurationMonths(){
          return this.minDurationMonths;
     }

     public double getPricePerMonth(){
          return this.pricePerMonth;
     }

     public double calculateTotalPrice() {
          double result = this.minDurationMonths * this.pricePerMonth;
          double discount = 0;

          if (this.plan == PlanType.QUARTERLY) {
               discount = 0.10;
          } else if (this.plan == PlanType.SEMI_ANNUAL) {
               discount = 0.20;
          } else if (this.plan == PlanType.ANNUAL) {
               discount = 0.30;
          }

          return result * (1 - discount);
     }

     public void setUpdatePrice(double newPrice){
          this.pricePerMonth = newPrice;
     }

     //PRINT

     //Metodo para printar todos
     public void printPlan() {
          System.out.println("---------- DADOS DO PLANO ----------");
          System.out.println("Nome: " + this.name);
          System.out.println("Descrição: " + this.description);
          System.out.println("Tipo: " + this.plan); // 'plan' com p minúsculo
          System.out.println("Duração Mínima: " + this.minDurationMonths + " meses");
          System.out.println("Preço Mensal: R$ " + this.pricePerMonth);
          // Aproveite para imprimir o valor com desconto!
          System.out.println("Preço Total (com descontos): R$ " + calculateTotalPrice());
          System.out.println("------------------------------------");
     }
}
