package br.edu.ifba.pedidos.observer;

import br.edu.ifba.pedidos.domain.Pedido;
import br.edu.ifba.pedidos.domain.StatusPedido;

import java.io.PrintStream;

public final class NotificacaoClienteObserver implements PedidoObserver {
    private final PrintStream saida;

    public NotificacaoClienteObserver(PrintStream saida) {
        this.saida = saida;
    }

    @Override
    public void aoAlterarStatus(Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus) {
        saida.printf(
                "Notificação: %s, o pedido #%d mudou de %s para %s.%n",
                pedido.getCliente().getNome(),
                pedido.getId(),
                statusAnterior,
                novoStatus);
    }
}
