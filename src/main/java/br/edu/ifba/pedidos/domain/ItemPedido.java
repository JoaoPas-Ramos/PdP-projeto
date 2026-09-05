package br.edu.ifba.pedidos.domain;

public final class ItemPedido {
    private final String produto;
    private final int quantidade;
    private final double precoUnitario;

    public ItemPedido(String produto, int quantidade, double precoUnitario) {
        if (produto == null || produto.isBlank()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (precoUnitario <= 0) {
            throw new IllegalArgumentException("O preço unitário deve ser maior que zero.");
        }
        this.produto = produto.trim();
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public double calcularSubtotal() {
        return precoUnitario * quantidade;
    }

    public String getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }
}
