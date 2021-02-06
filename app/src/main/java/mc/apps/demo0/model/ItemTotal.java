package mc.apps.demo0.model;

import java.io.Serializable;

public class ItemTotal implements Serializable {

    private String Code;
    private int Total;

    public ItemTotal(String code, int total) {
        Code = code;
        Total = total;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    @Override
    public String toString() {
        return Code + "=>" + Total;
    }
}
