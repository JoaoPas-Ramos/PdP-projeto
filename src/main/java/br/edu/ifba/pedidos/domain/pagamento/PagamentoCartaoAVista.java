package br.edu.ifba.pedidos.domain.pagamento;

public final class PagamentoCartaoAVista implements FormaPagamento {
    @Override
    public double calcularAjuste(double subtotal) {
        return 0;
    }

    @Override
    public String getDescricao() {
        return "Cartão à vista (sem desconto)";
    }
}
