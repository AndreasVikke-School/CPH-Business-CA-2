package facades;

import entities.Address;
import entities.CityInfo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import managers.FacadeManager;

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
        try {
            em.getTransaction().begin();
            CityInfo ci = FacadeManager.getSingleResult(em.createNamedQuery("CityInfo.getByZipCity", CityInfo.class).setParameter("zip", address.getCity().getZip()).setParameter("city", address.getCity().getCity()));
            if (ci == null) {
                ci = new CityInfo(address.getCity().getZip(), address.getCity().getCity());
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
        CityInfo ci = FacadeManager.getSingleResult(em.createNamedQuery("CityInfo.getByZipCity", CityInfo.class).setParameter("zip", address.getCity().getZip()).setParameter("city", address.getCity().getCity()));
        try {
            em.getTransaction().begin();
            if(ci == null) {
                em.persist(address.getCity());
            } else {
                address.setCity(ci);
            }
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
