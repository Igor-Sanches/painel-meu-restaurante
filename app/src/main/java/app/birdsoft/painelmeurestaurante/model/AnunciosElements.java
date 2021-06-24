package app.birdsoft.painelmeurestaurante.model;

import java.util.List;

public class AnunciosElements {
    private int vazioVisibility, layoutWifiOffline, listaVisibility, progressVisibility;
    private List<Anuncio> anuncios;

    public int getLayoutWifiOffline() {
        return layoutWifiOffline;
    }

    public void setLayoutWifiOffline(int layoutWifiOffline) {
        this.layoutWifiOffline = layoutWifiOffline;

    }

    public List<Anuncio> getAnuncios() {
        return anuncios;
    }

    public void setAnuncios(List<Anuncio> anuncios) {
        this.anuncios = anuncios;
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
