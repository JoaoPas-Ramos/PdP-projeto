package br.edu.ifba.pedidos.domain.entrega;

public final class EntregaPac implements ModalidadeEntrega {
    @Override
    public double calcularFrete(double subtotal) {
        return subtotal * 0.05;
    }

    @Override
    public String getDescricao() {
        return "PAC (5% do subtotal)";
    }
}
