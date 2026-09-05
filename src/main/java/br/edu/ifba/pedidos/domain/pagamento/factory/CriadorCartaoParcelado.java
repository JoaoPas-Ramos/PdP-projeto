package br.edu.ifba.pedidos.domain.pagamento.factory;

import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;
import br.edu.ifba.pedidos.domain.pagamento.PagamentoCartaoParcelado;

public final class CriadorCartaoParcelado extends CriadorFormaPagamento {
    @Override
    protected FormaPagamento criarFormaPagamento() {
        return new PagamentoCartaoParcelado();
    }
}
