package mc.apps.interv.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.interv.model.Client;

public class ClientDao extends Dao<Client> {

    public ClientDao() {
        super("clients");
    }

    public void find(String code, OnSuccess onSuccess) {
        String whereClause = "code='" + code +"'";
        super.find(whereClause, onSuccess);
    }

    public void add(Client u, OnSuccess onSuccess){
        try {
            String addClause="action=add&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")
                    +"&nom="+ URLEncoder.encode(u.getNom(), "utf-8")
                    +"&contact="+ URLEncoder.encode(u.getContact(), "utf-8")
                    +"&email="+ URLEncoder.encode(u.getEmail(), "utf-8")
                    +"&tel="+ URLEncoder.encode(u.getTelephone(), "utf-8");
            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public void update(Client u, OnSuccess onSuccess){
        try {
            String addClause="action=update&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")
                    +"&nom="+ URLEncoder.encode(u.getNom(), "utf-8")
                    +"&contact="+ URLEncoder.encode(u.getContact(), "utf-8")
                    +"&email="+ URLEncoder.encode(u.getEmail(), "utf-8")
                    +"&tel="+ URLEncoder.encode(u.getTelephone(), "utf-8");
            super.update(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
