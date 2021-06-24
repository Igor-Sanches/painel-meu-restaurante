package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;

public class Cupom implements Serializable {
    private long dataValidade;
    private int numerouso;
    private String codigo;
    private boolean ativo, vencimento, alluser, mininum, expirado;
    private int descontoType;
    private double valorDesconto;
    private double valorMinimo;

    public void setExpirado(boolean expirado) {
        this.expirado = expirado;
    }

    public boolean isExpirado() {
        return expirado;
    }

    public int getNumerouso() {
        return numerouso;
    }

    public void setNumerouso(int numerouso) {
        this.numerouso = numerouso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public long getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(long dataValidade) {
        this.dataValidade = dataValidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isVencimento() {
        return vencimento;
    }

    public void setVencimento(boolean vencimento) {
        this.vencimento = vencimento;
    }

    public boolean isAlluser() {
        return alluser;
    }

    public void setAlluser(boolean alluser) {
        this.alluser = alluser;
    }

    public boolean isMininum() {
        return mininum;
    }

    public void setMininum(boolean mininum) {
        this.mininum = mininum;
    }

    public int getDescontoType() {
        return descontoType;
    }

    public void setDescontoType(int descontoType) {
        this.descontoType = descontoType;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(double valorMinimo) {
        this.valorMinimo = valorMinimo;
    }
}
