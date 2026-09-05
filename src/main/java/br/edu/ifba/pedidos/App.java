package br.edu.ifba.pedidos;

import br.edu.ifba.pedidos.command.ExecutorComandos;
import br.edu.ifba.pedidos.facade.SistemaPedidosFacade;
import br.edu.ifba.pedidos.observer.AuditoriaStatusObserver;
import br.edu.ifba.pedidos.observer.NotificacaoClienteObserver;
import br.edu.ifba.pedidos.observer.PedidoObserver;
import br.edu.ifba.pedidos.ui.ConsoleApp;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        AuditoriaStatusObserver auditoria = new AuditoriaStatusObserver();
        List<PedidoObserver> observers = Arrays.asList(
                auditoria,
                new NotificacaoClienteObserver(System.out));

        SistemaPedidosFacade facade = new SistemaPedidosFacade(
                new ExecutorComandos(),
                auditoria,
                observers);

        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleApp(facade, scanner, System.out).executar();
        }
    }
}
