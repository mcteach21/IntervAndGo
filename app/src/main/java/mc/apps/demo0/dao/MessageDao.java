package mc.apps.demo0.dao;

import mc.apps.demo0.model.Message;

public class MessageDao extends Dao<Message> {

    public MessageDao() {
        super("messages");
    }
}
