package ui;

import domain.Plan;
import domain.PlanType;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("======= CADASTRO POR MENU NUMÉRICO =======");

        System.out.print("Nome do plano: ");
        String nome = leitor.nextLine();

        System.out.print("Descrição: ");
        String desc = leitor.nextLine();

        // --- MENU DE OPÇÕES ---
        System.out.println("\nSelecione o tipo de plano:");
        System.out.println("1 - Mensal");
        System.out.println("2 - Trimestral");
        System.out.println("3 - Semestral");
        System.out.println("4 - Anual");
        System.out.print("Opção: ");

        int opcaoEscolhida = leitor.nextInt();

        // Aqui usamos o "tradutor" que criamos no Enum
        PlanType tipoEscolhido = PlanType.fromInt(opcaoEscolhida);

        System.out.print("Duração (meses): ");
        int duracao = leitor.nextInt();

        System.out.print("Preço mensal: R$ ");
        double preco = leitor.nextDouble();

        // Criando o objeto com o Enum encontrado pelo número
        Plan novoPlano = new Plan(nome, desc, tipoEscolhido, duracao, preco);

        System.out.println("\n>>> SUCESSO! PLANO CRIADO VIA MENU:");
        novoPlano.printPlan();

        leitor.close();
    }
}