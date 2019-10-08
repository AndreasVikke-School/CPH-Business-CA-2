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
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

/**
 *
 * @author William
 */
//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private List<Person> people;

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
        people = new ArrayList();
        try {
            em.getTransaction().begin();

            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("InfoEntity.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();

            Phone p1 = new Phone("22883099", "Phone description");
            em.persist(p1);
            Phone p2 = new Phone("22759304", "Phone description 2 - The Redemption");
            em.persist(p2);
            List<Phone> phones = new ArrayList();
            List<Phone> phones2 = new ArrayList();
            phones.add(p1);
            phones2.add(p2);

            CityInfo ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);
            
            Address a = new Address("Hellerupvej", ci);
            em.persist(a);

            InfoEntity ie = new InfoEntity("Email@email.com", phones, a);
            InfoEntity ie2 = new InfoEntity("Email@email.com", phones2, a);
            em.persist(ie);
            em.persist(ie2);

            Hobby hobby1 = new Hobby("Revolutionist", "I like to start revoultions");
            em.persist(hobby1);
            Hobby hobby2 = new Hobby("Hobby", "Another hobby description");
            em.persist(hobby2);
            List<Hobby> hobbies = new ArrayList();
            hobbies.add(hobby1);
            hobbies.add(hobby2);

            Person person = new Person("Emil", "Svens", hobbies, ie);
            em.persist(person);
            Person person2 = new Person("Be", "Svens", hobbies, ie2);
            em.persist(person2);

            people.add(person);
            people.add(person2);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetById() {
        Long expected = people.get(0).getId();
        assertEquals(expected, facade.getById(expected).getId());
    }

    @Test
    public void testGetAll() {
        assertEquals(2, facade.getAll().size(), "Expects two rows in the database");
    }

    @Test
    public void testAdd() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        try {
            em.getTransaction().begin();
            expected += em.createQuery("SELECT p FROM Person p", Phone.class).getResultList().size();

            List<Hobby> hobbies = new ArrayList();
            Hobby hobby = new Hobby("Hiking", "Not fun");
            hobbies.add(hobby);
            em.persist(hobby);

            List<Phone> phones = new ArrayList();
            Phone phone = new Phone("12345678", "work");
            phones.add(phone);
            em.persist(phone);

            CityInfo ci = new CityInfo("2900", "Hellerup");
            em.persist(ci);

            Address address = new Address("Hellerupvej", ci);
            em.persist(address);

            InfoEntity ie = new InfoEntity("william@mail.dk", phones, address);
            em.persist(ie);
            em.getTransaction().commit();

            facade.add(new Person("William", "Test", hobbies, ie));
            result = em.createQuery("SELECT p FROM Person p", Phone.class).getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected + 1, result);
    }

    @Test
    public void testEdit() {
        String expected = "Bodil";
        Person result;

        people.get(0).setFirsName(expected);
        facade.edit(people.get(0));
        result = people.get(0);

        assertEquals(expected, result.getFirsName());
    }

    @Test
    public void testDelete() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        try {
            em.getTransaction().begin();
            expected = em.createQuery("SELECT p FROM Person p", Phone.class).getResultList().size();
            facade.delete(people.get(0).getId());
            result = em.createQuery("SELECT p FROM Person p", Phone.class).getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected - 1, result);
    }

    //@Disabled
    @Test
    public void testGetByPhone() {
        String num = people.get(0).getPhones().get(0).getNumber();
        Long expected = people.get(0).getId();

     
        assertEquals(expected, facade.getByPhone(num).getId());
    }

    
    @Disabled
    @Test
    public void testGetPersonsByCity() {
        
        assertEquals(people, facade.getPersonsByCity("Hellerup"));
        
    }
}
