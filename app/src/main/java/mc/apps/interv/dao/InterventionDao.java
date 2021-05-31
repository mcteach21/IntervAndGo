package mc.apps.interv.dao;


import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import mc.apps.interv.model.Intervention;

public class InterventionDao extends Dao<Intervention>{
    private static final String TAG = "tests";
    public InterventionDao() {
        super("interventions");
    }

    public void find(String code, OnSuccess onSuccess){
        find("code=" + code, onSuccess);
    }
    public void findByTech(String tech_code, OnSuccess onSuccess){
        super.query("intervs_tech", tech_code, onSuccess);
    }

    public void findByTechs(List<String> techs_codes, OnSuccess onSuccess){
        super.queryCustom(techs_codes, onSuccess);
    }

    public void findByClient(String client_code, OnSuccess onSuccess){
        super.query("intervs_client", client_code, onSuccess);
    }
    public void findByTechSuperv(String code, OnSuccess onSuccess){
        super.query2("intervs_compte", code, onSuccess);
    }

    public void add(Intervention u, OnSuccess onSuccess){
        try {
            String addClause="action=add&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")
                    +"&clientid="+ URLEncoder.encode(u.getClientId(), "utf-8")
                    +"&desc="+ URLEncoder.encode(u.getDescription(), "utf-8")

                    +"&service_equip_cible="+ URLEncoder.encode(u.getServiceEquipCible(), "utf-8")
                    +"&materiel_necessaire="+ URLEncoder.encode(u.getMaterielNecessaire(), "utf-8")
                    +"&superviseurId="+ URLEncoder.encode(u.getSuperviseurId(), "utf-8")
                    +"&consignes="+URLEncoder.encode(u.getConsignes(), "utf-8")
                    +"&datedebut="+URLEncoder.encode(u.getDateDebutPrevue(), "utf-8")
                    +"&datefin="+URLEncoder.encode(u.getDateFinPrevue(), "utf-8")
                    +"&datedebutR=null"
                    +"&datefinR=null";

            Log.i(TAG, "addIntervention: **********************************************");
            Log.i(TAG,  addClause);
            Log.i(TAG, "addIntervention: **********************************************");

            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void update(Intervention u, OnSuccess onSuccess){
        try {
            String updateClause="action=update&"
                    +"id="+ URLEncoder.encode(u.getCode(), "utf-8")

                    +"&clientid="+ URLEncoder.encode(u.getClientId(), "utf-8")
                    +"&desc="+ URLEncoder.encode(u.getDescription(), "utf-8")
                    +"&service_equip_cible="+ URLEncoder.encode(u.getServiceEquipCible(), "utf-8")
                    +"&materiel_necessaire="+ URLEncoder.encode(u.getMaterielNecessaire(), "utf-8")
                    +"&superviseurId="+ URLEncoder.encode(u.getSuperviseurId(), "utf-8")

                    +"&datedebut="+URLEncoder.encode(u.getDateDebutPrevue()==null?"":u.getDateDebutPrevue(), "utf-8")
                    +"&datefin="+URLEncoder.encode(u.getDateFinPrevue()==null?"":u.getDateFinPrevue(), "utf-8")

                    +"&consignes="+URLEncoder.encode(u.getConsignes(), "utf-8")
                    +"&observations="+URLEncoder.encode(u.getObservations(), "utf-8")
                    
                    +"&datedebutR="+URLEncoder.encode(u.getDateDebutReelle()==null?"":u.getDateDebutReelle(), "utf-8")
                    +"&datefinR="+URLEncoder.encode(u.getDateFinReelle()==null?"":u.getDateFinReelle(), "utf-8")
                    +"&statut="+u.getStatutId();

            Log.i(TAG, "addIntervention: **********************************************");
            Log.i(TAG,  updateClause);
            Log.i(TAG, "addIntervention: **********************************************");


            super.update(updateClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
