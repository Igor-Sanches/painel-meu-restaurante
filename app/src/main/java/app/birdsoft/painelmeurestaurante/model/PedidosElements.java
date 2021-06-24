package app.birdsoft.painelmeurestaurante.model;


import java.util.List;

public class PedidosElements {
    private int vazioVisibility, layoutWifiOffline, listaVisibility, progressVisibility;
    private List<Pedido> pedidos;

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public int getVazioVisibility() {
        return vazioVisibility;
    }

    public void setVazioVisibility(int vazioVisibility) {
        this.vazioVisibility = vazioVisibility;
    }

    public int getLayoutWifiOffline() {
        return layoutWifiOffline;
    }

    public void setLayoutWifiOffline(int layoutWifiOffline) {
        this.layoutWifiOffline = layoutWifiOffline;
    }

    public int getListaVisibility() {
        return listaVisibility;
    }

    public void setListaVisibility(int listaVisibility) {
        this.listaVisibility = listaVisibility;
    }

    public int getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(int progressVisibility) {
        this.progressVisibility = progressVisibility;
    }
}
