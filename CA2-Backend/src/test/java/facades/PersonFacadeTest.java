package facades;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.InfoEntity;
import utils.EMF_Creator;
import entities.Person;
import entities.Phone;
import java.util.ArrayList;
import java.util.List;
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
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    public PersonFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = PersonFacade.getPersonFacade(emf);
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
        facade = PersonFacade.getPersonFacade(emf);
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
            Phone p1 = new Phone("22883099", "Phone description");
            em.persist(p1);
            Phone p2 = new Phone("22759304", "Phone description 2 - The Redemption");
            em.persist(p2);
            List<Phone> phones = new ArrayList();
            phones.add(p1);
            phones.add(p2);
            CityInfo ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            Address a = new Address("Hellerupvej", ci);
            em.persist(a);
            InfoEntity ie = new InfoEntity("Email@email.com", phones, a);
            em.persist(ie);
            Hobby hobby1 = new Hobby("Revolutionist", "I like to start revoultions");
            em.persist(hobby1);
            Hobby hobby2 = new Hobby("Hobby", "Another hobby description");
            em.persist(hobby2);
            List<Hobby> hobbies = new ArrayList();
            hobbies.add(hobby1);
            hobbies.add(hobby2);
            Person p = new Person("Emil", "Svens", hobbies, ie);
            em.persist(p);

            p = new Person("Be", "Svens", hobbies, ie);
            em.persist(p);
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
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            Phone p1 = new Phone("22883099", "Phone description");
            em.persist(p1);
            Phone p2 = new Phone("22759304", "Phone description 2 - The Redemption");
            em.persist(p2);
            List<Phone> phones = new ArrayList();
            phones.add(p1);
            phones.add(p2);
            CityInfo ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            Address a = new Address("Hellerupvej", ci);
            em.persist(a);
            InfoEntity ie = new InfoEntity("Email@email.com", phones, a);
            em.persist(ie);
            Hobby hobby1 = new Hobby("Revolutionist", "I like to start revoultions");
            em.persist(hobby1);
            Hobby hobby2 = new Hobby("Hobby", "Another hobby description");
            em.persist(hobby2);
            List<Hobby> hobbies = new ArrayList();
            hobbies.add(hobby1);
            hobbies.add(hobby2);
            Person p = new Person("Emil", "Svens", hobbies, ie);
            em.persist(p);
            em.getTransaction().commit();
            expected = p.getId();
        } finally {
            em.close();
        }
        assertEquals(expected, facade.getById(expected).getId());
    }

    @Disabled
    @Test
    public void testGetAll() {
        assertEquals(2, facade.getAll().size(), "Expects two rows in the database");
    }

    @Test
    public void testAdd() {
        EntityManager em = emf.createEntityManager();
        int expected = 1;
        int result = 0;

        List<Hobby> hobbies = new ArrayList();
        Hobby hobby = new Hobby("Hiking", "Not fun");
        hobbies.add(hobby);
        List<Phone> phones = new ArrayList();
        Phone phone = new Phone("12345678", "work");
        phones.add(phone);
        CityInfo ci = new CityInfo("2900", "Hellerup");
        Address address = new Address("Hellerupvej", ci);
        InfoEntity ie = new InfoEntity("william@mail.dk", phones, address);

        try {
            em.getTransaction().begin();
            expected += em.createNamedQuery("Person.findAll").getResultList().size();
            em.persist(hobby);
            em.persist(phone);
            em.persist(ci);
            em.persist(address);
            em.persist(ie);
            em.getTransaction().commit();
            facade.add(new Person("William", "Test", hobbies, ie));
            result = em.createNamedQuery("Person.findAll").getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testEdit() {
        EntityManager em = emf.createEntityManager();
        String expected = "Bodil";
        Person result;

        List<Hobby> hobbies = new ArrayList();
        Hobby hobby = new Hobby("Hiking", "Much fun");
        hobbies.add(hobby);
        List<Phone> phones = new ArrayList();
        Phone phone = new Phone("12345678", "home");
        phones.add(phone);
        CityInfo ci = new CityInfo("2300", "Amager");
        Address address = new Address("Amagerbrogade", ci);
        InfoEntity ie = new InfoEntity("Marib@mail.dk", phones, address);
        Person p = new Person("Karsten", "Andersen", hobbies, ie);

        try {
            em.getTransaction().begin();
            em.persist(hobby);
            em.persist(phone);
            em.persist(ci);
            em.persist(address);
            em.persist(ie);
            em.persist(p);
            em.getTransaction().commit();
            p.setFirsName(expected);
            result = p;
        } finally {
            em.close();
        }
        assertEquals(expected, result.getFirsName());
    }

    @Test
    public void testDelete() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        
        List<Hobby> hobbies = new ArrayList();
        Hobby hobby = new Hobby("Running", " fun");
        hobbies.add(hobby);
        List<Phone> phones = new ArrayList();
        Phone phone = new Phone("123478", "Work");
        phones.add(phone);
        CityInfo ci = new CityInfo("2300", "Amager");
        Address address = new Address("Ørestads Boulevard", ci);
        InfoEntity ie = new InfoEntity("Marib@mail.dk", phones, address);
        Person p = new Person("Kim", "Andersen", hobbies, ie);
        
        try {
            em.getTransaction().begin();
            expected += em.createNamedQuery("Person.findAll").getResultList().size();
            em.persist(hobby);
            em.persist(phone);
            em.persist(ci);
            em.persist(address);
            em.persist(ie);
            em.persist(p);
            em.getTransaction().commit();
            Long deleteMe = p.getId();
            facade.delete(deleteMe);
            result = em.createNamedQuery("Person.findAll").getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected, result);
    }
    
    @Test
    public void testGetByPhone() {
        EntityManager em = emf.createEntityManager();
        Phone expected;
        Phone result;
        
        List<Hobby> hobbies = new ArrayList();
        Hobby hobby = new Hobby("Climbing", "Not fun");
        hobbies.add(hobby);
        List<Phone> phones = new ArrayList();
        Phone phone = new Phone("34343434", "Work");
        expected = new Phone("69696969", "Home");
        phones.add(phone);
        phones.add(expected);
        CityInfo ci = new CityInfo("2300", "Amager");
        Address address = new Address("Ørestads Boulevard", ci);
        InfoEntity ie = new InfoEntity("Tudekiks@mail.dk", phones, address);
        Person p = new Person("Tud", "Kim", hobbies, ie);
        
        
        try {
            em.getTransaction().begin();
            em.persist(hobby);
            em.persist(phone);
            em.persist(ci);
            em.persist(ie);
            em.persist(p);
            em.getTransaction().commit();
            result = p.getPhones().get(1);
        } finally {
            em.close();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testGetByCity() {
        EntityManager em = emf.createEntityManager();
        String expected = "Amager";
        String result;
        
        List<Hobby> hobbies = new ArrayList();
        Hobby hobby = new Hobby("Climbing", "Not fun");
        hobbies.add(hobby);
        List<Phone> phones = new ArrayList();
        Phone phone = new Phone("34343434", "Work");
        phones.add(phone);
        CityInfo ci = new CityInfo("2300", "Amager");
        Address address = new Address("Ørestads Boulevard", ci);
        InfoEntity ie = new InfoEntity("Tudekiks@mail.dk", phones, address);
        Person p = new Person("Tud", "Kim", hobbies, ie);
        
        
        try {
            em.getTransaction().begin();
            em.persist(hobby);
            em.persist(phone);
            em.persist(ci);
            em.persist(ie);
            em.persist(p);
            em.getTransaction().commit();
            result = p.getAddress().getCity().getCity();
        } finally {
            em.close();
        }
        assertEquals(expected, result);
    }
}
