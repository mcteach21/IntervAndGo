package mc.apps.demo0.model;

import java.io.Serializable;
import java.util.List;

public class Client implements Serializable {

	private int id;
	private String adresse;
	private String contact;
	private String cp;
	private String email;
	private String nom;
	private String telephone;
	private String ville;

	private List<Intervention> interventions;

	public Client() {
	}

	public Client(int id, String adresse, String contact, String cp, String email, String nom, String telephone, String ville) {
		this.id = id;
		this.adresse = adresse;
		this.contact = contact;
		this.cp = cp;
		this.email = email;
		this.nom = nom;
		this.telephone = telephone;
		this.ville = ville;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdresse() {
		return this.adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCp() {
		return this.cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getVille() {
		return this.ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public List<Intervention> getInterventions() {
		return this.interventions;
	}

	public void setInterventions(List<Intervention> interventions) {
		this.interventions = interventions;
	}

	public Intervention addIntervention(Intervention intervention) {
		getInterventions().add(intervention);
		//intervention.setClient(this);

		return intervention;
	}

	public Intervention removeIntervention(Intervention intervention) {
		getInterventions().remove(intervention);
		//intervention.setClient(null);

		return intervention;
	}

}