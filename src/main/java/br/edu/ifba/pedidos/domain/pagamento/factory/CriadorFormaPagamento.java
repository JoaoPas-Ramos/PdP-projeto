package br.edu.ifba.pedidos.domain.pagamento.factory;

import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;

public abstract class CriadorFormaPagamento {
    public final FormaPagamento criar() {
        FormaPagamento formaPagamento = criarFormaPagamento();
        if (formaPagamento == null) {
            throw new IllegalStateException("O Factory Method não pode retornar nulo.");
        }
        return formaPagamento;
    }

    protected abstract FormaPagamento criarFormaPagamento();
}
