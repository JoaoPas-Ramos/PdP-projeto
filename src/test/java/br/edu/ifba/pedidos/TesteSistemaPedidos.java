package br.edu.ifba.pedidos;

import br.edu.ifba.pedidos.command.ExecutorComandos;
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
import br.edu.ifba.pedidos.observer.AuditoriaStatusObserver;

import java.util.Collections;

public final class TesteSistemaPedidos {
    private TesteSistemaPedidos() {
    }

    public static void main(String[] args) {
        AuditoriaStatusObserver auditoria = new AuditoriaStatusObserver();
        ExecutorComandos executor = new ExecutorComandos();
        SistemaPedidosFacade facade = new SistemaPedidosFacade(
                executor,
                auditoria,
                Collections.singletonList(auditoria));

        Pedido pixPac = criarPedido(facade, new CriadorPix().criar(), new EntregaPac(), 100.00);
        verificarValor("Pix + PAC - desconto", 10.00, pixPac.getDesconto());
        verificarValor("Pix + PAC - frete", 5.00, pixPac.getValorFrete());
        verificarValor("Pix + PAC - total", 95.00, pixPac.getValorTotal());

        Pedido boletoSedex = criarPedido(facade, new CriadorBoleto().criar(), new EntregaSedex(), 200.00);
        verificarValor("Boleto + Sedex - total", 210.00, boletoSedex.getValorTotal());

        Pedido vistaTransportadora = criarPedido(
                facade,
                new CriadorCartaoAVista().criar(),
                new EntregaTransportadora(),
                100.00);
        verificarValor("Cartão à vista + transportadora - total", 115.00, vistaTransportadora.getValorTotal());

        Pedido parceladoPac = criarPedido(
                facade,
                new CriadorCartaoParcelado().criar(),
                new EntregaPac(),
                100.00);
        verificarValor("Cartão parcelado + PAC - total", 110.00, parceladoPac.getValorTotal());

        facade.alterarStatus(pixPac.getId(), StatusPedido.AGUARDANDO_PAGAMENTO);
        facade.alterarStatus(pixPac.getId(), StatusPedido.PAGO);
        facade.alterarStatus(pixPac.getId(), StatusPedido.EM_PREPARACAO);
        facade.alterarStatus(pixPac.getId(), StatusPedido.ENVIADO);
        facade.alterarStatus(pixPac.getId(), StatusPedido.ENTREGUE);

        verificar(pixPac.getStatus() == StatusPedido.ENTREGUE, "O fluxo de status deve terminar em ENTREGUE.");
        verificar(auditoria.getEventos().size() == 5, "O Observer deve registrar cinco mudanças de status.");
        verificar(executor.getHistorico().size() == 5, "O Command deve registrar cinco comandos executados.");
        verificar(facade.listarPedidos().size() == 4, "O repositório deve manter quatro pedidos em memória.");

        boolean transicaoInvalidaBloqueada = false;
        try {
            facade.alterarStatus(parceladoPac.getId(), StatusPedido.ENTREGUE);
        } catch (IllegalStateException e) {
            transicaoInvalidaBloqueada = true;
        }
        verificar(transicaoInvalidaBloqueada, "Uma transição de status inválida deve ser bloqueada.");

        System.out.println("TODOS OS TESTES PASSARAM");
    }

    private static Pedido criarPedido(
            SistemaPedidosFacade facade,
            FormaPagamento pagamento,
            ModalidadeEntrega entrega,
            double preco) {
        return facade.criarPedido(new PedidoBuilder()
                .cliente(new Cliente("Cliente Teste", "teste@example.com"))
                .adicionarItem(new ItemPedido("Produto Teste", 1, preco))
                .formaPagamento(pagamento)
                .modalidadeEntrega(entrega));
    }

    private static void verificarValor(String descricao, double esperado, double atual) {
        verificar(Math.abs(esperado - atual) < 0.001,
                descricao + ": esperado " + esperado + ", obtido " + atual + ".");
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
