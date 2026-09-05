package br.edu.ifba.pedidos.domain;

public enum StatusPedido {
    CRIADO,
    AGUARDANDO_PAGAMENTO,
    PAGO,
    EM_PREPARACAO,
    ENVIADO,
    ENTREGUE,
    CANCELADO;

    public boolean podeTransicionarPara(StatusPedido novoStatus) {
        switch (this) {
            case CRIADO:
                return novoStatus == AGUARDANDO_PAGAMENTO || novoStatus == CANCELADO;
            case AGUARDANDO_PAGAMENTO:
                return novoStatus == PAGO || novoStatus == CANCELADO;
            case PAGO:
                return novoStatus == EM_PREPARACAO || novoStatus == CANCELADO;
            case EM_PREPARACAO:
                return novoStatus == ENVIADO || novoStatus == CANCELADO;
            case ENVIADO:
                return novoStatus == ENTREGUE;
            case ENTREGUE:
            case CANCELADO:
            default:
                return false;
        }
    }
}
