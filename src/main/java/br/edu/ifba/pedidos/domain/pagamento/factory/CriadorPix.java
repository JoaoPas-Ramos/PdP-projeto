package br.edu.ifba.pedidos.domain.pagamento.factory;

import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;
import br.edu.ifba.pedidos.domain.pagamento.PagamentoPix;

public final class CriadorPix extends CriadorFormaPagamento {
    @Override
    protected FormaPagamento criarFormaPagamento() {
        return new PagamentoPix();
    }
}
