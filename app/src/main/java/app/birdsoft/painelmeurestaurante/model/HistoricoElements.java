package app.birdsoft.painelmeurestaurante.model;

import java.util.List;

public class HistoricoElements {
    private int vazioVisibility, layoutWifiOffline, listaVisibility, progressVisibility;
    private List<Pedido> pedidos;

    public void setLayoutWifiOffline(int layoutWifiOffline) {
        this.layoutWifiOffline = layoutWifiOffline;
    }

    public int getLayoutWifiOffline() {
        return layoutWifiOffline;
    }

    public int getVazioVisibility() {
        return vazioVisibility;
    }

    public void setVazioVisibility(int vazioVisibility) {
        this.vazioVisibility = vazioVisibility;
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

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
