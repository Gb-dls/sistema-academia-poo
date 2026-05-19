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

![Diagrama de Classes Etapa 01](./diagrama-final.png)

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
As classes CpfValidator e ContactValidator foram organizadas em um pacote separado validators, fora das três camadas principais. Optamos pelo pacote separado pois são utilitários reutilizáveis que não pertencem exclusivamente a nenhuma das camadas. Para manter a entidade de domínio (Student) pura e focada apenas em seus próprios dados, a responsabilidade de acionar o pacote validators foi delegada exclusivamente para a camada de aplicação, dentro do StudentService.

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
A ordenação é tratada na camada de Aplicação (Services). Para evitar duplicação, a lógica de ordenação é executada imediatamente após operações de inserção ou atualização de dados, garantindo que as coleções em memória permaneçam consistentes. Além disso, utilizamos a API de Comparators do Java para manter o código conciso e reutilizável.
### 4.23 Tratamento de entrada numérica inválida
O tratamento de entradas com tipo incorreto é realizado de forma preventiva diretamente na camada de Aplicação (nos Services). A estratégia adotada consiste em receber os dados numéricos repassados pela interface como String e validá-los utilizando Expressões Regulares (ex: input.matches("\\d+")) antes de realizar a conversão real. Caso o formato seja inválido (como letras digitadas em campos de preço ou duração), o serviço bloqueia a operação imediatamente e retorna um OperationResult com uma mensagem de erro amigável. Essa abordagem garante que a aplicação nunca tente fazer o parse de dados corrompidos, evitando o encerramento do programa por exceções (como NumberFormatException) e mantendo o sistema estável

### 4.24 Situação financeira como estado ou cálculo
Toda verificação financeira é feita por um cálculo dinâmico em tempo real através do método calculateBalance() da entidade Enrollment, que subtrai a soma do histórico de pagamentos (calculateTotalPaid()) do valor total do contrato (totalPrice).

### 4.25 Pontos de extensão para próximas etapas
O grupo identificou dois pontos principais de extensão já preparados nesta etapa. O primeiro é a evolução de `PlanType` para subclasses de `Plan` — o `if/else` em `calculateTotalPrice()` é temporário e será substituído, onde cada subclasse implementará sua própria regra de cálculo. O segundo é a conversão de `UserInterface` de classe concreta para interface Java, permitindo múltiplas implementações como terminal e interface gráfica. Os menus já referenciam `UserInterface` pelo tipo, o que facilita essa transição sem reescrita dos menus.

---

## 5. Regras de negócio implementadas

#### Regras de Alunos e Planos:

- Unicidade do CPF: O CPF deve ser único no sistema — verificado em StudentService.registerStudent().
- Validação do CPF: O CPF deve ser válido com verificação dos dígitos verificadores — implementado em CpfValidator.isValidCpf().
- Campos Obrigatórios: Todos os campos do aluno são obrigatórios — verificado em StudentService.registerStudent().
- Unicidade do Plano: O nome do plano deve ser único — verificado em PlanService.registerPlan() via nameExists().
- Valores Positivos: A duração mínima do plano deve ser maior que zero e o preço por mês deve ser positivo — ambos verificados em PlanService.registerPlan().
- Imutabilidade do Histórico: A alteração de preço não afeta matrículas existentes — garantida porque totalPrice é calculado e armazenado no momento da criação da matrícula via plan.calculateTotalPrice(durationMonths).
- Validação de Tipo: O tipo de plano deve ser válido — verificado em PlanService.registerPlan() via verificação de null.

#### Regras de Matrícula e Financeiras:
- Bloqueio de Inativação: Um aluno com matrícula ativa ou dívida pendente não pode ser removido/inativado — verificado em StudentService.removeStudent() mediante consulta ao EnrollmentService.
- Limite de Matrículas: Um aluno não pode ter mais de uma matrícula ativa simultaneamente — verificado em EnrollmentService.enroll().
- Duração Mínima: A duração da matrícula escolhida pelo aluno deve ser maior ou igual à duração mínima exigida pelo plano — verificado em EnrollmentService.enroll().
- Trava de Pagamento: Um pagamento não pode ser registrado em uma matrícula que já encontra-se cancelada — verificado em EnrollmentService.registerPayment().
- Status Definitivo: Uma matrícula cancelada não pode voltar a ser ativa (não possui fluxo de reativação) — garantido pela arquitetura de encapsulamento e ausência de métodos de reversão de status na classe de domínio Enrollment.

---

## 6. Dificuldades e aprendizados

O desenvolvimento desta etapa trouxe desafios em frentes distintas — técnicas e organizacionais — que o grupo enfrentou ao longo do processo.

**Controle de versão com Git e GitHub**

A maior dificuldade organizacional foi o uso correto do Git. No início do projeto, o grupo não tinha familiaridade suficiente com o fluxo de branches definido — criar branches a partir de `stage-1`, abrir pull requests e evitar commits diretos na `main`. Houve situações em que alterações foram feitas diretamente em branches erradas e conflitos de merge precisaram ser resolvidos manualmente. O processo de entender como integrar o trabalho de três pessoas em um único repositório sem sobrescrever o que o colega havia feito foi, na prática, mais desafiador do que o esperado. Com o tempo, o grupo foi se adaptando ao fluxo e os commits passaram a ser mais frequentes e direcionados — mas a curva de aprendizado com o Git foi real e tomou tempo que poderia ter sido dedicado à implementação.

**Arquitetura em camadas**

Outro ponto de dificuldade foi compreender, na prática, onde cada responsabilidade deveria residir. No início, o grupo começou implementando as classes de domínio sem ter clareza sobre como a comunicação entre as camadas funcionaria. Isso gerou retrabalho: algumas lógicas foram colocadas em lugares errados e precisaram ser movidas depois que a estrutura foi melhor compreendida. Entender que o menu coleta e exibe, que o `FitManager` coordena, e que os serviços validam e executam parece simples na teoria — mas exigiu revisão constante durante a implementação para não misturar responsabilidades. A separação de camadas foi o conceito que mais demandou atenção coletiva ao longo do desenvolvimento.

**Tratamento de entradas e coordenação da interface**

A parte técnica que mais gerou dificuldade foi o tratamento de entradas inválidas nos menus e a padronização das mensagens exibidas ao usuário. Dois problemas específicos se combinaram: tratar `NumberFormatException` quando o usuário digita letras em campos numéricos e garantir que todos os menus apresentassem feedback de forma consistente — sem que cada menu tivesse sua própria lógica de exibição. Centralizar as saídas em `UserInterface` e garantir que nenhum menu chamasse `System.out` diretamente foi mais trabalhoso do que o grupo antecipou, especialmente porque envolvia coordenação entre os três membros que desenvolveram menus distintos.

**Divisão do trabalho**

A coordenação entre os membros foi um ponto positivo desta etapa. A divisão por área de responsabilidade — alunos, planos, matrículas e pagamentos — funcionou bem e permitiu que o grupo trabalhasse em paralelo sem grandes conflitos de dependência. A comunicação foi constante e não houve impasses significativos na organização do trabalho em equipe.

**O que faríamos diferente**

Se o grupo começasse novamente, investiria mais tempo antes de escrever código: estudar o fluxo do Git com branches e pull requests, e desenhar com mais cuidado como as camadas se comunicam antes de implementar qualquer classe. A decisão de começar pelo domínio sem ter o diagrama completamente compreendido gerou retrabalho que poderia ter sido evitado. Planejar antes de codificar é uma lição que o grupo leva para as próximas etapas.

---

## 7. Referências

Os seguintes materiais foram utilizados como apoio ao longo do desenvolvimento desta etapa:

- **Slides das aulas** — material disponibilizado pelo professor ao longo da disciplina, consultado como referência principal para os conceitos de orientação a objetos, arquitetura em camadas e boas práticas de projeto.
- **TURINI, Rodrigo. *Desbravando Java e Orientação a Objetos: Um guia para o iniciante da linguagem*.** Casa do Código. Utilizado como referência de apoio para conceitos de POO aplicados à linguagem Java.
- ***Java — Ensinando o Básico*.** Material complementar consultado para revisão de sintaxe e recursos fundamentais da linguagem.
- **Claude (Anthropic), ChatGPT (OpenAI) e Gemini (Google)** — ferramentas de IA utilizadas como auxílio durante o desenvolvimento, para tirar dúvidas pontuais sobre sintaxe, revisar lógica de implementação e apoiar a escrita da documentação.


------------------------

# Relatório da Etapa 2: Refatoração FitManager

## 1. Introdução da Etapa 2
Nesta segunda etapa, o sistema FitManager passou por uma refatoração arquitetural profunda para incorporar conceitos essenciais de Programação Orientada a Objetos: herança, classes abstratas, polimorfismo e interfaces.

As entidades `Plan` e `Payment` deixaram de ser classes concretas genéricas com enums informativos e tornaram-se superclasses abstratas, dando origem a hierarquias especializadas. Além disso, a classe `UserInterface` foi transformada em uma interface Java pura, permitindo múltiplas implementações de visualização (Console e Caixa de Diálogo). O objetivo foi eliminar estruturas condicionais baseadas em tipo, aumentar a coesão e preparar o sistema para evoluções futuras.

---

## 2. Integrantes e Contribuições
* **Maria:** Responsável pela conversão da `UserInterface` em interface Java e implementação das classes `TerminalUI` e `JOptionPaneUI`, além da adequação dos menus.
* **Marcelly:** Responsável pela refatoração da hierarquia de Planos (`Plan` abstrata, `MonthlyPlan`, `QuarterlyPlan`, `SemiAnnualPlan`, `AnnualPlan`), lógica matemática de descontos e multas.
* **Gabriel:** Responsável pela refatoração da hierarquia de Pagamentos (`Payment` abstrata e subclasses) e lógica de resumo e troco.

---

## 3. Diagrama de Classes Final

![Diagrama de Classes Etapa 01](./diagrama-stage-2.png)
---

## 4. Decisões de Projeto da Etapa 2

### 4.1. Hierarquia de Planos (Plan)
* **Padronização nos planos e definição de diferenças:** Decidimos que os atributos `name`, `description`, `minDurationMonths` e `pricePerMonth` são universais e pertencem à superclasse abstrata `Plan`. Os métodos `calculateTotalPrice(months)` e `getCancellationFee(enrollment)` variam conforme o tipo e foram declarados como abstratos.
* **Papel do enum PlanType no novo modelo:** Decidimos remover o `PlanType`. Com a introdução das subclasses, o enum tornou-se redundante e adicionava complexidade desnecessária. A escolha do usuário no menu passou a transitar como um número inteiro para o serviço.
* **Estratégia de instanciação da subclasse correta no serviço:** No `PlanService`, adotamos uma estrutura condicional (`if`/`else`) atuando com o número escolhido no menu. Essa abordagem foi preferida por isolar a regra de criação e manter o menu livre do conhecimento sobre as subclasses de domínio.
* **Apresentação amigável dos tipos de plano ao usuário:** No `PlanMenu`, substituímos a listagem do Enum por uma exibição textual amigável e numerada (1 - Mensal, 2 - Trimestral, etc.), melhorando a usabilidade e a clareza da interface.
* **Encapsulamento da taxa de cancelamento via objeto Enrollment:** O método `getCancellationFee(Enrollment enrollment)` recebe o objeto completo em vez de atributos soltos. Essa passagem de contexto reduz o acoplamento, permitindo extrair dados dinamicamente e blindando a assinatura do método contra futuras mudanças nas regras de negócio.
* **Isolamento do cálculo de tempo cumprido:** Criamos um método auxiliar na classe `Enrollment`, que utiliza a API `java.time.temporal.ChronoUnit` para calcular se metade do período já passou. Isso abstraiu o cálculo de datas das regras dos planos.
* **Independência entre taxa de cancelamento e saldo pendente:** A taxa representa uma quebra de contrato atrelada ao benefício oferecido. Ela é calculada e exibida independentemente de o aluno ter saldo devedor das mensalidades passadas, consistindo em uma cobrança administrativa extra.

### 4.2. Hierarquia de Pagamentos (Payment)
* **Padronização e definição de diferenças:** O atributo `amount` (valor nominal da transação) é universal e abstrato, pertencendo à superclasse `Payment`. As subclasses encapsulam dados específicos de seus canais: `CashPayment` (montante físico entregue), `PixPayment` (chave de transação) e variantes de cartão (dados do titular, bandeira e parcelas).
* **Responsabilidade pela taxa de processamento:** A academia absorve a taxa administrativa das operadoras. Exemplo: um pagamento de 100,00 reais no cartão com taxa de 5% (5 reais) terá apenas 95,00 reais líquidos abatidos do saldo devedor via `calculateTotalPaid()`, mantendo R$ 5,00 como pendência contábil.
* **Uso de getPaymentSummary() em vez de toString():** Evitamos sobrecarregar o `toString()` (projetado para debugging). O método `getPaymentSummary()` permite que cada subclasse formate sua saída polimorficamente com termos comerciais.
* **Tratamento de métodos exclusivos em subclasses:** Encapsulamos o `getChange()` estritamente em `CashPayment`. No `EnrollmentService`, usamos `instanceof` (Pattern Matching do Java moderno) pontualmente para extrair o troco de forma segura.
* **Papel do enum PaymentType:** Removido do modelo de domínio, pois violava o Princípio do Polimorfismo Aberto/Fechado (OCP). A própria instância da subclasse já define o tipo de pagamento.
* **Localização das validações específicas:** Validações financeiras (ex: `received < amount`) foram posicionadas no `EnrollmentService` e na UI, evitando lançar exceções no construtor de objetos de entidade.
* **Responsabilidade pela formatação de saída:** `getPaymentSummary()` gera uma String formatada dentro da classe de domínio, mantendo os dados brutos privados e entregando uma representação contextualizada.
* **Transporte de dados do pagamento inicial:** O `EnrollmentMenu` utiliza parâmetros posicionais e strings adicionais. O serviço avalia e faz o parse correspondente antes de invocar o construtor correto.

### 4.3. Interface de Usuário (UserInterface)
* **Escolha entre Interface e Classe Abstrata:** Implementada como interface Java. `TerminalUI` e `JOptionPaneUI` não compartilham atributos ou comportamentos, invalidando o uso de classe abstrata.
* **Estratégia de escolha inicial:** Acontece logo no `main`, antes da criação dos menus, sendo o único ponto de acoplamento direto com a interface gráfica.
* **Garantia de consistência:** O mesmo comportamento e validações foram mantidos em ambas as UIs.
* **Gestão de retornos nulos no JOptionPane:** Retornos nulos (fechar ou cancelar) são tratados como strings vazias. Validações com `isEmpty()` impedem cadastros incompletos, interrompendo o fluxo.
* **Adaptação do contrato:** Os quatro métodos originais da interface foram suficientes para atender terminal e interface gráfica.
* **Viabilidade de persistência:** Seria possível salvar a preferência do usuário em arquivo, já que a escolha acontece apenas no `main`.

### 4.4. Arquitetura, Organização e Outros
* **Estratégia de refatoração incremental:** Refatoramos primeiro `Plan` (garantindo `PlanService`), depois `Payment` e, por último, `UserInterface`.
* **Refatoração da lógica de relatórios:** Separação entre busca de dados e formatação. Métodos como `listStudentsWithDebt()` retornam listas validadas; a formatação fica no `ReportsMenu`.
* **Transparência na exibição:** O `toString()` de `Plan` exibe o valor bruto e o valor com desconto lado a lado.
* **Ordem de operações no cancelamento:** Toda a lógica foi internalizada no método `.cancel()` de `Enrollment`, evitando "Anemia de Domínio".
* **Exibição condicional da taxa nula:** Taxas zeradas aparecem como "Isento de multas rescisórias" por transparência.
* **Tratamento de situações com instanceof:** Restrito estritamente a verificar `CashPayment` para troco.
* **Organização em subpacotes:** Criados `domain.plan` e `domain.payment` para agrupar classes afins.
* **Centralização de formatações:** A classe `DateFormatter` centraliza lógicas de limpeza e formatação.

---

## 5. Como o polimorfismo simplificou o código
A aplicação de polimorfismo eliminou estruturas condicionais que amarravam as regras de negócio aos serviços.

**Cenário sem polimorfismo (Etapa 1):**
```java
if (plan.getType() == PlanType.QUARTERLY) {
    preco = preco * 0.95;
} else if (plan.getType() == PlanType.ANNUAL) { 
    preco = preco * 0.85; 
}
```

Com a refatoração, o serviço usa apenas `enrollment.getPlan().calculateTotalPrice(months)`. A responsabilidade fica encapsulada na subclasse concreta instanciada. Nenhuma mudança no `EnrollmentService` precisará ser feita caso a academia adicione novos planos.

## 6. Regras de Negócio Implementadas nesta Etapa

* **Aplicação Inclusiva de Descontos:** O desconto é garantido sempre que os meses contratados forem maiores ou iguais à carência mínima (`months >= minDurationMonths`).
* **Restrição de Taxas em Planos sem Benefício:** Retorno isento (`0.0`) para a multa do plano Mensal via sobrescrita.
* **Consistência de Fluxo de Caixa:** Travas estritas em pagamentos em dinheiro (`received < amount`) e exigência de preenchimento obrigatório de chaves/dados em pagamentos eletrônicos. Pagamentos avulsos não podem superar o saldo devedor.

---

## 7. Funcionalidades Extras

###  Funcionalidade 1: Política de Taxa de Cancelamento Progressiva
* **O que foi implementado:** Multa por quebra de tempo mínimo para todos os planos longos, atrelada em 5% acima do desconto oferecido (Trimestral = 10%, Semestral = 15%, Anual = 20%), usando o método protegido `calculatePercentageFee` na superclasse.
* **Agrega valor?** Sim. Penaliza a quebra de contrato e protege a academia.
* **Arquitetura/Impacto:** Protegida no pacote `domain.plan`, usa herança e não impacta outras classes.

###  Funcionalidade 2: Fluxo Inteligente de Descoberta de Matrícula por CPF
* **O que foi implementado:** Fluxos de pagamento avulso e cancelamento agora solicitam o CPF e o sistema localiza automaticamente a matrícula via `findActiveEnrollmentByStudent(cpf)`.
* **Agrega valor?** Sim. Otimiza radicalmente a usabilidade do operador de caixa.
* **Arquitetura/Impacto:** A UI apenas coleta o dado; o controlador gerencia a ponte. Mínimo impacto nas classes existentes.

### Funcionalidade 3: Sistema de Bloqueio de Inadimplência e Flexibilização de Caixa
* **O que foi implementado:** O método `hasDebt()` bloqueia novas matrículas de alunos inadimplentes. O método `registerPayment()` foi reescrito para aceitar pagamentos de matrículas `CANCELLED` com saldo devedor.
* **Agrega valor?** Sim. Protege a saúde financeira da empresa e permite recuperar créditos de contratos já encerrados.
* **Arquitetura/Impacto:** Regras executadas no `EnrollmentService`, controlando consistentemente o ciclo de vida das entidades.

---

## 8. Dificuldades e Aprendizados da Etapa 2

**Gestão de Conflitos e Consistência de Estados:**
Nesta etapa, o grupo enfrentou desafios significativos na resolução de *Merge Conflicts* no Git, decorrentes da edição simultânea de arquivos centrais, aprendendo a conciliar alterações estruturais com correções pontuais. Também tivemos dificuldade em padronizar a emissão de mensagens descritivas via `OperationResult` em todas as camadas, mantendo o sistema livre de estados inconsistentes.

**Sinergia do Desenvolvimento:** Outro desafio profundo foi integrar as três frentes de refatoração (`Plan`, `Payment` e `UI`). Aprendemos que adotar uma estratégia incremental e utilizar o diagrama de classes como ferramenta viva de projeto foram essenciais para antecipar acoplamentos.

**A Realidade da Refatoração:** Por fim, descobrimos na prática que refatorar um código já existente é consideravelmente mais complexo do que criá-lo do zero, exigindo intenso alinhamento arquitetural de todo o grupo.