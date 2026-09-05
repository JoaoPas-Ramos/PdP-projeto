package br.edu.ifba.pedidos.domain;

import br.edu.ifba.pedidos.domain.entrega.ModalidadeEntrega;
import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;

import java.util.ArrayList;
import java.util.List;

public final class PedidoBuilder {
    private Cliente cliente;
    private final List<ItemPedido> itens = new ArrayList<>();
    private FormaPagamento formaPagamento;
    private ModalidadeEntrega modalidadeEntrega;

    public PedidoBuilder cliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public PedidoBuilder adicionarItem(ItemPedido item) {
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        itens.add(item);
        return this;
    }

    public PedidoBuilder formaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
        return this;
    }

    public PedidoBuilder modalidadeEntrega(ModalidadeEntrega modalidadeEntrega) {
        this.modalidadeEntrega = modalidadeEntrega;
        return this;
    }

    public Pedido construir(int id) {
        if (cliente == null) {
            throw new IllegalStateException("O pedido precisa de um cliente.");
        }
        if (itens.isEmpty()) {
            throw new IllegalStateException("O pedido precisa de pelo menos um item.");
        }
        if (formaPagamento == null) {
            throw new IllegalStateException("Selecione uma forma de pagamento.");
        }
        if (modalidadeEntrega == null) {
            throw new IllegalStateException("Selecione uma modalidade de entrega.");
        }
        return new Pedido(id, cliente, itens, formaPagamento, modalidadeEntrega);
    }
}
