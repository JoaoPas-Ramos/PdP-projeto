package br.edu.ifba.pedidos.domain.entrega;

public final class EntregaTransportadora implements ModalidadeEntrega {
    @Override
    public double calcularFrete(double subtotal) {
        return subtotal * 0.15;
    }

    @Override
    public String getDescricao() {
        return "Transportadora (15% do subtotal)";
    }
}
