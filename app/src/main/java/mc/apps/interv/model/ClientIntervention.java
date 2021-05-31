package mc.apps.interv.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientIntervention implements Serializable {
    private Client client;
    private List<Intervention> interventions;

    public ClientIntervention(Client client) {
        this.client = client;
        this.interventions = new ArrayList<Intervention>();
    }
    public ClientIntervention(Client client, List<Intervention> interventions) {
        this.client = client;
        this.interventions = interventions;
    }

    public void addIntervention(Intervention intervention) {
        interventions.add(intervention);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }

    public void setInterventions(List<Intervention> interventions) {
        this.interventions = interventions;
    }
}
