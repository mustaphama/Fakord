package dao;

import jakarta.persistence.EntityManager;
import metier.InviteCanal;

public class InviteCanalJPADAO implements InviteCanalDAO{
    public InviteCanalJPADAO(EntityManager em) {
    }

    @Override
    public void create(InviteCanal invite) {

    }
}
