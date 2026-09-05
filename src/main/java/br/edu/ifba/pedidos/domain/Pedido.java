package br.edu.ifba.pedidos.domain;

import br.edu.ifba.pedidos.domain.entrega.ModalidadeEntrega;
import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;
import br.edu.ifba.pedidos.observer.PedidoObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Pedido {
    private final int id;
    private final Cliente cliente;
    private final List<ItemPedido> itens;
    private final LocalDateTime dataCriacao;
    private StatusPedido status;
    private final double subtotal;
    private final double desconto;
    private final double acrescimoPagamento;
    private final double valorFrete;
    private final double valorTotal;
    private final FormaPagamento formaPagamento;
    private final ModalidadeEntrega modalidadeEntrega;
    private final List<PedidoObserver> observers = new ArrayList<>();

    Pedido(
            int id,
            Cliente cliente,
            List<ItemPedido> itens,
            FormaPagamento formaPagamento,
            ModalidadeEntrega modalidadeEntrega) {
        this.id = id;
        this.cliente = cliente;
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
        this.formaPagamento = formaPagamento;
        this.modalidadeEntrega = modalidadeEntrega;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedido.CRIADO;

        this.subtotal = calcularSubtotalDosItens();
        double ajustePagamento = formaPagamento.calcularAjuste(subtotal);
        this.desconto = ajustePagamento < 0 ? -ajustePagamento : 0;
        this.acrescimoPagamento = ajustePagamento > 0 ? ajustePagamento : 0;
        this.valorFrete = modalidadeEntrega.calcularFrete(subtotal);
        this.valorTotal = subtotal + ajustePagamento + valorFrete;
    }

    private double calcularSubtotalDosItens() {
        double soma = 0;
        for (ItemPedido item : itens) {
            soma += item.calcularSubtotal();
        }
        return soma;
    }

    public void adicionarObserver(PedidoObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void alterarStatus(StatusPedido novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status é obrigatório.");
        }
        if (!status.podeTransicionarPara(novoStatus)) {
            throw new IllegalStateException(
                    "Transição inválida: " + status + " -> " + novoStatus + ".");
        }

        StatusPedido statusAnterior = status;
        status = novoStatus;
        observers.forEach(observer -> observer.aoAlterarStatus(this, statusAnterior, novoStatus));
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getAcrescimoPagamento() {
        return acrescimoPagamento;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public ModalidadeEntrega getModalidadeEntrega() {
        return modalidadeEntrega;
    }
}
