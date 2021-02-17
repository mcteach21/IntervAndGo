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
        action(u, onSuccess,"add");

    }
    public void update(Contrat u, OnSuccess onSuccess){
        action(u, onSuccess,"add");

    }

    private void action(Contrat u, OnSuccess onSuccess, String action) {
        try {
            String addClause="action="+action+"&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")
                    +"&nom="+ URLEncoder.encode(u.getNom(), "utf-8")
                    +"&client_id="+ URLEncoder.encode(u.getClientId(), "utf-8");

            if(action.equals("add"))
                super.add(addClause, onSuccess);
            else
                super.update(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
