package mc.apps.interv.model;

import java.io.Serializable;
import java.util.List;


public class Statut implements Serializable {
	private byte id;
	private String statut;

	private List<Intervention> interventions;

	public Statut(byte id, String statut) {
		this.id = id;
		this.statut = statut;
	}

	public byte getId() {
		return this.id;
	}

	public void setId(byte id) {
		this.id = id;
	}

	public String getStatut() {
		return this.statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public List<Intervention> getInterventions() {
		return this.interventions;
	}

	public void setInterventions(List<Intervention> interventions) {
		this.interventions = interventions;
	}

	public Intervention addIntervention(Intervention intervention) {
		getInterventions().add(intervention);
		//intervention.setStatut(this);

		return intervention;
	}

	public Intervention removeIntervention(Intervention intervention) {
		getInterventions().remove(intervention);
		//intervention.setStatut(null);

		return intervention;
	}

}