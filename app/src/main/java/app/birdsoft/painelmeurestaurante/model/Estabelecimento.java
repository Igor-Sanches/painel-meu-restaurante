package app.birdsoft.painelmeurestaurante.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Estabelecimento implements Serializable {
    private Integer prazo;
    private String endereco;
    private GeoPoint local;
    private String prazoDefault;
    private ArrayList<EstabelecimentoHorario> horarios;
    private String telefone;
    private String email;
    private String whatsapp;
    private boolean aberto;
    private boolean config;
    private Integer taxa;
    private Integer prazoMinutoFixo;
    private Integer km;
    private Double valorPerKm;
    private Double valorFixo;

    public Integer getPrazo() {
        return prazo;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public GeoPoint getLocal() {
        return local;
    }

    public void setLocal(GeoPoint local) {
        this.local = local;
    }

    public String getPrazoDefault() {
        return prazoDefault;
    }

    public void setPrazoDefault(String prazoDefault) {
        this.prazoDefault = prazoDefault;
    }

    public ArrayList<EstabelecimentoHorario> getHorarios() {
        return horarios;
    }

    public void setHorarios(ArrayList<EstabelecimentoHorario> horarios) {
        this.horarios = horarios;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public boolean isConfig() {
        return config;
    }

    public void setConfig(boolean config) {
        this.config = config;
    }

    public Integer getTaxa() {
        return taxa;
    }

    public void setTaxa(Integer taxa) {
        this.taxa = taxa;
    }

    public Integer getPrazoMinutoFixo() {
        return prazoMinutoFixo;
    }

    public void setPrazoMinutoFixo(Integer prazoMinutoFixo) {
        this.prazoMinutoFixo = prazoMinutoFixo;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Double getValorPerKm() {
        return valorPerKm;
    }

    public void setValorPerKm(Double valorPerKm) {
        this.valorPerKm = valorPerKm;
    }

    public Double getValorFixo() {
        return valorFixo;
    }

    public void setValorFixo(Double valorFixo) {
        this.valorFixo = valorFixo;
    }
}
