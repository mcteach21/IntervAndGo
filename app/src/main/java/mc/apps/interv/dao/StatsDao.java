package mc.apps.interv.dao;

import mc.apps.interv.model.ItemTotal;

public class StatsDao extends Dao<ItemTotal>{

    public StatsDao() {
        super("");
    }

    public void ItemTotalStats(int num, OnSuccess onSuccess){
        String named_query=(num==1)?"stat_client_count_intervs":"stat_tech_count_intervs";
        super.query(named_query, onSuccess);
    }

    /*public void clientStats(OnSuccess onSuccess){
        super.query("stat_client_count_intervs", onSuccess);
    }
    public void technicianStats(OnSuccess onSuccess){
        super.query("stat_tech_count_intervs", onSuccess);
    }*/
}
