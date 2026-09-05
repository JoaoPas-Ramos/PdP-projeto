package br.edu.ifba.pedidos.observer;

import br.edu.ifba.pedidos.domain.Pedido;
import br.edu.ifba.pedidos.domain.StatusPedido;

public interface PedidoObserver {
    void aoAlterarStatus(Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus);
}
