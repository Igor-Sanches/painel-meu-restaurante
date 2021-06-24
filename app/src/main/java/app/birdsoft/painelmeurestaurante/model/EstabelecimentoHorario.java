package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EstabelecimentoHorario  implements Serializable {
    private String diaSemane;
    private boolean aberto;
    private String horarioAbrir;
    private String horarioFechar;

    public EstabelecimentoHorario(){}

    public EstabelecimentoHorario(String diaSemane, boolean aberto, String horarioAbrir, String horarioFechar) {
        this.diaSemane = diaSemane;
        this.aberto = aberto;
        this.horarioAbrir = horarioAbrir;
        this.horarioFechar = horarioFechar;
    }

    public String getDiaSemane() {
        return diaSemane;
    }

    public void setDiaSemane(String diaSemane) {
        this.diaSemane = diaSemane;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public String getHorarioAbrir() {
        return horarioAbrir;
    }

    public void setHorarioAbrir(String horarioAbrir) {
        this.horarioAbrir = horarioAbrir;
    }

    public String getHorarioFechar() {
        return horarioFechar;
    }

    public void setHorarioFechar(String horarioFechar) {
        this.horarioFechar = horarioFechar;
    }

}
