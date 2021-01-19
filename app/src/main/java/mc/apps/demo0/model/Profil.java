package mc.apps.demo0.model;

import java.io.Serializable;


public class Profil implements Serializable {
	private byte id;
	private String profil;

	public Profil(byte id, String profil) {
		this.id = id;
		this.profil = profil;
	}

	public byte getId() {
		return this.id;
	}

	public void setId(byte id) {
		this.id = id;
	}

	public String getProfil() {
		return this.profil;
	}

	public void setProfil(String profil) {
		this.profil = profil;
	}

}