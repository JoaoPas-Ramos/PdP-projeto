package br.edu.ifba.pedidos.domain.pagamento;

public final class PagamentoPix implements FormaPagamento {
    @Override
    public double calcularAjuste(double subtotal) {
        return -subtotal * 0.10;
    }

    @Override
    public String getDescricao() {
        return "Pix (10% de desconto)";
    }
}
