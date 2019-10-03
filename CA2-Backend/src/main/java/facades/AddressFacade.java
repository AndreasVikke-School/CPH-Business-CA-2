package facades;

import entities.Address;
import entities.CityInfo;
import entities.Phone;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

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
        return getEntityManager().createNamedQuery("Address.findAll").getResultList();

    }

    @Override
    public Address add(Address address) {
        EntityManager em = getEntityManager();
        CityInfo ci;
        try {
            em.getTransaction().begin();
            try {
                ci = em.createNamedQuery("CityInfo.getByZipCity", CityInfo.class).setParameter("zip", address.getCity().getZip()).setParameter("city", address.getCity().getCity()).getSingleResult();
            } catch (NoResultException ex) {
                ci = new CityInfo(address.getCity().getZip(), address.getCity().getCity());
            }
            if (ci.getId() == null) {
                em.persist(ci);
            }
            address.setCity(ci);
            em.persist(address);
            em.getTransaction().commit();
            return address;
        } finally {
            em.close();
        }
    }

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
        }
    }
}
