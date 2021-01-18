package mc.apps.demo0.model;

import android.widget.EditText;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Intervention implements Serializable {
	private int id;
	private String commentaire;

	@SerializedName("date_debut_prevue")
	private String dateDebutPrevue;

	@SerializedName("date_fin_prevue")
	private String dateFinPrevue;

	@SerializedName("date_debut_reelle")
	private String dateDebutReelle;
	@SerializedName("date_fin_reelle")
	private String dateFinReelle;

	private String description;
	@SerializedName("materiel_necessaire")
	private String materielNecessaire;
	@SerializedName("service_equip_cible")
	private String serviceEquipCible;

	@SerializedName("client_id")
	private int clientId;
	@SerializedName("superviseur_id")
	private int superviseurId;
	@SerializedName("statut_id")
	private int statutId;

	public Intervention(int id, String commentaire, String dateDebutPrevue, String dateFinPrevue, String dateDebutReelle, String dateFinReelle, String description, String materielNecessaire, String serviceEquipCible, int clientId, int superviseurId, int statutId) {
		this.id = id;
		this.commentaire = commentaire;
		this.dateDebutPrevue = dateDebutPrevue;
		this.dateFinPrevue = dateFinPrevue;
		this.dateDebutReelle = dateDebutReelle;
		this.dateFinReelle = dateFinReelle;
		this.description = description;
		this.materielNecessaire = materielNecessaire;
		this.serviceEquipCible = serviceEquipCible;
		this.clientId = clientId;
		this.superviseurId = superviseurId;
		this.statutId = statutId;
	}

    public Intervention(int id, int codeClient, String description, String dateDebutPrevue, String dateFinPrevue, String serviceEquipCible, String commentaire) {
		this.id = id;
		this.clientId = codeClient;
		this.description = description;
		this.dateDebutPrevue = dateDebutPrevue;
		this.dateFinPrevue = dateFinPrevue;
		this.serviceEquipCible = serviceEquipCible;
		this.commentaire = commentaire;
	}

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getDateDebutPrevue() {
		return dateDebutPrevue;
	}

	public void setDateDebutPrevue(String dateDebutPrevue) {
		this.dateDebutPrevue = dateDebutPrevue;
	}

	public String getDateFinPrevue() {
		return dateFinPrevue;
	}

	public void setDateFinPrevue(String dateFinPrevue) {
		this.dateFinPrevue = dateFinPrevue;
	}

	public String getDateDebutReelle() {
		return dateDebutReelle;
	}

	public void setDateDebutReelle(String dateDebutReelle) {
		this.dateDebutReelle = dateDebutReelle;
	}

	public String getDateFinReelle() {
		return dateFinReelle;
	}

	public void setDateFinReelle(String dateFinReelle) {
		this.dateFinReelle = dateFinReelle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMaterielNecessaire() {
		return materielNecessaire;
	}

	public void setMaterielNecessaire(String materielNecessaire) {
		this.materielNecessaire = materielNecessaire;
	}

	public String getServiceEquipCible() {
		return serviceEquipCible;
	}

	public void setServiceEquipCible(String serviceEquipCible) {
		this.serviceEquipCible = serviceEquipCible;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getSuperviseurId() {
		return superviseurId;
	}

	public void setSuperviseurId(int superviseurId) {
		this.superviseurId = superviseurId;
	}

	public int getStatutId() {
		return statutId;
	}

	public void setStatutId(int statutId) {
		this.statutId = statutId;
	}

	@Override
	public String toString() {
		return "Intervention [" + id +"] " + description;
	}
}