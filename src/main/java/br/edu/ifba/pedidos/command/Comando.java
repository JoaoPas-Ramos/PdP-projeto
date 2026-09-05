package br.edu.ifba.pedidos.command;

public interface Comando {
    void executar();

    String getDescricao();
}
