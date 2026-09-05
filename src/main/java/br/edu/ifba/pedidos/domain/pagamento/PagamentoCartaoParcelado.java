package br.edu.ifba.pedidos.domain.pagamento;

public final class PagamentoCartaoParcelado implements FormaPagamento {
    @Override
    public double calcularAjuste(double subtotal) {
        return subtotal * 0.05;
    }

    @Override
    public String getDescricao() {
        return "Cartão parcelado (5% de acréscimo)";
    }
}
