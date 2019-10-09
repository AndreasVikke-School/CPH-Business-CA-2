/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.Address;
import entities.CityInfo;
import entities.Company;
import entities.InfoEntity;
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
import utils.EMF_Creator;

/**
 *
 * @author William
 */
//@Disabled
public class CompanyFacadeTest {

    private static EntityManagerFactory emf;
    private static CompanyFacade facade;
    private List<Company> companies;

    public CompanyFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = CompanyFacade.getCompanyFacade(emf);
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
        facade = CompanyFacade.getCompanyFacade(emf);
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
        companies = new ArrayList();
        try {
            em.getTransaction().begin();
            
            em.createNamedQuery("InfoEntity.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            
            Phone phone = new Phone("22155812", "Boss");
            em.persist(phone);
            Phone phone2 = new Phone("89756410", "Home");
            em.persist(phone2);
            
            List<Phone> phones = new ArrayList();
            phones.add(phone);
            List<Phone> phones2 = new ArrayList();
            phones2.add(phone2);
            
            CityInfo ci = new CityInfo("0001", "Olympen");
            em.persist(ci);
            
            Address address = new Address("Hellerupvej", ci);
            em.persist(address);
            
            InfoEntity ie = new InfoEntity("william@erok.dk", phones, address);
            em.persist(ie);
            
            InfoEntity ie2 = new InfoEntity("william@erok.dk", phones2, address);
            em.persist(ie2);
            
            Company company = new Company("Himmelriget", "Making sure you don't get to heaven", "00000", 1, 80085, ie);
            em.persist(company);
            
            Company company2 = new Company("Bilka", "Hvem ka'", "09234500", 8000, 8000000, ie2);
            em.persist(company2);
            
            em.getTransaction().commit();
            
            companies.add(company);
            companies.add(company2);
        } finally {
            em.close();
        }
    }
    
    @Test
    public void getById() {
        Long expected = companies.get(0).getId();
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
            expected += em.createNamedQuery("Company.findAll").getResultList().size();
            
            Phone phone = new Phone("536244", "Wrok");
            em.persist(phone);
            List<Phone> phones = new ArrayList();
            phones.add(phone);
            
            CityInfo ci = new CityInfo("1719", "Vesterbro");
            em.persist(ci);
            
            Address address = new Address("Krusågade", ci);
            em.persist(address);
            
            InfoEntity ie = new InfoEntity("sushibest@gmail.dk", phones, address);
            em.persist(ie);
            
            em.getTransaction().commit();
            Company company = new Company("SushiBest", "Go sushi", "124335", 9, 975, ie);
            
            facade.add(company);
            result = em.createNamedQuery("Company.findAll").getResultList().size();
        } finally {
            em.close();
        } 
        assertEquals(expected + 1, result);
    }

    @Test
    public void testEdit() {
        Company result;
        String expected = "NytNavn";
        
        companies.get(0).setName(expected);
        facade.edit(companies.get(0));
        
        result = companies.get(0);
        
        assertEquals(expected, result.getName());
    }
    
    @Test
    public void testDelete() {
        EntityManager em = emf.createEntityManager();
        int expected = 0;
        int result = 0;
        try {
            em.getTransaction().begin();
            expected = em.createNamedQuery("Company.findAll").getResultList().size();
            facade.delete(companies.get(0).getId());
            result = em.createNamedQuery("Company.findAll").getResultList().size();
        } finally {
            em.close();
        } 
        assertEquals(expected - 1, result);
    }
    
    //@Disabled 
    @Test
    public void testGetByPhone() throws Throwable {
        String num = companies.get(0).getPhones().get(0).getNumber();
        Long expected = companies.get(0).getId();
        assertEquals(expected, facade.getByPhone(num).getId());
    }
    
    @Test
    public void testGetByCvr() {
        String cvr = companies.get(0).getCvr();
        Long expected = companies.get(0).getId();
        assertEquals(expected, facade.getByCVR(cvr).getId());
    }
}
