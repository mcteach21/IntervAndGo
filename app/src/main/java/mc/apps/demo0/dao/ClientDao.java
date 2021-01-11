package mc.apps.demo0.dao;

import mc.apps.demo0.model.Client;

public class ClientDao extends Dao<Client> {

    public ClientDao() {
        super("clients");
    }
}
