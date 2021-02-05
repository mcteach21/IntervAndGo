package mc.apps.demo0.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.GpsPosition;

public class GpsDao extends Dao<GpsPosition> {

    public GpsDao() {
        super("gps");
    }

    public void find(String code, OnSuccess onSuccess) {
        String whereClause = "technicien_id='" + code +"'";
        super.find(whereClause, onSuccess);
    }
    public void add(GpsPosition u, OnSuccess onSuccess){
        try {
            String addClause="action=add&"
                    +"technicien_id="+ URLEncoder.encode(u.getTechnicien_id(), "utf-8")
                    +"&latitude="+ u.getLatitude()
                    +"&longitude="+ u.getLongitude();
            super.add(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void update(GpsPosition u, OnSuccess onSuccess){
        try {
            String addClause="action=update&"
                    +"technicien_id="+ URLEncoder.encode(u.getTechnicien_id(), "utf-8")
                    +"&latitude="+ u.getLatitude()
                    +"&longitude="+ u.getLongitude();
            super.update(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
