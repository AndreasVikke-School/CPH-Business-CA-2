package facades;

import entities.Address;
import entities.CityInfo;
import entities.Company;
import entities.Hobby;
import entities.InfoEntity;
import entities.Person;
import entities.Phone;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

/**
 *
 * @author Joe
 */
public class test {
    public static void main(String[] args) {
        CityInfo ci1 = new CityInfo("2800", "Skodsborg");
        
        Address a1 = new Address("Hejvejtest1", ci1);
        Address a2 = new Address("Hejvejtest2", ci1);
        
        List<Phone> phones = new ArrayList();
        phones.add(new Phone("12345678", "Moms phone"));
        phones.add(new Phone("12345679", "Dads phone"));
        
        List<Hobby> hobbies = new ArrayList();
        hobbies.add(new Hobby("Boxing", "Slap peoples head"));
        hobbies.add(new Hobby("KickBoxing", "Slapping peoples head with leg"));
        
        InfoEntity ie1 = new InfoEntity("cph@test.dk", phones, a1) {};
        
        Person p = new Person("Martin", "Frederiksen", hobbies, ie1);
        
        Company c = new Company("Cphbusiness", "School", "123456789", 25, 20000000, ie1);
        
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.DROP_AND_CREATE);
        EntityManager em = emf.createEntityManager();
        
        
        em.getTransaction().begin();
        em.persist(ci1);
        em.persist(a1);
        em.persist(phones.get(0));
        em.persist(phones.get(1));
//        em.persist(ie1);
        em.persist(hobbies.get(0));
        em.persist(hobbies.get(1));
        em.persist(p);
        em.persist(c);
        em.getTransaction().commit();
        
    }
}
