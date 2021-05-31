package mc.apps.interv.dao;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.interv.model.User;

public class UserDao extends Dao<User> {
    public UserDao(){
        super("users");
    }

    public void login(String login, String password, OnSuccess onSuccess){
        find("login=" + login + "&password=" + password, onSuccess);
    }

    public void findTechsByInterv(String interv_code, OnSuccess onSuccess){
        super.query("techs_interv", interv_code, onSuccess);
    }
    public void findByCode(String code, OnSuccess onSuccess){
        try {
            find("code='" + URLEncoder.encode(code, "utf-8")+"'", onSuccess);
        } catch (UnsupportedEncodingException e) {}
    }
    public void findByLogin(String login, OnSuccess onSuccess){
        try {
            find("email='" + URLEncoder.encode(login, "utf-8")+"'", onSuccess);
        } catch (UnsupportedEncodingException e) {}
    }
    public void findBySupervisor(String supervisor_id, OnSuccess onSuccess){
        try {
            find("supervisor_id='" + URLEncoder.encode(supervisor_id, "utf-8")+"'", onSuccess);
        } catch (UnsupportedEncodingException e) {}
    }
    private void action(User u, String action, OnSuccess onSuccess){

        try {
            String addClause="action="+action+"&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")
                    +"&firstname="+ URLEncoder.encode(u.getFirstname(), "utf-8")
                    +"&lastname="+ URLEncoder.encode(u.getLastname(), "utf-8")
                    +"&email="+ URLEncoder.encode(u.getEmail(), "utf-8")
                    +"&password="+ URLEncoder.encode(u.getPassword(), "utf-8")
                    +"&profil_id="+ u.getProfilId()
                    +"&activated="+ u.getActivated()
                    +"&supervisor_id="+ u.getSupervisorId();

            if(action.equals("add"))
                super.add(addClause, onSuccess);
            if(action.equals("update"))
                super.update(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void add(User u, OnSuccess onSuccess){
        this.action(u,"add", onSuccess);
    }
    public void update(User u, OnSuccess onSuccess){
        this.action(u,"update", onSuccess);
    }
}
