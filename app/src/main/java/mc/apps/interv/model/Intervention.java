package mc.apps.interv.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Intervention implements Serializable {
	private String code;
	@SerializedName("client_id")
	private String clientId;

	@SerializedName("date_debut_prevue")
	private String dateDebutPrevue;
	@SerializedName("date_debut_reelle")
	private String dateDebutReelle;
	@SerializedName("date_fin_prevue")
	private String dateFinPrevue;
	@SerializedName("date_fin_reelle")
	private String dateFinReelle;
	private String description;
	@SerializedName("materiel_necessaire")
	private String materielNecessaire;
	@SerializedName("service_equip_cible")
	private String serviceEquipCible;


	@SerializedName("statut_id")
	private int statutId;
	@SerializedName("superviseur_id")
	private String superviseurId;


	@SerializedName("date_creation")
	private String dateCreation;

	private String consignes; 		//intervention
	private String observations;	//rapport

	private List<User> technicians;
	private List<Affectation> affectations;
	private Statut statut;
	private User user;

	public Intervention(String code, String clientId, String description, String dateDebutPrevue, String dateFinPrevue, String consignes, String materielNecessaire, String serviceEquipCible, String superviseurId) {
		this(code,  clientId,  description,  dateDebutPrevue,  dateFinPrevue, consignes,  materielNecessaire,  serviceEquipCible, superviseurId, "");
	}
	public Intervention(String code, String clientId, String description, String dateDebutPrevue, String dateFinPrevue, String consignes, String materielNecessaire, String serviceEquipCible, String superviseurId, String observations) {
		this.code = code;
		this.clientId = clientId;
		this.superviseurId = superviseurId;

		this.description = description;
		this.dateDebutPrevue = dateDebutPrevue;
		this.dateFinPrevue = dateFinPrevue;

		this.materielNecessaire = materielNecessaire;
		this.serviceEquipCible = serviceEquipCible;
		this.consignes = consignes;
		this.observations = observations;
	}
    public Intervention(String code, String clientId, String description, String dateDebutPrevue, String dateFinPrevue,
						String consignes, String materielNecessaire, String serviceEquipCible,
						String superviseurId, List<User> technicians) {

		this(code, clientId, description, dateDebutPrevue, dateFinPrevue,
				consignes, materielNecessaire, serviceEquipCible,
				superviseurId);

		this.observations="";

		this.technicians = technicians;
		this.affectations = new ArrayList<>();
		for (User tech : this.technicians)
			affectations.add(new Affectation(0, this.code, tech.getCode()));
	}

/*    public Intervention(String code, String clientId, String consignes, String dateDebutPrevue, String dateDebutReelle, String dateFinPrevue, String dateFinReelle, String description, String materielNecessaire, String serviceEquipCible, int statutId, String superviseurId) {
		this.code = code;
		this.clientId = clientId;
		this.consignes = consignes;
		this.dateDebutPrevue = dateDebutPrevue;
		this.dateDebutReelle = dateDebutReelle;
		this.dateFinPrevue = dateFinPrevue;
		this.dateFinReelle = dateFinReelle;
		this.description = description;
		this.materielNecessaire = materielNecessaire;
		this.serviceEquipCible = serviceEquipCible;
		this.statutId = statutId;
		this.superviseurId = superviseurId;
	}*/

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

	public String getDateDebutPrevue() {
		return dateDebutPrevue;
	}

	public void setDateDebutPrevue(String dateDebutPrevue) {
		this.dateDebutPrevue = dateDebutPrevue;
	}

	public String getDateDebutReelle() {
		return dateDebutReelle;
	}

	public void setDateDebutReelle(String dateDebutReelle) {
		this.dateDebutReelle = dateDebutReelle;
	}

	public String getDateFinPrevue() {
		return dateFinPrevue;
	}

	public void setDateFinPrevue(String dateFinPrevue) {
		this.dateFinPrevue = dateFinPrevue;
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

	public int getStatutId() {
		return statutId;
	}

	public void setStatutId(int statutId) {
		this.statutId = statutId;
	}

	public String getSuperviseurId() {
		return superviseurId;
	}

	public void setSuperviseurId(String superviseurId) {
		this.superviseurId = superviseurId;
	}

	public List<Affectation> getAffectations() {
		return affectations;
	}

	public void setAffectations(List<Affectation> affectations) {
		this.affectations = affectations;
	}

	public Statut getStatut() {
		return statut;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public String getConsignes() {
		return consignes;
	}

	public void setConsignes(String consignes) {
		this.consignes = consignes;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	@Override
	public String toString() {
		return "Intervention [" + code +"] " + description;
	}



}