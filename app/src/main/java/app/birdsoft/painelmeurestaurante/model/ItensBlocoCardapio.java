package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;

public class ItensBlocoCardapio implements Serializable {
    private String content;
    private Double valor;
    private boolean text;
    private String texto;
    private int maxItensQuantidate;

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setMaxItensQuantidate(int maxItensQuantidate) {
        this.maxItensQuantidate = maxItensQuantidate;
    }

    public int getMaxItensQuantidate() {
        return maxItensQuantidate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
