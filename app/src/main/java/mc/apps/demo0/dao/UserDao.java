package mc.apps.demo0.dao;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;

public class UserDao extends Dao<User> {
    public UserDao(){
        super("users");
    }

    public void login(String login, String password, OnSuccess onSuccess){
        find("login=" + login + "&password=" + password, onSuccess);
    }
    public void add(User u, OnSuccess onSuccess){

        try {
            String addClause="action=add&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")
                    +"&firstname="+ URLEncoder.encode(u.getFirstname(), "utf-8")
                    +"&lastname="+ URLEncoder.encode(u.getLastname(), "utf-8")
                    +"&email="+ URLEncoder.encode(u.getEmail(), "utf-8")
                    +"&password="+ URLEncoder.encode(u.getPassword(), "utf-8")
                    +"&profil_id="+ u.getProfilId();

            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
