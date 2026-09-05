package br.edu.ifba.pedidos.domain.entrega;

public interface ModalidadeEntrega {
    double calcularFrete(double subtotal);

    String getDescricao();
}
