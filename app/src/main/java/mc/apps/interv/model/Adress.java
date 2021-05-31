package mc.apps.interv.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Adress implements Serializable {
    private int id;
    private String cp;
    private String nom;
    private String ville;
    private String voie;

    @SerializedName("client_id")
    private String clientId;

    //...
    private Client client;

    public Adress(int id, String nom, String voie, String cp, String ville, String clientId) {
        this.id = id;
        this.nom = nom;
        this.voie = voie;
        this.cp = cp;
        this.ville = ville;
        this.clientId = clientId;
    }


/*    public Adress(int id, String cp, String nom, String ville, String voie, String client) {
        this.id = id;
        this.cp = cp;
        this.nom = nom;
        this.ville = ville;
        this.voie = voie;
        this.client = client;
    */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
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

    @Override
    public String toString() {
        return voie + '\n' + cp + '\n' + ville;
    }
}
