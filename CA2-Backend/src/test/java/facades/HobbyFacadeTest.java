/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.Hobby;
import entities.Person;
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
import utils.EMF_Creator;

/**
 *
 * @author William
 */
//@Disabled
public class HobbyFacadeTest {
    
    private static EntityManagerFactory emf;
    private static HobbyFacade facade;
    private List<Hobby> hobbies;

    public HobbyFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = HobbyFacade.getHobbyFacade(emf);
    }

    /*   **** HINT **** 
        A better way to handle configuration values, compared to the UNUSED example above, is to store those values
        ONE COMMON place accessible from anywhere.
        The file config.properties and the corresponding helper class utils.Settings is added just to do that. 
        See below for how to use these files. This is our RECOMENDED strategy
     */
    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = HobbyFacade.getHobbyFacade(emf);
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
        hobbies = new ArrayList();
        try {
            em.getTransaction().begin();

            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();

            Hobby hobby = new Hobby("Snorkling", "At dykke");
            Hobby hobby2 = new Hobby("Løbe", "Træning for benene");
            em.persist(hobby);
            em.persist(hobby2);

            em.getTransaction().commit();

            hobbies.add(hobby);
            hobbies.add(hobby2);
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetById() {
        Long expected = hobbies.get(0).getId();
        assertEquals(expected, facade.getById(expected).getId());
    }

    @Test
    public void testGetAll() {
        assertEquals(hobbies.size(), facade.getAll().size(), "Expects two rows in the database");
    }

    @Test
    public void testAdd() {
        EntityManager em = emf.createEntityManager();
        int expected;
        int result;
        try {
            expected = em.createQuery("SELECT h FROM Hobby h", Hobby.class).getResultList().size();
            facade.add(new Hobby("Cykle", "Det går hurtigt"));
            result = em.createQuery("SELECT h FROM Hobby h", Hobby.class).getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected + 1, result);
    }

    @Test
    public void testEdit() {
        Hobby result;
        String expected = "Springgymnastik";

        hobbies.get(0).setName(expected);
        facade.edit(hobbies.get(0));
        result = hobbies.get(0);

        assertEquals(expected, result.getName());
    }

    @Test
    public void testDelete() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        try {
            em.getTransaction().begin();
            expected = em.createQuery("SELECT h FROM Hobby h", Hobby.class).getResultList().size();
            facade.delete(hobbies.get(0).getId());
            result = em.createQuery("SELECT h FROM Hobby h", Hobby.class).getResultList().size();
        } finally {
            em.close();
        }
        assertEquals(expected - 1, result);
    }

    @Test
    public void testGetPersonCountByHobby() {
        Long id = hobbies.get(0).getId();
        Long expected = new Long(hobbies.get(0).getPersons().size());
        Long result = facade.getPersonCountByHobby(id);
        assertEquals(expected, result);
    }
    
//    @Test
//    public void testGetPersonsByHobby() {
//        List<Person> expected = hobbies.get(0).getPersons();
//        List<Person> result = facade.getPersonsByHobby(hobbies.get(0).getId());
//        assertEquals(expected, result);
//    }
    
}
