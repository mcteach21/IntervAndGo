package mc.apps.demo0.dao;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import mc.apps.demo0.model.Affectation;
import mc.apps.demo0.model.Intervention;


public class AffectationDao extends Dao<Affectation>{
    private static final String TAG = "tests";
    public AffectationDao() {
        super("affectations");
    }

    public void add(Intervention interv, OnSuccess onSuccess){
        try {
            for (Affectation affectation : interv.getAffectations()) {
                String addClause = "action=add&"
                        + "id="+affectation.getId()
                        + "&intervention_id=" + URLEncoder.encode(affectation.getInterventionId(), "utf-8")
                        + "&technicien_id=" + URLEncoder.encode(affectation.getTechnicienId(), "utf-8");

                Log.i(TAG, addClause);
                super.add(addClause, onSuccess);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public void find(String interv_code, String tech_code, OnSuccess onSuccess){
        find("intervention_id=" + interv_code+"&technicien_id="+tech_code, onSuccess);
    }
}
