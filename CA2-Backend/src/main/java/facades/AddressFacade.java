package facades;

import entities.Address;
import entities.Phone;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Joe
 */
public class AddressFacade implements IFacade<Address> {
    private static AddressFacade instance;
    private static EntityManagerFactory emf;

    public AddressFacade() {
    }
    
    public static AddressFacade getAddressFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AddressFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Address getById(long id) {
        return getEntityManager().find(Address.class, id);
    }

    @Override
    public List<Address> getAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Address.findAll").getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Address add(Address address) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(address);
            em.getTransaction().commit();
            return address;
        } finally {
            em.close();
        }}

    @Override
    public Address edit(Address address) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(address);
            em.getTransaction().commit();
            return address;
        } finally {
            em.close();
        }
    }

    @Override
    public Address delete(long id) {
        EntityManager em = getEntityManager();
        Address a = em.find(Address.class, id);
        if (a != null) {
            try {
                em.getTransaction().begin();
                em.remove(a);
                em.getTransaction().commit();
                return a;
            } finally {
                em.close();
            }
        } else {
            throw new IllegalArgumentException("Not a valid id supplied");
        }}
}
