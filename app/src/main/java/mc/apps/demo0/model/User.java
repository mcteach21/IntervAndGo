package mc.apps.demo0.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class User implements Serializable {
	private String code;
	private String email;
	private String firstname;
	private String lastname;
	private String password;

	@SerializedName("profil_id")
	private byte profilId;

	private List<Affectation> affectations;
	private List<Intervention> interventions;

	public User() {
	}

	public User(String code, String email, String firstname, String lastname, String password, byte profilId) {
		this.code = code;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
		this.profilId = profilId;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte getProfilId() {
		return this.profilId;
	}

	public void setProfilId(byte profilId) {
		this.profilId = profilId;
	}

	public List<Affectation> getAffectations() {
		return this.affectations;
	}

	public void setAffectations(List<Affectation> affectations) {
		this.affectations = affectations;
	}

	public Affectation addAffectation(Affectation affectation) {
		getAffectations().add(affectation);
		affectation.setUser(this);

		return affectation;
	}

	public Affectation removeAffectation(Affectation affectation) {
		getAffectations().remove(affectation);
		affectation.setUser(null);

		return affectation;
	}

	public List<Intervention> getInterventions() {
		return this.interventions;
	}

	public void setInterventions(List<Intervention> interventions) {
		this.interventions = interventions;
	}

	public Intervention addIntervention(Intervention intervention) {
		getInterventions().add(intervention);
		//intervention.setUser(this);

		return intervention;
	}

	public Intervention removeIntervention(Intervention intervention) {
		getInterventions().remove(intervention);
		//intervention.setUser(null);

		return intervention;
	}

	@Override
	public String toString() {
		return firstname + ' ' +  lastname;
	}
}