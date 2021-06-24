package app.birdsoft.painelmeurestaurante.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class EstabelecimentoElements {
    private boolean aberto;
    private int prazo;
    private List<EstabelecimentoHorario> horarios;
    private String whatsapp;
    private String ligacao;
    private GeoPoint geoPoint;
    private String endereco;
    private int layoutWifiOffline, layoutInicial, layoutVazio, layoutProgress;
    private int km;
    private int taxa, prazoMinutoFixo;
    private double valorPerKm;
    private double valorFixo;
    private String email;

    public void setPrazoMinutoFixo(int prazoMinutoFixo) {
        this.prazoMinutoFixo = prazoMinutoFixo;
    }

    public int getPrazoMinutoFixo() {
        return prazoMinutoFixo;
    }

    public void setPrazo(int prazo) {
        this.prazo = prazo;
    }

    public int getPrazo() {
        return prazo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public int getLayoutProgress() {
        return layoutProgress;
    }

    public void setLayoutProgress(int layoutProgress) {
        this.layoutProgress = layoutProgress;
    }

    public int getLayoutVazio() {
        return layoutVazio;
    }

    public void setLayoutVazio(int layoutVazio) {
        this.layoutVazio = layoutVazio;
    }

    public void setConfig(boolean config) {
    }

    public int getLayoutWifiOffline() {
        return layoutWifiOffline;
    }

    public void setLayoutWifiOffline(int layoutWifiOffline) {
        this.layoutWifiOffline = layoutWifiOffline;
    }

    public int getLayoutInicial() {
        return layoutInicial;
    }

    public void setLayoutInicial(int layoutInicial) {
        this.layoutInicial = layoutInicial;
    }

    public int getTaxa() {
        return taxa;
    }

    public void setTaxa(int taxa) {
        this.taxa = taxa;
    }

    public double getValorPerKm() {
        return valorPerKm;
    }

    public void setValorPerKm(double valorPerKm) {
        this.valorPerKm = valorPerKm;
    }

    public double getValorFixo() {
        return valorFixo;
    }

    public void setValorFixo(double valorFixo) {
        this.valorFixo = valorFixo;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public int getKm() {
        return km;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public List<EstabelecimentoHorario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<EstabelecimentoHorario> horarios) {
        this.horarios = horarios;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getLigacao() {
        return ligacao;
    }

    public void setLigacao(String ligacao) {
        this.ligacao = ligacao;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

}
