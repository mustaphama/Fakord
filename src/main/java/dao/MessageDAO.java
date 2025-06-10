package dao;



import metier.Message;

import java.util.List;

public interface MessageDAO {

    boolean sendPrivateMessage(int idSender, int idReceiver, String message);

    List<Message> getMessagesPrivee(int idSender, int idReceiver) throws java.sql.SQLException;
}
