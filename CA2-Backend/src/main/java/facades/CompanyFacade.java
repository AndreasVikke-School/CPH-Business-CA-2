package facades;

import entities.Address;
import entities.CityInfo;
import entities.Company;
import entities.Hobby;
import entities.InfoEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import managers.FacadeManager;

/**
 *
 * @author Martin Frederiksen
 */
public class CompanyFacade implements IFacade<Company> {

    private static CompanyFacade instance;
    private static EntityManagerFactory emf;

    public CompanyFacade() {
    }

    public static CompanyFacade getCompanyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CompanyFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Company getById(long id) {
        return getEntityManager().find(Company.class, id);
    }

    @Override
    public List<Company> getAll() {
        return getEntityManager().createQuery("SELECT company FROM Company company").getResultList();
    }

    @Override
    public Company add(Company company) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Company c = FacadeManager.getSingleResult(em.createQuery("SELECT company FROM Company company WHERE company.cvr = :cvr AND company.name = :name", Company.class).setParameter("cvr", company.getCvr()).setParameter("name", company.getName()));
            if(c == null) {
                c = new Company(company.getName(), company.getDescription(), company.getCvr(), company.getEmployeeCount(), company.getMarketValue(), new InfoEntity(company.getEmail(), company.getPhones(), company.getAddress()));
            }
            em.persist(c);
            em.getTransaction().commit();
            return company;
        } finally {
            em.close();
        }
    }

    @Override
    public Company edit(Company company) {
        EntityManager em = getEntityManager();
        Company c = em.find(Company.class, company.getId());
        company = c;
        try {
            em.getTransaction().begin();
            em.merge(company);
            em.getTransaction().commit();
            return company;
        } finally {
            em.close();
        }
    }

    @Override
    public Company delete(long id) {
        EntityManager em = getEntityManager();
        Company c = em.find(Company.class, id);
        try {
            em.getTransaction().begin();
            em.remove(c);
            em.getTransaction().commit();
            return c;
        } finally {
            em.close();
        }
    }

    public Company getByPhone(String phone) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Missing exception case!
    public Company getByCVR(String cvr) {
        return getEntityManager().createQuery("SELECT company FROM Company company WHERE company.cvr = :cvr", Company.class).setParameter("cvr", cvr).getSingleResult();
    }
}
