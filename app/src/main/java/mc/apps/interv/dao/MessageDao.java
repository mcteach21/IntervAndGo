package mc.apps.interv.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import mc.apps.interv.model.Message;
public class MessageDao extends Dao<Message> {

    public MessageDao() {
        super("messages");
    }

    private void action(Message message, String action, OnSuccess onSuccess){
        try {
            String addClause="action="+action+"&"
                    +"id="+ message.getId()
                    +"&message="+ URLEncoder.encode(message.getMessage(), "utf-8")
                    +"&from_user="+ URLEncoder.encode(message.getFromUser(), "utf-8")
                    +"&to_user="+ URLEncoder.encode(message.getToUser(), "utf-8")
                    +"&seen="+ message.getSeen();

            if(action.equals("add"))
                super.add(addClause, onSuccess);
            if(action.equals("update"))
                super.update(addClause, onSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void find(String tech_code, OnSuccess onSuccess){
        try {
            super.find("to_user='" + URLEncoder.encode(tech_code, "utf-8")+"'&seen=0", onSuccess);
        } catch (UnsupportedEncodingException e) {}
    }

    public void add(Message message, OnSuccess onSuccess){
        this.action(message, "add", onSuccess);
    }
    public void update(Message message, OnSuccess onSuccess){
        this.action(message, "update", onSuccess);
    }
}
