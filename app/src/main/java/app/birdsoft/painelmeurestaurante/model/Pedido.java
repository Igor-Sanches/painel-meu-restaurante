package app.birdsoft.painelmeurestaurante.model;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    public Boolean isItem = true;
    public Boolean isMenuOptions = false;
    public Boolean isStatusOptions = false;
    private String observacao;
    private String uid;
    private Cupom cupom;
    private String DisplayName;
    private Double valorTotal;
    private boolean freteGratis;
    private String endereco;
    private String pagamento;
    private double frete, valorComFrete;
    private String distancia;
    private long dataPedido, dataRecebimento;
    private String msgCancelamento;
    private String uid_client;
    private String telefone;
    private String clienteNome;
    private List<Carrinho> itensPedido;
    private String statusPedido;
    private Double valorTroco;
    private boolean isTroco;
    private int prazo;
    private boolean cancelado;
    private String coordenadas;

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public boolean isFreteGratis() {
        return freteGratis;
    }

    public void setFreteGratis(boolean freteGratis) {
        this.freteGratis = freteGratis;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    private boolean alteracao;
    private String msgAlteracao;
    private boolean atrasado;

    public boolean isAtrasado() {
        return atrasado;
    }

    public double getFrete() {
        return frete;
    }

    public void setFrete(double frete) {
        this.frete = frete;
    }

    public double getValorComFrete() {
        return valorComFrete;
    }

    public void setValorComFrete(double valorComFrete) {
        this.valorComFrete = valorComFrete;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public void setAtrasado(boolean atrasado) {
        this.atrasado = atrasado;
    }

    public boolean isAlteracao() {
        return alteracao;
    }

    public String getMsgAlteracao() {
        return msgAlteracao;
    }

    public void setAlteracao(boolean alteracao) {
        this.alteracao = alteracao;
    }

    public void setMsgAlteracao(String msgAlteracao) {
        this.msgAlteracao = msgAlteracao;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public int getPrazo() {
        return prazo;
    }

    public void setPrazo(int prazo) {
        this.prazo = prazo;
    }

    public String getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(String statusPedido) {
        this.statusPedido = statusPedido;
    }

    public Double getValorTroco() {
        return valorTroco;
    }

    public void setValorTroco(Double valorTroco) {
        this.valorTroco = valorTroco;
    }

    public boolean isTroco() {
        return isTroco;
    }

    public void setTroco(boolean troco) {
        isTroco = troco;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getPagamento() {
        return pagamento;
    }

    public void setPagamento(String pagamento) {
        this.pagamento = pagamento;
    }

    public long getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(long dataPedido) {
        this.dataPedido = dataPedido;
    }

    public long getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(long dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public String getMsgCancelamento() {
        return msgCancelamento;
    }

    public void setMsgCancelamento(String msgCancelamento) {
        this.msgCancelamento = msgCancelamento;
    }

    public String getUid_client() {
        return uid_client;
    }

    public void setUid_client(String uid_client) {
        this.uid_client = uid_client;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public List<Carrinho> getItensPedido() {
        return itensPedido;
    }

    public void setItensPedido(List<Carrinho> itensPedido) {
        this.itensPedido = itensPedido;
    }
}