package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Cardapio implements Serializable {
    private int position;
    private String uid;
    private String name;
    private String receita;
    private String imageUrl;
    private int tipoLanche;
    private double valor;
    private boolean disponivel;
    private ArrayList<BlocoPublicar> cardapio;
    private long data;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setCardapio(ArrayList<BlocoPublicar> cardapio) {
        this.cardapio = cardapio;
    }

    public ArrayList<BlocoPublicar> getCardapio() {
        return cardapio;
    }

    public void setData(long data) {
        this.data = data;
    }

    public long getData() {
        return data;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceita() {
        return receita;
    }

    public void setReceita(String receita) {
        this.receita = receita;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTipoLanche() {
        return tipoLanche;
    }

    public void setTipoLanche(int tipoLanche) {
        this.tipoLanche = tipoLanche;
    }

}
