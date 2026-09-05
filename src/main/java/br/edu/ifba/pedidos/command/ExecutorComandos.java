package br.edu.ifba.pedidos.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExecutorComandos {
    private final List<String> historico = new ArrayList<>();

    public void executar(Comando comando) {
        comando.executar();
        historico.add(comando.getDescricao());
    }

    public List<String> getHistorico() {
        return Collections.unmodifiableList(new ArrayList<>(historico));
    }
}
