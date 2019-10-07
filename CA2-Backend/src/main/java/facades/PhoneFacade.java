package facades;

import entities.Phone;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Martin Frederiksen
 */
public class PhoneFacade implements IFacade<Phone> {

    private static PhoneFacade instance;
    private static EntityManagerFactory emf;

    public PhoneFacade() {
    }

    public static PhoneFacade getPhoneFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PhoneFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Phone getById(long id) {
        return getEntityManager().find(Phone.class, id);
    }

    @Override
    public List<Phone> getAll() {
        return getEntityManager().createQuery("SELECT phone FROM Phone phone").getResultList();
    }

    @Override
    public Phone add(Phone phone) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(phone);
            em.getTransaction().commit();
            return phone;
        } finally {
            em.close();
        }
    }

    @Override
    public Phone edit(Phone phone) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(phone);
            em.getTransaction().commit();
            return phone;
        } finally {
            em.close();
        }
    }

    @Override
    public Phone delete(long id) {
        EntityManager em = getEntityManager();
        Phone p = em.find(Phone.class, id);
        if (p != null) {
            try {
                em.getTransaction().begin();
                em.remove(p);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }
        return p;
    }
}
