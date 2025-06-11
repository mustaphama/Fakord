package dao;

import metier.Message;
import java.util.List;
import jakarta.persistence.EntityManager;

public interface MessageDAO {
    boolean sendPrivateMessage(int idSender, int idReceiver, String message);
    List<Message> getMessagesPrivee(int idSender, int idReceiver);
}