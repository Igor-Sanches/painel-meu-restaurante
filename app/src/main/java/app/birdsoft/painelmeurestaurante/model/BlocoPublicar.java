package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BlocoPublicar implements Serializable {
    private String uid, dispayTitulo;
    private ArrayList<Boolean> text;
    private ArrayList<String> contents;
    private ArrayList<String> textos;
    private ArrayList<Double> valores;
    private int selectMax;
    private boolean multiselect;
    private boolean obgdSelect;
    private boolean valorMaior;
    private ArrayList<Integer> maxItensAdicionais;
    private boolean itensAdicionais;

    public void setTextos(ArrayList<String> textos) {
        this.textos = textos;
    }

    public ArrayList<String> getTextos() {
        return textos;
    }

    public ArrayList<Boolean> getText() {
        return text;
    }

    public void setText(ArrayList<Boolean> text) {
        this.text = text;
    }

    public ArrayList<Integer> getMaxItensAdicionais() {
        return maxItensAdicionais;
    }

    public void setMaxItensAdicionais(ArrayList<Integer> maxItensAdicionais) {
        this.maxItensAdicionais = maxItensAdicionais;
    }

    public boolean isItensAdicionais() {
        return itensAdicionais;
    }

    public void setItensAdicionais(boolean itensAdicionais) {
        this.itensAdicionais = itensAdicionais;
    }

    public boolean isValorMaior() {
        return valorMaior;
    }

    public void setValorMaior(boolean valorMaior) {
        this.valorMaior = valorMaior;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDispayTitulo() {
        return dispayTitulo;
    }

    public void setDispayTitulo(String dispayTitulo) {
        this.dispayTitulo = dispayTitulo;
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }

    public ArrayList<Double> getValores() {
        return valores;
    }

    public void setValores(ArrayList<Double> valores) {
        this.valores = valores;
    }

    public int getSelectMax() {
        return selectMax;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public boolean isMultiselect() {
        return multiselect;
    }

    public void setMultiselect(boolean multiselect) {
        this.multiselect = multiselect;
    }

    public boolean isObgdSelect() {
        return obgdSelect;
    }

    public void setObgdSelect(boolean obgdSelect) {
        this.obgdSelect = obgdSelect;
    }

}
