package mc.apps.demo0.model;

import java.io.Serializable;

public class Affectation implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private Intervention intervention;
	private User user;

	public Affectation() {
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

}