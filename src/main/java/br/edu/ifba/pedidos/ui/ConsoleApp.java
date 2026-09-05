package br.edu.ifba.pedidos.ui;

import br.edu.ifba.pedidos.domain.Cliente;
import br.edu.ifba.pedidos.domain.ItemPedido;
import br.edu.ifba.pedidos.domain.Pedido;
import br.edu.ifba.pedidos.domain.PedidoBuilder;
import br.edu.ifba.pedidos.domain.StatusPedido;
import br.edu.ifba.pedidos.domain.entrega.EntregaPac;
import br.edu.ifba.pedidos.domain.entrega.EntregaSedex;
import br.edu.ifba.pedidos.domain.entrega.EntregaTransportadora;
import br.edu.ifba.pedidos.domain.entrega.ModalidadeEntrega;
import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;
import br.edu.ifba.pedidos.domain.pagamento.factory.CriadorBoleto;
import br.edu.ifba.pedidos.domain.pagamento.factory.CriadorCartaoAVista;
import br.edu.ifba.pedidos.domain.pagamento.factory.CriadorCartaoParcelado;
import br.edu.ifba.pedidos.domain.pagamento.factory.CriadorPix;
import br.edu.ifba.pedidos.facade.SistemaPedidosFacade;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public final class ConsoleApp {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final SistemaPedidosFacade facade;
    private final Scanner entrada;
    private final PrintStream saida;

    public ConsoleApp(SistemaPedidosFacade facade, Scanner entrada, PrintStream saida) {
        this.facade = facade;
        this.entrada = entrada;
        this.saida = saida;
    }

    public void executar() {
        saida.println("========================================");
        saida.println("       SISTEMA DE PEDIDOS - IFBA");
        saida.println("========================================");

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ", 0, 5);

            try {
                switch (opcao) {
                    case 1:
                        criarPedido();
                        break;
                    case 2:
                        listarPedidos();
                        break;
                    case 3:
                        detalharPedido();
                        break;
                    case 4:
                        alterarStatus();
                        break;
                    case 5:
                        exibirAuditoria();
                        break;
                    case 0:
                        saida.println("Sistema encerrado.");
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                saida.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        saida.println();
        saida.println("1 - Criar pedido");
        saida.println("2 - Listar pedidos");
        saida.println("3 - Consultar pedido e valor final");
        saida.println("4 - Mudar status do pedido");
        saida.println("5 - Exibir auditoria de status");
        saida.println("0 - Sair");
    }

    private void criarPedido() {
        saida.println("\n--- Novo pedido ---");
        Cliente cliente = new Cliente(
                lerTextoObrigatorio("Nome do cliente: "),
                lerTextoObrigatorio("E-mail do cliente: "));

        PedidoBuilder builder = new PedidoBuilder().cliente(cliente);
        boolean adicionarOutro;
        do {
            String produto = lerTextoObrigatorio("Produto: ");
            int quantidade = lerInteiro("Quantidade: ", 1, Integer.MAX_VALUE);
            double preco = lerDecimalPositivo("Preço unitário: R$ ");
            builder.adicionarItem(new ItemPedido(produto, quantidade, preco));
            adicionarOutro = lerSimNao("Adicionar outro item? (s/n): ");
        } while (adicionarOutro);

        builder.formaPagamento(selecionarFormaPagamento());
        builder.modalidadeEntrega(selecionarModalidadeEntrega());

        Pedido pedido = facade.criarPedido(builder);
        saida.printf("Pedido #%d criado com sucesso. Total: %s%n",
                pedido.getId(), formatarMoeda(pedido.getValorTotal()));
    }

    private FormaPagamento selecionarFormaPagamento() {
        saida.println("\nForma de pagamento:");
        saida.println("1 - Pix (10% de desconto)");
        saida.println("2 - Boleto (5% de desconto)");
        saida.println("3 - Cartão à vista (sem desconto)");
        saida.println("4 - Cartão parcelado (5% de acréscimo)");

        int opcao = lerInteiro("Escolha: ", 1, 4);
        switch (opcao) {
            case 1:
                return new CriadorPix().criar();
            case 2:
                return new CriadorBoleto().criar();
            case 3:
                return new CriadorCartaoAVista().criar();
            case 4:
                return new CriadorCartaoParcelado().criar();
            default:
                throw new IllegalArgumentException("Forma de pagamento inválida.");
        }
    }

    private ModalidadeEntrega selecionarModalidadeEntrega() {
        saida.println("\nModalidade de entrega:");
        saida.println("1 - PAC (5% do pedido)");
        saida.println("2 - Sedex (10% do pedido)");
        saida.println("3 - Transportadora (15% do pedido)");

        int opcao = lerInteiro("Escolha: ", 1, 3);
        switch (opcao) {
            case 1:
                return new EntregaPac();
            case 2:
                return new EntregaSedex();
            case 3:
                return new EntregaTransportadora();
            default:
                throw new IllegalArgumentException("Modalidade de entrega inválida.");
        }
    }

    private void listarPedidos() {
        List<Pedido> pedidos = facade.listarPedidos();
        saida.println("\n--- Pedidos ---");
        if (pedidos.isEmpty()) {
            saida.println("Nenhum pedido cadastrado.");
            return;
        }

        saida.printf("%-6s %-25s %-24s %12s%n", "ID", "Cliente", "Status", "Total");
        for (Pedido pedido : pedidos) {
            saida.printf(
                    "#%-5d %-25s %-24s %12s%n",
                    pedido.getId(),
                    limitar(pedido.getCliente().getNome(), 25),
                    pedido.getStatus(),
                    formatarMoeda(pedido.getValorTotal()));
        }
    }

    private void detalharPedido() {
        int id = lerInteiro("ID do pedido: ", 1, Integer.MAX_VALUE);
        Pedido pedido = facade.consultarPedido(id);

        saida.println("\n--- Pedido #" + pedido.getId() + " ---");
        saida.println("Cliente: " + pedido.getCliente().getNome());
        saida.println("E-mail: " + pedido.getCliente().getEmail());
        saida.println("Criado em: " + pedido.getDataCriacao().format(FORMATO_DATA));
        saida.println("Status: " + pedido.getStatus());
        saida.println("Pagamento: " + pedido.getFormaPagamento().getDescricao());
        saida.println("Entrega: " + pedido.getModalidadeEntrega().getDescricao());
        saida.println("Itens:");
        for (ItemPedido item : pedido.getItens()) {
            saida.printf("  - %s | %d x %s = %s%n",
                    item.getProduto(),
                    item.getQuantidade(),
                    formatarMoeda(item.getPrecoUnitario()),
                    formatarMoeda(item.calcularSubtotal()));
        }
        saida.println("Subtotal: " + formatarMoeda(pedido.getSubtotal()));
        saida.println("Desconto: -" + formatarMoeda(pedido.getDesconto()));
        saida.println("Acréscimo do pagamento: +" + formatarMoeda(pedido.getAcrescimoPagamento()));
        saida.println("Frete: +" + formatarMoeda(pedido.getValorFrete()));
        saida.println("VALOR FINAL: " + formatarMoeda(pedido.getValorTotal()));
    }

    private void alterarStatus() {
        int id = lerInteiro("ID do pedido: ", 1, Integer.MAX_VALUE);
        Pedido pedido = facade.consultarPedido(id);
        saida.println("Status atual: " + pedido.getStatus());
        StatusPedido[] opcoes = StatusPedido.values();
        for (int i = 0; i < opcoes.length; i++) {
            saida.printf("%d - %s%n", i + 1, opcoes[i]);
        }

        int escolha = lerInteiro("Novo status: ", 1, opcoes.length);
        facade.alterarStatus(id, opcoes[escolha - 1]);
        saida.println("Status atualizado com sucesso.");
    }

    private void exibirAuditoria() {
        List<String> eventos = facade.consultarAuditoriaDeStatus();
        saida.println("\n--- Auditoria de status ---");
        if (eventos.isEmpty()) {
            saida.println("Nenhuma alteração registrada.");
            return;
        }
        eventos.forEach(saida::println);
    }

    private String lerTextoObrigatorio(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String valor = entrada.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            saida.println("O valor é obrigatório.");
        }
    }

    private int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            saida.print(mensagem);
            String texto = entrada.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException ignored) {
                // A mensagem comum de validação é apresentada abaixo.
            }
            saida.printf("Informe um número inteiro entre %d e %d.%n", minimo, maximo);
        }
    }

    private double lerDecimalPositivo(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String texto = entrada.nextLine().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (valor > 0) {
                    return valor;
                }
            } catch (NumberFormatException ignored) {
                // A mensagem comum de validação é apresentada abaixo.
            }
            saida.println("Informe um valor monetário maior que zero.");
        }
    }

    private boolean lerSimNao(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String resposta = entrada.nextLine().trim().toLowerCase();
            if (resposta.equals("s")) {
                return true;
            }
            if (resposta.equals("n")) {
                return false;
            }
            saida.println("Digite 's' para sim ou 'n' para não.");
        }
    }

    private String formatarMoeda(double valor) {
        return String.format("R$ %.2f", valor);
    }

    private String limitar(String texto, int tamanho) {
        if (texto.length() <= tamanho) {
            return texto;
        }
        return texto.substring(0, tamanho - 3) + "...";
    }
}
