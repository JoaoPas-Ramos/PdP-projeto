package br.edu.ifba.pedidos.facade;

import br.edu.ifba.pedidos.command.AlterarStatusPedidoCommand;
import br.edu.ifba.pedidos.command.ExecutorComandos;
import br.edu.ifba.pedidos.domain.Pedido;
import br.edu.ifba.pedidos.domain.PedidoBuilder;
import br.edu.ifba.pedidos.domain.StatusPedido;
import br.edu.ifba.pedidos.observer.AuditoriaStatusObserver;
import br.edu.ifba.pedidos.observer.PedidoObserver;

import java.util.ArrayList;
import java.util.List;

public final class SistemaPedidosFacade {
    private final List<Pedido> pedidos = new ArrayList<>();
    private final List<PedidoObserver> observers;
    private final ExecutorComandos executorComandos;
    private final AuditoriaStatusObserver auditoria;
    private int proximoId = 1;

    public SistemaPedidosFacade(
            ExecutorComandos executorComandos,
            AuditoriaStatusObserver auditoria,
            List<PedidoObserver> observers) {
        this.executorComandos = executorComandos;
        this.auditoria = auditoria;
        this.observers = new ArrayList<>(observers);
    }

    public Pedido criarPedido(PedidoBuilder builder) {
        Pedido pedido = builder.construir(proximoId++);
        for (PedidoObserver observer : observers) {
            pedido.adicionarObserver(observer);
        }
        pedidos.add(pedido);
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public Pedido consultarPedido(int id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId() == id) {
                return pedido;
            }
        }
        throw new IllegalArgumentException("Pedido #" + id + " não encontrado.");
    }

    public void alterarStatus(int id, StatusPedido novoStatus) {
        Pedido pedido = consultarPedido(id);
        executorComandos.executar(new AlterarStatusPedidoCommand(pedido, novoStatus));
    }

    public List<String> consultarAuditoriaDeStatus() {
        return auditoria.getEventos();
    }

    public List<String> consultarHistoricoDeComandos() {
        return executorComandos.getHistorico();
    }
}
