# Sistema de Pedidos em Java

Projeto final da disciplina **Padrões de Projeto**. A aplicação simula, no console, o gerenciamento e o processamento de pedidos de uma loja. Todo o código funcional foi escrito em Java, sem banco de dados, interface gráfica, aplicação web ou framework.

Discente
Nome: João Gabriel Passos Ramos
Matrícula: 20232TADSSAJ0032

Funcionalidades

- criar pedidos com cliente e um ou mais itens;
- listar os pedidos mantidos em memória;
- consultar todos os dados e o valor final de um pedido;
- selecionar Pix, boleto, cartão à vista ou cartão parcelado;
- selecionar entrega por PAC, Sedex ou transportadora;
- alterar o status respeitando as transições válidas;
- registrar e exibir uma auditoria das mudanças de status.

Regras de cálculo

O subtotal é a soma de `quantidade x preço unitário` de todos os itens. Desconto, acréscimo e frete são calculados sobre esse subtotal:

```text
valor final = subtotal - desconto + acréscimo do pagamento + frete
```

| Pagamento | Regra |
|---|---:|
| Pix | 10% de desconto |
| Boleto | 5% de desconto |
| Cartão à vista | sem desconto ou acréscimo |
| Cartão parcelado | 5% de acréscimo |

| Entrega | Frete |
|---|---:|
| PAC | 5% do subtotal |
| Sedex | 10% do subtotal |
| Transportadora | 15% do subtotal |

Os valores monetários usam `double`, como nas estruturas de classes sugeridas pelo enunciado, e são exibidos com duas casas decimais.

Como executar

Requisitos

- JDK 17 ou superior;
- Maven 3.9 ou superior, caso seja usada a opção com Maven.

Com Maven

Na raiz do projeto:

```bash
mvn clean package
java -jar target/sistema-pedidos-1.0.0.jar
```

Somente com o JDK

No PowerShell, na raiz do projeto:

```powershell
$fontes = (Get-ChildItem src/main/java -Recurse -Filter *.java).FullName
New-Item -ItemType Directory -Force build/classes | Out-Null
javac -encoding UTF-8 -d build/classes $fontes
java -cp build/classes br.edu.ifba.pedidos.App
```

Como executar a verificação automatizada

O projeto inclui um cenário sem bibliotecas externas que confere as quatro formas de pagamento, as três modalidades de entrega, o armazenamento em memória, o fluxo de status, o Observer e o Command.

```powershell
$fontes = (Get-ChildItem src/main/java,src/test/java -Recurse -Filter *.java).FullName
New-Item -ItemType Directory -Force build/test-classes | Out-Null
javac -encoding UTF-8 -d build/test-classes $fontes
java -ea -cp build/test-classes br.edu.ifba.pedidos.TesteSistemaPedidos
```

O resultado esperado é:

```text
TODOS OS TESTES PASSARAM
```

Organização do código

```text
src/main/java/br/edu/ifba/pedidos
├── App.java                    # composição dos objetos e ponto de entrada
├── domain                     # entidades e regras de negócio
│   ├── entrega                # estratégias de frete
│   └── pagamento              # estratégias e fábricas de pagamento
├── command                    # comandos de alteração de status
├── observer                   # reações às alterações de status
├── facade                     # operações e lista de pedidos em memória
└── ui                         # menu e entrada/saída de console
```

Padrões de projeto utilizados

Foram usados **seis padrões distintos**: dois criacionais, um estrutural e três comportamentais.

1. Builder — criacional

**Problema:** um pedido possui cliente, uma coleção variável de itens, forma de pagamento e modalidade de entrega. Um construtor com todos esses argumentos seria extenso, pouco legível e permitiria objetos incompletos.

**Por que foi escolhido:** o Builder permite montar o pedido gradualmente conforme os dados são informados no console e centraliza a validação dos campos obrigatórios.

**Como foi implementado:** `PedidoBuilder` expõe operações encadeáveis para definir o cliente, adicionar itens, escolher o pagamento e escolher a entrega. `construir(int)` valida o estado e somente então instancia `Pedido`.

**Classes envolvidas:** `PedidoBuilder`, `Pedido`, `Cliente` e `ItemPedido`.

**Benefícios:** criação legível, encapsulamento do construtor de `Pedido` e garantia de que nenhum pedido seja criado sem os dados mínimos.

**Localização:** `domain/PedidoBuilder.java` e uso em `ui/ConsoleApp.java`.

2. Factory Method — criacional

**Problema:** a interface de console precisa obter diferentes objetos de pagamento sem conhecer detalhes da construção de cada estratégia.

**Por que foi escolhido:** separa a decisão sobre o tipo de pagamento da instanciação da implementação concreta e permite adicionar uma nova criação por meio de outra subclasse.

**Como foi implementado:** `CriadorFormaPagamento` define a operação pública `criar()` e declara o Factory Method protegido `criarFormaPagamento()`. Cada criador concreto sobrescreve esse método e produz a estratégia correspondente.

**Classes envolvidas:** `CriadorFormaPagamento`, `CriadorPix`, `CriadorBoleto`, `CriadorCartaoAVista`, `CriadorCartaoParcelado` e as implementações de `FormaPagamento`.

**Benefícios:** criação desacoplada, classes pequenas e extensão sem modificar os criadores existentes.

**Localização:** pacote `domain/pagamento/factory` e método `selecionarFormaPagamento()` de `ui/ConsoleApp.java`.

3. Facade — estrutural

**Problema:** a interface de console teria de controlar diretamente a lista de pedidos, as buscas, os objetos Command e a auditoria.

**Por que foi escolhido:** fornece um ponto de entrada simples para os casos de uso e esconde a colaboração entre subsistemas.

**Como foi implementado:** `SistemaPedidosFacade` mantém a lista de pedidos em memória e oferece as operações `criarPedido`, `listarPedidos`, `consultarPedido`, `alterarStatus` e `consultarAuditoriaDeStatus`. As mudanças de status são delegadas ao executor de comandos.

**Classes envolvidas:** `SistemaPedidosFacade`, `ExecutorComandos`, `AlterarStatusPedidoCommand` e `AuditoriaStatusObserver`.

**Benefícios:** baixo acoplamento da camada de apresentação, API de aplicação coesa e facilidade para substituir a interface de console no futuro.

**Localização:** `facade/SistemaPedidosFacade.java` e seu uso em `ui/ConsoleApp.java`.

4. Strategy — comportamental

**Problema:** descontos, acréscimos e fretes variam de acordo com as opções selecionadas. Condicionais dentro de `Pedido` tornariam a classe grande e exigiriam sua alteração para toda nova regra.

**Por que foi escolhido:** transforma cada algoritmo de cálculo em um objeto intercambiável e explora composição e polimorfismo.

**Como foi implementado:** `FormaPagamento` representa a estratégia de ajuste financeiro, com quatro implementações. `ModalidadeEntrega` representa a estratégia de frete, com três implementações. `Pedido` recebe as duas abstrações e calcula o total sem verificar tipos concretos.

**Classes envolvidas:** `FormaPagamento`, `PagamentoPix`, `PagamentoBoleto`, `PagamentoCartaoAVista`, `PagamentoCartaoParcelado`, `ModalidadeEntrega`, `EntregaPac`, `EntregaSedex`, `EntregaTransportadora` e `Pedido`.

**Benefícios:** elimina grandes condicionais de cálculo, isola cada regra e atende aos princípios Aberto/Fechado e Inversão de Dependência.

**Localização:** pacotes `domain/pagamento` e `domain/entrega`, além do cálculo no construtor de `domain/Pedido.java`.

5. Command — comportamental

**Problema:** a alteração de status é uma ação importante que deve ser encapsulada e registrada, em vez de ser disparada de forma dispersa pela interface.

**Por que foi escolhido:** representa cada solicitação de mudança como um objeto executável e permite que um invocador mantenha histórico das ações concluídas.

**Como foi implementado:** `AlterarStatusPedidoCommand` guarda o pedido e o novo status. `ExecutorComandos` executa o comando e registra sua descrição somente depois de uma execução bem-sucedida.

**Classes envolvidas:** `Comando`, `AlterarStatusPedidoCommand`, `ExecutorComandos`, `Pedido` e `SistemaPedidosFacade`.

**Benefícios:** separação entre solicitação e execução, histórico centralizado e abertura para funcionalidades futuras como fila ou desfazer.

**Localização:** pacote `command` e método `alterarStatus()` de `facade/SistemaPedidosFacade.java`.

6. Observer — comportamental

**Problema:** uma mudança de status precisa gerar mais de uma reação — registrar auditoria e avisar o cliente — sem acoplar `Pedido` a essas tarefas.

**Por que foi escolhido:** permite que interessados independentes sejam notificados automaticamente quando o estado do pedido muda.

**Como foi implementado:** `Pedido` mantém observadores definidos pela interface `PedidoObserver`. Depois de uma transição válida, notifica `AuditoriaStatusObserver` e `NotificacaoClienteObserver`.

**Classes envolvidas:** `PedidoObserver`, `AuditoriaStatusObserver`, `NotificacaoClienteObserver`, `Pedido` e `App`.

**Benefícios:** novas reações podem ser incluídas sem modificar `Pedido`; auditoria e notificação permanecem coesas e independentes.

**Localização:** pacote `observer`, métodos `adicionarObserver()` e `alterarStatus()` de `domain/Pedido.java`, e registro em `App.java`.

POO, SOLID, coesão e acoplamento

- Os campos das entidades são privados e os objetos de valor são imutáveis sempre que possível.
- As regras variáveis dependem de interfaces e são recebidas por composição.
- Cada classe possui uma responsabilidade principal: domínio, cálculo, comando, observação ou apresentação.
- Novas estratégias de pagamento, entrega e novos observadores podem ser adicionados sem modificar `Pedido`.
- A interface de console conhece a Facade em vez de coordenar todos os subsistemas.
- Transições inválidas de status, pedidos incompletos e entradas incorretas são rejeitados com mensagens claras.

Fluxo de status

```text
CRIADO -> AGUARDANDO_PAGAMENTO -> PAGO -> EM_PREPARACAO -> ENVIADO -> ENTREGUE
   |               |                |              |
   +---------------+----------------+--------------+----> CANCELADO
```

`ENTREGUE` e `CANCELADO` são estados finais.

Matriz de atendimento

| Requisito | Implementação principal |
|---|---|
| Criar pedidos | `PedidoBuilder`, `SistemaPedidosFacade` e `ConsoleApp` |
| Listar pedidos | lista em memória de `SistemaPedidosFacade` |
| Calcular valor final | `Pedido`, estratégias de pagamento e entrega |
| Selecionar pagamento | Factory Method e menu de console |
| Selecionar entrega | estratégias de `ModalidadeEntrega` |
| Mudar status | `AlterarStatusPedidoCommand` e regras de `StatusPedido` |
| Dados em memória | lista de `Pedido` mantida por `SistemaPedidosFacade` |
| Aplicação em console | `ConsoleApp` e `App` |

Observação sobre armazenamento

Os pedidos permanecem somente durante a execução. Ao encerrar o programa, todos os dados são descartados, conforme solicitado no enunciado.


Observação final
Para o readme foi utilizada a ajuda do Gemini para organizar as informações de forma compreensível. 