package mc.apps.demo0.dao;

import java.net.URLEncoder;
import java.util.List;

import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;

public class ClientDao extends Dao<Client> {

    public ClientDao() {
        super("clients");
    }

    public void find(String code, OnSuccess onSuccess) {
        String whereClause = "code='" + code +"'";
        super.find(whereClause, onSuccess);
    }

}
