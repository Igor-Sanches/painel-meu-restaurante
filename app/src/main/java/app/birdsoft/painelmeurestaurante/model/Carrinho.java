package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Carrinho implements Serializable {
    private ArrayList<ItemCardapioLista> listas;
    private Double valorTotal;
    private int quantidade;
    public ArrayList<Double> listaValores;
    private String displayName, uid;
    private long data;

    public ArrayList<Double> getListaValores() {
        return listaValores;
    }

    public void setListaValores(ArrayList<Double> listaValores) {
        this.listaValores = listaValores;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public ArrayList<ItemCardapioLista> getListas() {
        return listas;
    }

    public void setListas(ArrayList<ItemCardapioLista> listas) {
        this.listas = listas;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}

