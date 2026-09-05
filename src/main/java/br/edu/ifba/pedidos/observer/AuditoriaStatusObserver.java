package br.edu.ifba.pedidos.observer;

import br.edu.ifba.pedidos.domain.Pedido;
import br.edu.ifba.pedidos.domain.StatusPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AuditoriaStatusObserver implements PedidoObserver {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final List<String> eventos = new ArrayList<>();

    @Override
    public void aoAlterarStatus(Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus) {
        eventos.add(String.format(
                "%s | Pedido #%d | %s -> %s",
                LocalDateTime.now().format(FORMATO),
                pedido.getId(),
                statusAnterior,
                novoStatus));
    }

    public List<String> getEventos() {
        return Collections.unmodifiableList(new ArrayList<>(eventos));
    }
}
