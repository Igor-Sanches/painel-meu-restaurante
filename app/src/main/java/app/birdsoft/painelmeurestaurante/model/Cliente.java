package app.birdsoft.painelmeurestaurante.model;

public class Cliente {

    private String nome, email, data, uuid;

    public String getUuid() {
        return uuid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
