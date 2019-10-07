package facades;

import utils.EMF_Creator;
import entities.Address;
import entities.CityInfo;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class AddressFacadeTest {

    private static EntityManagerFactory emf;
    private static AddressFacade facade;

    public AddressFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = AddressFacade.getAddressFacade(emf);
    }

    /*   **** HINT **** 
        A better way to handle configuration values, compared to the UNUSED example above, is to store those values
        ONE COMMON place accessible from anywhere.
        The file config.properties and the corresponding helper class utils.Settings is added just to do that. 
        See below for how to use these files. This is our RECOMENDED strategy
     */
    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = AddressFacade.getAddressFacade(emf);
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
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            CityInfo ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            em.persist(new Address("Strandvejen", ci));
            em.persist(new Address("Hellerupvej", ci));

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetById() {
        EntityManager em = emf.createEntityManager();
        Long expected = 0L;
        try {
            em.getTransaction().begin();
            CityInfo ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            Address a = new Address("Bernstorfsvej", ci);
            em.persist(a);
            em.getTransaction().commit();
            expected = a.getId();
        } finally {
            em.close();
        }
        assertEquals(expected, facade.getById(expected).getId());
    }

    @Test
    public void testGetAll() {
        assertEquals(2, facade.getAll().size(), "Expects two rows in the database");
    }

    @Test
    public void testAdd() {
        EntityManager em = emf.createEntityManager();
        int expected = 1;
        int result = 0;
        CityInfo ci;
        try {
            em.getTransaction().begin();
            expected += em.createNamedQuery("Address.findAll").getResultList().size();
            ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            em.getTransaction().commit();
            facade.add(new Address("Dagmarvej", ci));
            result = em.createNamedQuery("Address.findAll").getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testEdit() {
        EntityManager em = emf.createEntityManager();
        Address result;
        String expected = "Nytvejnavn";
        try {
            em.getTransaction().begin();
            CityInfo ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            Address a = new Address("Oliviavej",ci);
            em.persist(a);
            em.getTransaction().commit();
            a.setStreet(expected);
            result = a;
        } finally {
            em.close();
        }
        assertEquals(expected,result.getStreet());
    }

    @Test
    public void testDelete() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        CityInfo ci;
        try {
            em.getTransaction().begin();
            expected += em.createNamedQuery("Address.findAll").getResultList().size();
            ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            Address a = new Address("Tuborgvej", ci);
            em.persist(a);
            em.getTransaction().commit();
            Long toDel = a.getId();
            facade.delete(toDel);
            result = em.createNamedQuery("Address.findAll").getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected, result);
    }

}
