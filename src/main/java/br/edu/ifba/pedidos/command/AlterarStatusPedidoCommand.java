package br.edu.ifba.pedidos.command;

import br.edu.ifba.pedidos.domain.Pedido;
import br.edu.ifba.pedidos.domain.StatusPedido;

public final class AlterarStatusPedidoCommand implements Comando {
    private final Pedido pedido;
    private final StatusPedido novoStatus;

    public AlterarStatusPedidoCommand(Pedido pedido, StatusPedido novoStatus) {
        this.pedido = pedido;
        this.novoStatus = novoStatus;
    }

    @Override
    public void executar() {
        pedido.alterarStatus(novoStatus);
    }

    @Override
    public String getDescricao() {
        return "Alterar pedido #" + pedido.getId() + " para " + novoStatus;
    }
}
