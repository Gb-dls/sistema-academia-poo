# FitManager — Sistema de Gestão de Academia

Sistema de gerenciamento de academia desenvolvido em Java, permitindo o controle de alunos, planos, matrículas e pagamentos.

## Integrantes

- Gabriel Gonçalves de Assis de Souza;
- Marcelly Lais Ferreira de Almeida;
- Maria Rita do Nascimento Vieira

## Versão do Java

Java 21

## Como compilar e executar

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