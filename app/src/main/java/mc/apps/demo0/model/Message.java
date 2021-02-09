package mc.apps.demo0.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import mc.apps.demo0.libs.MyTools;

public class Message implements Serializable {
    private int id;

    private String message;

    @SerializedName("from_user")
    private String fromUser;
    @SerializedName("to_user")
    private String toUser;
    private byte seen;

    @SerializedName("date_creation")
    private String dateCreation;

    public String getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Message(int id, String message, String fromUser, String toUser, byte seen) {
        this.id = id;
        this.message = message;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.seen = seen;
        this.dateCreation = MyTools.getCurrentDate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte getSeen() {
        return seen;
    }

    public void setSeen(byte seen) {
        this.seen = seen;
    }


}
