package br.edu.ifba.pedidos.domain.pagamento;

public interface FormaPagamento {
    double calcularAjuste(double subtotal);

    String getDescricao();
}
