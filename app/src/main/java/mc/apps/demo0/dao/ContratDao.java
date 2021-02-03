package mc.apps.demo0.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.demo0.model.Contrat;


public class ContratDao extends Dao<Contrat> {
    private static final String TAG = "tests";

    public ContratDao() {
        super("contrats");
    }

    public void ofClient(String code_client, OnSuccess onSuccess) {
        String whereClause = "client_id='" + code_client +"'";
        super.find(whereClause, onSuccess);
    }
    public void add(Contrat u, OnSuccess onSuccess){
        try {
            String addClause="action=add&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")
                    +"&nom="+ URLEncoder.encode(u.getNom(), "utf-8")
                    +"&client_id="+ URLEncoder.encode(u.getClientId(), "utf-8");
            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
