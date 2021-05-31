package mc.apps.interv.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.interv.model.Adress;

public class AdressDao extends Dao<Adress> {
    private static final String TAG = "tests";

    public AdressDao() {
        super("adresses");
    }

    public void ofClient(String code_client, OnSuccess onSuccess) {
        String whereClause = "client_id='" + code_client +"'";
        super.find(whereClause, onSuccess);
    }
    public void add(Adress u, OnSuccess onSuccess){
        action(u, onSuccess, "add");
    }
    public void update(Adress u, OnSuccess onSuccess){
        action(u, onSuccess, "add");
    }
    private void action(Adress u, OnSuccess onSuccess,String action) {
        try {
            String addClause="action="+action+"&"
                    +"id="+ URLEncoder.encode("0", "utf-8")
                    +"&nom="+ URLEncoder.encode(u.getNom(), "utf-8")
                    +"&voie="+ URLEncoder.encode(u.getVoie(), "utf-8")
                    +"&cp="+ URLEncoder.encode(u.getVoie(), "utf-8")
                    +"&ville="+ URLEncoder.encode(u.getVille(), "utf-8")
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
