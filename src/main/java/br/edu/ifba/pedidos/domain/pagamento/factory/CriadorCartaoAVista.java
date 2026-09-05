package br.edu.ifba.pedidos.domain.pagamento.factory;

import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;
import br.edu.ifba.pedidos.domain.pagamento.PagamentoCartaoAVista;

public final class CriadorCartaoAVista extends CriadorFormaPagamento {
    @Override
    protected FormaPagamento criarFormaPagamento() {
        return new PagamentoCartaoAVista();
    }
}
