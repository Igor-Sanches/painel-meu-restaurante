package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemCardapioLista implements Serializable {
    public boolean expanded = true, selected_obg = false, modificate = false;
    private String displayName = "";
    private ArrayList<String> contents = new ArrayList<>();
    private ArrayList<Double> valores = new ArrayList<>();
    private ArrayList<Integer> quantidate = new ArrayList<>();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public ArrayList<Integer> getQuantidate() {
        return quantidate;
    }

    public void setQuantidate(ArrayList<Integer> quantidate) {
        this.quantidate = quantidate;
    }
}
