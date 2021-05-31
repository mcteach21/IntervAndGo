package mc.apps.interv.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Affectation implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	@SerializedName("intervention_id")
	private String interventionId;

	@SerializedName("technicien_id")
	private String technicienId;

	private Intervention intervention;
	private User user;

	public Affectation(int id, String interventionId, String technicienId) {
		this.id = id;
		this.interventionId = interventionId;
		this.technicienId = technicienId;
	}

	public Affectation(int id, Intervention intervention, User user) {
		this.id = id;
		this.intervention = intervention;
		this.user = user;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInterventionId() {
		return interventionId;
	}

	public void setInterventionId(String interventionId) {
		this.interventionId = interventionId;
	}

	public String getTechnicienId() {
		return technicienId;
	}

	public void setTechnicienId(String technicienId) {
		this.technicienId = technicienId;
	}

	public Intervention getIntervention() {
		return this.intervention;
	}

	public void setIntervention(Intervention intervention) {
		this.intervention = intervention;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return  "[" + interventionId + "-" + technicienId + "]";
	}
}