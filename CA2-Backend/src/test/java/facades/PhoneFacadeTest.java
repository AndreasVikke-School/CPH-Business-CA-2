/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.Phone;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author APC
 */
public class PhoneFacadeTest {
    
    private static EntityManagerFactory emf;
    private static PhoneFacade facade;
    private List<Phone> phones;
    
    public PhoneFacadeTest() {
    }
    
    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = PhoneFacade.getPhoneFacade(emf);
    }
    
    
    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = PhoneFacade.getPhoneFacade(emf);
    }
    
    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }
    
    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        phones = new ArrayList();
        try {
            em.getTransaction().begin();

            em.createNamedQuery("InfoEntity.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();

            Phone phone = new Phone("12345678", "Home");
            Phone phone2 = new Phone("87654321", "Work");
            em.persist(phone);
            em.persist(phone2);

            em.getTransaction().commit();

            phones.add(phone);
            phones.add(phone2);
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testGetPhoneById() {
        Long expected = phones.get(0).getId();
        assertEquals(expected, facade.getById(expected).getId());
    }
    
    @Test
    public void testGetAll() {
        assertEquals(phones.size(), facade.getAll().size());
    }
    
    @Test
    public void testAdd() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        try {
            em.getTransaction().begin();
            
            expected = em.createQuery("SELECT p FROM Phone p", Phone.class).getResultList().size();
            em.persist(new Phone("8888888", "L'easy"));
            em.getTransaction().commit();
            
            result = em.createQuery("SELECT p FROM Phone p", Phone.class).getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected + 1, result);
    }
    
    @Test
    public void testEdit() {
        Phone result;
        String expected = "00000000";

        phones.get(0).setNumber(expected);
        facade.edit(phones.get(0));
        result = phones.get(0);

        assertEquals(expected, result.getNumber());
    }
    
    @Test
    public void testDelete() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        try {
            em.getTransaction().begin();
            expected = em.createQuery("SELECT p FROM Phone p", Phone.class).getResultList().size();
            facade.delete(phones.get(0).getId());
            result = em.createQuery("SELECT p FROM Phone p", Phone.class).getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected - 1, result);
    }
    
    
}
