package mc.apps.demo0.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {
    private int id;

    private String message;

    @SerializedName("from_user")
    private String fromUser;
    @SerializedName("to_user")
    private String toUser;
    private byte seen;

    public Message(int id, String fromUser, String toUser, byte seen) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.seen = seen;
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
