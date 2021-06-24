package app.birdsoft.painelmeurestaurante.model;

import java.util.List;

public class CardapioElements {
    private int vazioVisibility, listaVisibility, progressVisibility, LayoutWifiOffline;
    public List<Cardapio> cardapios;

    public int getLayoutWifiOffline() {
        return LayoutWifiOffline;
    }

    public void setLayoutWifiOffline(int layoutWifiOffline) {
        LayoutWifiOffline = layoutWifiOffline;
    }

    public List<Cardapio> getCardapios() {
        return cardapios;
    }

    public void setCardapios(List<Cardapio> cardapios) {
        this.cardapios = cardapios;
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
}
