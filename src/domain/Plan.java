package domain;

public class Plan {
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

     public PlanType getType(){
          return this.type;
     }

     public int getMinDurationMonths(){
          return this.minDurationMonths;
     }

     public double getPricePerMonth(){
          return this.pricePerMonth;
     }

     // Calcula o preço total para a duração informada em meses.
     // O desconto é aplicado com base no tipo do plano:
     // QUARTERLY: 10%, SEMI_ANNUAL: 20%, ANNUAL: 30%, MONTHLY: sem desconto.
     // Nota: esta estrutura condicional por tipo é temporária — na próxima etapa,
     // cada PlanType se tornará uma subclasse de Plan com sua própria regra de cálculo.
     public double calculateTotalPrice(int months) {
          double result = months * this.pricePerMonth;
          double discount = 0;

          if (this.type == PlanType.QUARTERLY) {
               discount = 0.10;
          } else if (this.type == PlanType.SEMI_ANNUAL) {
               discount = 0.20;
          } else if (this.type == PlanType.ANNUAL) {
               discount = 0.30;
          }

          return result * (1 - discount);
     }

     // Atualiza o preço mensal do plano.
     // Matrículas já existentes não são afetadas, pois armazenam
     // o totalPrice calculado no momento da criação.
     public void updatePrice(double newPrice){
          this.pricePerMonth = newPrice;
     }

     //PRINT

     /*
     public void printPlan() {
          System.out.println("---------- DADOS DO PLANO ----------");
          System.out.println("Nome: " + this.name);
          System.out.println("Descrição: " + this.description);
          System.out.println("Tipo: " + this.type); // 'plan' com p minúsculo
          System.out.println("Duração Mínima: " + this.minDurationMonths + " meses");
          System.out.println("Preço Mensal: R$ " + this.pricePerMonth);
          // Aproveite para imprimir o valor com desconto!
          System.out.println("Preço Total (com descontos): R$ " + calculateTotalPrice());
          System.out.println("------------------------------------");
     }*/
}
