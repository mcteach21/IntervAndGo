package mc.apps.demo0.dao;


import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.demo0.model.Intervention;

public class InterventionDao extends Dao<Intervention>{

    private static final String TAG = "tests";

    public InterventionDao() {
        super("interventions");
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

                    +"&comment="+URLEncoder.encode(u.getCommentaire(), "utf-8")
                    +"&datedebut="+URLEncoder.encode(u.getDateDebutPrevue(), "utf-8")
                    +"&datefin="+URLEncoder.encode(u.getDateFinPrevue(), "utf-8");

            Log.i(TAG, "addIntervention: **********************************************");
            Log.i(TAG,  addClause);
            Log.i(TAG, "addIntervention: **********************************************");

            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
