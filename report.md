# Relatório — FitManager

## 1. Introdução

O FitManager é um sistema de gestão de academia desenvolvido em Java como trabalho prático da disciplina de Programação Orientada a Objetos. O sistema permite o gerenciamento de alunos, planos, matrículas e pagamentos, seguindo uma arquitetura em três camadas: Interface do Usuário (UI), Aplicação e Domínio.

Nesta primeira etapa, foi construída a base funcional do sistema — modelando as entidades do domínio, implementando as operações essenciais de cadastro, consulta e listagem, e organizando o código de forma que o projeto possa evoluir com consistência nas etapas seguintes.

---

## 2. Integrantes e contribuições

- **Gabriel Gonçalves de Assis de Souza** — Responsável pela gestão de matrículas e pagamentos: `Enrollment.java`, `EnrollmentService.java`, `EnrollmentStatus.java`, `Payment.java`, `PaymentType.java`, `FitManager.java`.

- **Marcelly Lais Ferreira de Almeida** — Responsável pela gestão de planos e pelo padrão de resultado das operações: `Plan.java`, `PlanType.java`, `PlanService.java`, `OperationResult.java`. Responsável também pela documentação: `README.md`, `report.md`.

- **Maria Rita do Nascimento Vieira** — Responsável pela gestão de alunos e pela interface com o usuário: `Student.java`, `StudentService.java`, `CpfValidator.java`, `ContactValidator.java`, `UserInterface.java`, `Main.java`, `MainMenu.java`, `StudentMenu.java`, `PlanMenu.java`, `EnrollmentMenu.java`, `ReportsMenu.java`.

---

## 3. Diagrama de classes final

> ⚠️ TODO — inserir diagrama atualizado refletindo o sistema conforme implementado.

---

## 4. Decisões de projeto

### 4.1 Armazenamento do CPF
O CPF é armazenado sem formatação, contendo apenas os 11 dígitos numéricos. Consideramos armazená-lo com formatação (`123.456.789-00`), o que melhoraria a exibição direta, mas optamos pela forma sem formatação pois simplifica buscas e comparações em todo o sistema, eliminando a necessidade de normalização antes de cada operação. A formatação é aplicada apenas na exibição, via `getCpfFormatted()` na classe `Student`.

### 4.2 Validação do CPF
Implementamos o algoritmo completo de verificação dos dígitos verificadores na classe `CpfValidator`. A alternativa seria validar apenas o formato básico — 11 dígitos numéricos — o que seria mais simples mas rejeitaria apenas CPFs obviamente inválidos. Optamos pela validação completa pois aumenta a robustez do sistema, rejeitando CPFs numericamente inválidos que passariam por uma validação superficial.

### 4.3 Remoção vs. inativação de alunos
O grupo adotou a estratégia de inativação via `deactivate()`, sem remover o objeto `Student` da lista. A remoção física seria mais simples, mas deixaria referências inválidas nos objetos `Enrollment` já existentes. A inativação preserva o histórico de matrículas associadas e mantém a integridade dos dados. O método `deleteStudent()` no `FitManager` chama `deactivateStudent()` no `StudentService`, que apenas marca o aluno como inativo sem removê-lo da coleção.

### 4.4 Validação da data de nascimento
A conversão e validação da data de nascimento é feita pelo método privado `parseDate(String)`, que fica na classe **FitManager** e é chamado nos métodos `registerStudent` e `updateStudent`.
O método recebe a data como String e faz as checagens: verifica se a `string` não é nula ou vazia, depois confere se ela está no padrão `yyyy-MM-dd` usando uma expressão regular e extrai os valores de ano, mês e dia para garantir que o mês está entre 1 e 12 e o dia entre 1 e 31. Se qualquer uma dessas checagens falhar, o método retorna `null.`
Quando o retorno é `null`, o fluxo é cortado na hora e o sistema devolve uma mensagem de erro: **"Data de nascimento inválida. Use o formato yyyy-MM-dd."** , somente quando a data passa por tudo isso é que o `LocalDate` é criado e o cadastro ou atualização segue em frente.

### 4.5 Seleção do tipo de plano
Os valores de `PlanType` são apresentados ao usuário numerados no menu, e o número digitado é convertido para o enum via `PlanType.fromOptionValue()`. Consideramos aceitar a entrada como texto e converter para o enum, o que seria mais flexível, mas a abordagem numérica é mais robusta e evita erros de digitação. O mesmo padrão é adotado para `PaymentType`.

### 4.6 `fromOptionValue` retorna null
O método `PlanType.fromOptionValue()` retorna `null` em vez de lançar exceção quando o valor não corresponde a nenhuma opção válida. A alternativa seria lançar `IllegalArgumentException`, capturada no menu com `try/catch`. Optamos por retornar `null` pois é mais consistente com o padrão adotado nos métodos de busca do sistema — `findByName()` também retorna `null` quando não encontra o objeto. A validação fica centralizada no `PlanService` via `OperationResult(false, "Tipo de plano inválido.")`.

### 4.7 Lógica de desconto por tipo de plano
O método `calculateTotalPrice(int months)` aplica descontos com base no tipo do plano: QUARTERLY 10%, SEMI_ANNUAL 20%, ANNUAL 30% e MONTHLY sem desconto. A lógica usa uma estrutura condicional `if/else` por tipo, o que é adequado para esta etapa. Essa estrutura é intencionalmente temporária — na próxima etapa, cada `PlanType` se tornará uma subclasse de `Plan` com sua própria regra de cálculo, eliminando o `if/else` por polimorfismo.

### 4.8 Encapsulamento das coleções internas
Os métodos de listagem como `listPlans()` retornam `new ArrayList<>(plans)` em vez da lista interna diretamente. A alternativa seria expor a lista diretamente, o que permitiria que classes externas a modificassem sem passar pelas validações do serviço. A cópia defensiva garante o encapsulamento da coleção e foi adotada de forma consistente em todos os serviços.

### 4.9 Métodos de busca retornam null
Métodos como `findByName()` e `findByCpf()` retornam `null` quando o objeto não é encontrado, em vez de retornar um `OperationResult` com os dados embutidos. Essa convenção foi adotada de forma consistente em todo o sistema — o chamador sempre verifica o retorno antes de usar o objeto. Os métodos públicos dos serviços que são chamados pelos menus retornam `OperationResult`; os métodos internos de busca retornam `null`.

### 4.10 Instanciação dos menus
Os menus são instanciados no início do programa, em `Main.java`, e passados como parâmetros. A alternativa seria criar cada menu sob demanda no momento em que fosse necessário. Optamos pela instanciação antecipada pois garante que todas as dependências estejam disponíveis desde o início e facilita o rastreamento do fluxo de execução. `Main.java` é responsável por criar e conectar todos os objetos necessários.

### 4.11 Campo `data` em `OperationResult`
O campo `data` é do tipo `Object` nesta etapa, permitindo retornar qualquer objeto junto com o resultado da operação. Por exemplo, `registerPlan()` retorna o `Plan` criado para que o menu o exiba sem precisar buscá-lo novamente. Consideramos não incluir o campo nesta etapa, mas optamos por mantê-lo pois o documento prevê sua evolução para um tipo genérico `T` nas etapas seguintes, e já utilizá-lo agora prepara o sistema para essa transição.

### 4.12 Pacote `validators`
As classes `CpfValidator` e `ContactValidator` foram organizadas em um pacote separado `validators`, fora das três camadas principais. Poderíamos tê-las colocado no pacote `domain` ou `application`, mas optamos pelo pacote separado pois são utilitários reutilizáveis que não pertencem exclusivamente a nenhuma das camadas. O pacote `validators` é usado tanto pela camada de domínio (`Student`) quanto pela camada de aplicação (`StudentService`).

### 4.13 Pagamento inicial mínimo
O pagamento mínimo para efetivar a matrícula foi definido como o valor exato da primeira parcela mensal. O cálculo ocorre na camada de aplicação, dentro do método enroll() do EnrollmentService. A lógica divide o valor total do contrato (com os devidos descontos do plano) pelo número de meses (plan.calculateTotalPrice(durationMonths) / durationMonths). Se o valor fornecido for menor que essa parcela, o serviço barra a criação do objeto e retorna um OperationResult de falha.

### 4.14 Data de término da matrícula
O cálculo da endDate ocorre de forma isolada dentro do construtor da classe de domínio Enrollment. O EnrollmentService não calcula datas, apenas repassa a startDate. O Domínio utiliza LocalDate, aplicando o método startDate.plusMonths(durationMonths) para definir com precisão absoluta o dia do vencimento final do contrato no momento exato em que ele é instanciado.

### 4.15 Atomicidade do fluxo de matrícula
A atomicidade é garantida pela ordem de execução dentro de EnrollmentService.enroll(). Embora as validações e a instanciação de Enrollment e Payment ocorram em etapas separadas, a persistência na memória (o this.enrollments.add()) é estritamente a última instrução executada. Se ocorrer qualquer falha nas validações anteriores ou na criação do pagamento, a execução é interrompida pelo retorno de um OperationResult de erro, impedindo que matrículas incompletas ou sem pagamento sejam salvas no sistema.

### 4.16 Quem verifica matrícula ativa
A responsabilidade por verificar a existência de matrícula ativa reside exclusivamente na camada de aplicação, dentro do EnrollmentService (através do método auxiliar hasActiveEnrollment()). O FitManager atua apenas como um orquestrador e não detém conhecimento sobre regras de unicidade. Ele repassa o CPF para o serviço, e o serviço decide se a operação prossegue ou não.

### 4.17 Quem cria o objeto `Payment`
A instanciação do objeto Payment ocorre dentro do EnrollmentService. O serviço atua como o controlador financeiro: ele recebe o valor e o PaymentType vindos do FitManager, instancia o Payment e então o injeta no domínio chamando o método registerPayment() da classe Enrollment.

### 4.18 Situação financeira após quitação
O sistema adota o bloqueio estrito de pagamentos excedentes. Dentro do EnrollmentService.registerPayment(), o sistema compara o valor da transação com o saldo devedor atual (flagEnrollment.calculateBalance()). Se a tentativa de pagamento for maior que a dívida, a operação é bloqueada com uma mensagem de erro, impedindo a geração de saldos negativos (créditos) para a academia.

### 4.19 Taxas de cancelamento
O grupo optou por manter a regra de negócio focada e não aplicar taxas punitivas para cancelamentos antecipados. A lógica de cancelamento (no EnrollmentService) altera o status do contrato para CANCELLED e gera um extrato informando o saldo devedor exato até o momento, sem acréscimo de multas, cessando a cobrança dos meses futuros.

### 4.20 Data e motivo do cancelamento
Decidimos não implementar os atributos adicionais cancellationDate e cancellationReason na entidade Enrollment. A indicação de cancelamento é controlada puramente pelo enum EnrollmentStatus.CANCELLED, o que atende plenamente ao requisito de bloquear o acesso do aluno na catraca, mantendo a estrutura da classe de domínio limpa e coesa.

### 4.21 Pendências financeiras como critério de bloqueio
A arquitetura definida exige que o StudentService consulte as regras financeiras antes de efetivar uma remoção. Alunos que possuam matrículas com status ativo, ou matrículas canceladas onde o calculateBalance() > 0, terão sua remoção bloqueada, garantindo a preservação do histórico de dívidas da academia.

### 4.22 Ordenação nas listagens
⚠️ PENDENTE — grupo deve documentar em qual camada fica a responsabilidade de ordenação das listagens e como evitar duplicação de código entre listagens similares.

### 4.23 Tratamento de entrada numérica inválida
⚠️ PENDENTE — Maria deve documentar a estratégia adotada para tratar entradas de tipo incorreto nos menus, como letras em campos numéricos.

### 4.24 Situação financeira como estado ou cálculo
Toda verificação financeira é feita por um cálculo dinâmico em tempo real através do método calculateBalance() da entidade Enrollment, que subtrai a soma do histórico de pagamentos (calculateTotalPaid()) do valor total do contrato (totalPrice).

### 4.25 Pontos de extensão para próximas etapas
O grupo identificou dois pontos principais de extensão já preparados nesta etapa. O primeiro é a evolução de `PlanType` para subclasses de `Plan` — o `if/else` em `calculateTotalPrice()` é temporário e será substituído, onde cada subclasse implementará sua própria regra de cálculo. O segundo é a conversão de `UserInterface` de classe concreta para interface Java, permitindo múltiplas implementações como terminal e interface gráfica. Os menus já referenciam `UserInterface` pelo tipo, o que facilita essa transição sem reescrita dos menus.

---

## 5. Regras de negócio implementadas

O CPF deve ser único no sistema — verificado em `StudentService.registerStudent()`. O CPF deve ser válido com verificação dos dígitos verificadores — implementado em `CpfValidator.isValidCpf()`. Todos os campos do aluno são obrigatórios — verificado em `StudentService.registerStudent()`. O nome do plano deve ser único — verificado em `PlanService.registerPlan()` via `nameExists()`. A duração mínima do plano deve ser maior que zero e o preço por mês deve ser positivo — ambos verificados em `PlanService.registerPlan()`. A alteração de preço não afeta matrículas existentes — garantida porque `totalPrice` é calculado e armazenado no momento da criação da matrícula via `plan.calculateTotalPrice(durationMonths)`. O tipo de plano deve ser válido — verificado em `PlanService.registerPlan()` via verificação de `null`.

As seguintes regras estão pendentes de implementação por Gabriel: aluno com matrícula ativa não pode ser removido; aluno não pode ter mais de uma matrícula ativa simultaneamente; duração da matrícula deve ser maior ou igual à duração mínima do plano; pagamento não pode ser registrado em matrícula cancelada; matrícula cancelada não pode voltar a ser ativa.

---

## 6. Dificuldades e aprendizados

⚠️ PENDENTE — grupo deve descrever as principais dificuldades técnicas ou organizacionais encontradas durante o desenvolvimento e como foram resolvidas.
