package br.edu.ifba.pedidos.domain.pagamento;

public final class PagamentoBoleto implements FormaPagamento {
    @Override
    public double calcularAjuste(double subtotal) {
        return -subtotal * 0.05;
    }

    @Override
    public String getDescricao() {
        return "Boleto (5% de desconto)";
    }
}
