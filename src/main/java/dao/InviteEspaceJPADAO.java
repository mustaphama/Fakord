package dao;

import jakarta.persistence.EntityManager;
import metier.InviteEspace;

public class InviteEspaceJPADAO implements InviteEspaceDAO {
    public InviteEspaceJPADAO(EntityManager em) {
    }

    @Override
    public void create(InviteEspace invite) {

    }
}
