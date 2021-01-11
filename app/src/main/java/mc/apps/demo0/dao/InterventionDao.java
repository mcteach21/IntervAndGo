package mc.apps.demo0.dao;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.demo0.model.Intervention;

public class InterventionDao extends Dao<Intervention>{

    public InterventionDao() {
        super("interventions");
    }

    public void add(Intervention u, OnSuccess onSuccess){

        try {

            String addClause="action=add&id=0&clientid=1&desc="+ URLEncoder.encode(u.getDescription(), "utf-8")
                    +"&comment="+URLEncoder.encode(u.getCommentaire(), "utf-8")
                    +"&datedebut="+URLEncoder.encode(u.getDateDebutPrevue(), "utf-8")
                    +"&datefin="+URLEncoder.encode(u.getDateFinPrevue(), "utf-8");

            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
