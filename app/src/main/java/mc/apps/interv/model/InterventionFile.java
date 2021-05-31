package mc.apps.interv.model;

import com.google.gson.annotations.SerializedName;

public class InterventionFile {
    private int id;

    @SerializedName("intervention_id")
    private String interventionId;
    private String filename;
    private int photo; // 1 - 0


    public InterventionFile(int id, String interventionId, String filename, int photo) {
        this.id = id;
        this.interventionId = interventionId;
        this.filename = filename;
        this.photo = photo;
    }
    public InterventionFile(int id, String interventionId, String filename) {
        this.id = id;
        this.interventionId = interventionId;
        this.filename = filename;
    }

    public int getId() {
        return id;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
