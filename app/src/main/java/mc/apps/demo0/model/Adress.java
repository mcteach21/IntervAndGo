package mc.apps.demo0.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Adress implements Serializable {
    private int id;
    private int cp;
    private String nom;
    private String ville;
    private String voie;

    @SerializedName("client_id")
    private String clientId;

    //...
    private Client client;

    public Adress(int id, int cp, String nom, String ville, String voie, String clientId) {
        this.id = id;
        this.cp = cp;
        this.nom = nom;
        this.ville = ville;
        this.voie = voie;
        this.clientId = clientId;
    }

    public Adress(int id, int cp, String nom, String ville, String voie, Client client) {
        this.id = id;
        this.cp = cp;
        this.nom = nom;
        this.ville = ville;
        this.voie = voie;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getVoie() {
        return voie;
    }

    public void setVoie(String voie) {
        this.voie = voie;
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
}
