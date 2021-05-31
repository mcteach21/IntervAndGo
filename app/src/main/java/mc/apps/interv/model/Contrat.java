package mc.apps.interv.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Contrat implements Serializable {
    private String code;
    private String nom;
    @SerializedName("client_id")
    private String clientId;
    private Client client;

    public Contrat(String code, String nom, String clientId) {
        this.code = code;
        this.nom = nom;
        this.clientId = clientId;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return nom;
    }
}
