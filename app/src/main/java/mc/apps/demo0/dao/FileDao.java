package mc.apps.demo0.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mc.apps.demo0.model.InterventionFile;
import mc.apps.demo0.model.Message;

public class FileDao extends Dao<InterventionFile> {

    public FileDao() {
        super("interventions_files");
    }

    private void action(InterventionFile interv_file, String action, OnSuccess onSuccess){
        try {
            String addClause="action="+action+"&"
                    +"id="+ interv_file.getId()
                    +"&intervention_id="+ URLEncoder.encode(interv_file.getInterventionId(), "utf-8")
                    +"&filename="+ URLEncoder.encode(interv_file.getFilename(), "utf-8")
                    +"&photo="+ interv_file.getPhoto();

            if(action.equals("add"))
                super.add(addClause, onSuccess);
            if(action.equals("update"))
                super.update(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void find(String intervention_id, OnSuccess onSuccess){
        try {
            super.find("intervention_id='" + URLEncoder.encode(intervention_id, "utf-8")+"'", onSuccess);
        } catch (UnsupportedEncodingException e) {}
    }

    public void add(InterventionFile interv_file, OnSuccess onSuccess){
        this.action(interv_file, "add", onSuccess);
    }
    public void update(InterventionFile interv_file, OnSuccess onSuccess){
        this.action(interv_file, "update", onSuccess);
    }
}
