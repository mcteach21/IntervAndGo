package mc.apps.demo0.model;

import java.io.Serializable;
import java.util.List;

public class Client implements Serializable {

	private String code;
	private String contact;
	private String email;
	private String nom;
	private String telephone;

	private List<Intervention> interventions;
	private List<Adress> adresses;

	public Client(String code, String adresse, String contact, String cp, String email, String nom, String telephone, String ville) {
		this.code = code;
		this.contact = contact;
		this.email = email;
		this.nom = nom;
		this.telephone = telephone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
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

	@Override
	public String toString() {
		return nom.substring(0,1).toUpperCase()+nom.substring(1).toLowerCase();
	}
}