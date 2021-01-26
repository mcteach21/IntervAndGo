package mc.apps.demo0.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;

public class AdressDao extends Dao<Adress> {
    public AdressDao() {
        super("adresses");
    }

    public void ofClient(String code_client, OnSuccess onSuccess) {
        String whereClause = "client_id='" + code_client +"'";
        super.find(whereClause, onSuccess);
    }
    public void add(Adress u, OnSuccess onSuccess){
        try {
            String addClause="action=add&"
                    +"id="+ URLEncoder.encode("0", "utf-8")
                    +"&nom="+ URLEncoder.encode(u.getNom(), "utf-8")
                    +"&voie="+ URLEncoder.encode(u.getVoie(), "utf-8")
                    +"&cp="+ URLEncoder.encode(u.getVoie(), "utf-8")
                    +"&ville="+ URLEncoder.encode(u.getVille(), "utf-8")
                    +"&client_id="+ URLEncoder.encode(u.getClientId(), "utf-8");
            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
