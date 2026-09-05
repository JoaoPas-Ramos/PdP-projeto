package br.edu.ifba.pedidos.domain.pagamento.factory;

import br.edu.ifba.pedidos.domain.pagamento.FormaPagamento;
import br.edu.ifba.pedidos.domain.pagamento.PagamentoBoleto;

public final class CriadorBoleto extends CriadorFormaPagamento {
    @Override
    protected FormaPagamento criarFormaPagamento() {
        return new PagamentoBoleto();
    }
}
