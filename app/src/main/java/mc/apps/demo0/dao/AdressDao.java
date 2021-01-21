package mc.apps.demo0.dao;

import mc.apps.demo0.model.Adress;

public class AdressDao extends Dao<Adress> {
    public AdressDao() {
        super("adresses");
    }

    public void ofClient(String code_client, OnSuccess onSuccess) {
        String whereClause = "client_id='" + code_client +"'";
        super.find(whereClause, onSuccess);
    }
}
