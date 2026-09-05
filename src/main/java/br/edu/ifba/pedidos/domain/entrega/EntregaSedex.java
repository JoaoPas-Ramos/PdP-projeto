package br.edu.ifba.pedidos.domain.entrega;

public final class EntregaSedex implements ModalidadeEntrega {
    @Override
    public double calcularFrete(double subtotal) {
        return subtotal * 0.10;
    }

    @Override
    public String getDescricao() {
        return "Sedex (10% do subtotal)";
    }
}
