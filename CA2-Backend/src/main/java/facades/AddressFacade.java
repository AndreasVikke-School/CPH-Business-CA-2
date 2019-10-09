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
        CityInfo ci = FacadeManager.getSingleResult(em.createNamedQuery("CityInfo.getByZipCity", CityInfo.class).setParameter("zip", address.getCityInfo().getZip()).setParameter("city", address.getCityInfo().getCity()));
        Address a = FacadeManager.getSingleResult(em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.cityInfo.id = :id", Address.class).setParameter("street", address.getStreet()).setParameter("id", address.getCityInfo().getId()));
        try {
            if (ci == null) {
                em.getTransaction().begin();
                em.persist(address.getCityInfo());
                em.getTransaction().commit();
            } else {
                address.setCityInfo(ci);
            }

            if (a == null) {
                a = address;
                em.getTransaction().begin();
                em.persist(a);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
        return a;
    }

    @Override
    public Address edit(Address address) {
        EntityManager em = getEntityManager();
        CityInfo ci = FacadeManager.getSingleResult(em.createNamedQuery("CityInfo.getByZipCity", CityInfo.class).setParameter("zip", address.getCityInfo().getZip()).setParameter("city", address.getCityInfo().getCity()));
        try {
            em.getTransaction().begin();
            if(ci == null) {
                em.persist(address.getCityInfo());
            } else {
                address.setCityInfo(ci);
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
            } finally {
                em.close();
            }
        }
        return a;
    }
    
}
