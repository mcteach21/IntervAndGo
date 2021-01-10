package mc.apps.demo0.dao;


import mc.apps.demo0.model.User;

public class UserDao extends Dao<User> {
    public UserDao(){
        super("users");
    }
    public void login(String login, String password, OnSuccess onSuccess){
        find("login=" + login + "&password=" + password, onSuccess);
    }
}
