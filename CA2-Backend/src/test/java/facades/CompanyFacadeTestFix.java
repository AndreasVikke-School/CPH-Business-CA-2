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
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Andreas Vikke
 */
//@Disabled
public class CompanyFacadeTestFix {
    private static EntityManagerFactory emf;
    private static CompanyFacade facade;
    private List<Company> companies;
    
    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = CompanyFacade.getCompanyFacade(emf);
    }
    
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
            
            Phone phone = new Phone("22155812", "Mobil");
            Phone phone2 = new Phone("22155813", "Home");
            em.persist(phone);
            em.persist(phone2);
            List<Phone> phones = new ArrayList();
            phones.add(phone);
            
            CityInfo ci = new CityInfo("0001", "Olympen");
            em.persist(ci);
            
            Address address = new Address("Hellerupvej 2", ci);
            em.persist(address);
            
            Address address2 = new Address("Hellerupvej 3", ci);
            em.persist(address2);
            
            InfoEntity ie = new InfoEntity("test@test.dk", phones, address);
            em.persist(ie);
            
            InfoEntity ie2 = new InfoEntity("test2@test2.dk", phones, address2);
            em.persist(ie2);
            
            Company company = new Company("Himmelriget", "Making sure you don't get to heaven", "12345679", 1, 80085, ie);
            em.persist(company);
            
            Company company2 = new Company("Cykelexperten", "cykel", "12345678", 800, 800000, ie);
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
        int expected;
        int result;
        try {
            em.getTransaction().begin();
            expected = em.createNamedQuery("Company.findAll").getResultList().size();
            
            Phone phone = new Phone("536244", "Work");
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
            expected = em.createNamedQuery("Company.findAll").getResultList().size();
            facade.delete(companies.get(0).getId());
            result = em.createNamedQuery("Company.findAll").getResultList().size();
        } finally {
            em.close();
        } 
        assertEquals(expected - 1, result);
    }
    
    @Disabled
    @Test
    public void testGetByPhone() throws Throwable {
        String number = companies.get(0).getPhones().get(0).getNumber();
        Long expected = companies.get(0).getId();
        assertEquals(expected, facade.getByPhone(number).getId());
    }
    
    @Test
    public void testGetByCvr() {
        String expected = companies.get(0).getCvr();
        assertEquals(expected, facade.getByCVR(expected).getCvr());
    }
}
