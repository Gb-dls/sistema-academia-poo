# FitManager — Sistema de Gestão de Academia

Sistema de gerenciamento de academia desenvolvido em Java, permitindo o controle de alunos, planos, matrículas e pagamentos.

## Integrantes

- Gabriel Gonçalves de Assis de Souza;
- Marcelly Lais Ferreira de Almeida;
- Maria Rita do Nascimento Vieira

## Versão do Java

Java 21

## Como compilar e executar - PARTE 1

### Compilar
Navegue até a pasta `src` do projeto e execute:
```bash
javac -d ../out application/*.java domain/*.java ui/*.java validators/*.java
```

### Executar
```bash
java -cp ../out ui.Main
```

## Estrutura do projeto

```
src/
├── application/    → FitManager, PlanService, StudentService, EnrollmentService, OperationResult
├── domain/         → Student, Plan, Enrollment, Payment, PlanType, PaymentType, EnrollmentStatus
├── ui/             → Main, MainMenu, StudentMenu, PlanMenu, EnrollmentMenu, ReportsMenu, UserInterface
└── validators/     → CpfValidator, ContactValidator
```

## Como compilar e executar - PARTE 2

### Pré-requisito
Crie a pasta `bin` na raiz do projeto (apenas na primeira vez):
```bash
mkdir bin
```

### Compilar
Navegue até a pasta `src` do projeto e execute:
```bash
javac -d ../bin application/*.java domain/*.java domain/payment/*.java domain/plan/*.java formatters/*.java ui/*.java validators/*.java
```

### Executar
Ainda dentro da pasta `src`, execute:
```bash
java -cp ../bin ui.Main
```
## Estrutura do projeto

```
src/
├── application/    → Serviços e controladores (FitManager, PlanService, EnrollmentService) e a classe Main
├── domain/         → Entidades base (Student, Enrollment)
│   ├── plan/       → Hierarquia polimórfica de planos (Plan abstrata, MonthlyPlan, AnnualPlan, etc.)
│   └── payment/    → Hierarquia polimórfica de pagamentos (Payment abstrata, CashPayment, PixPayment, etc.)
├── formatters/     → Formatador isolado (DateFormatter)
├── ui/             → Contratos e implementações visuais (UserInterface, TerminalUI, JOptionPaneUI e Menus)
└── validators/     → Validadores isolados (CpfValidator, ContactValidator)
```