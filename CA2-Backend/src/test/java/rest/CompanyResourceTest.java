/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import entities.Address;
import entities.CityInfo;
import entities.Company;
import entities.Hobby;
import entities.InfoEntity;
import entities.Person;
import entities.Phone;
import entities.dto.AddressDTO;
import entities.dto.CityInfoDTO;
import entities.dto.CompanyDTO;
import entities.dto.HobbyDTO;
import entities.dto.InfoEntityDTO;
import entities.dto.PhoneDTO;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author William
 */
public class CompanyResourceTest {
    
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Company company, company2;
    
    
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }
    
    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        Phone phone = new Phone("22883099", "Phone Work");
        Phone phone2 = new Phone("22883099", "Phone Work");
        List<Phone> phones = new ArrayList();
        List<Phone> phones2 = new ArrayList();
        phones.add(phone);
        phones2.add(phone2);
        CityInfo ci = new CityInfo("2900", "Hellerup");
        Address address = new Address("Hellerupvej", ci);
        InfoEntity ie = new InfoEntity("Email@email.com", phones, address);
        InfoEntity ie2 = new InfoEntity("Email@email.com", phones2, address);
        company = new Company("Himmelriget", "Making sure you don't get to heaven", "00000", 1, 80085, ie);
        company2 = new Company("Bilka", "Hvem ka'", "09234500", 8000, 8000000, ie2);
        
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InfoEntity.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.persist(phone);
            em.persist(phone2);
            em.persist(ci);
            em.persist(address);
            em.persist(ie);
            em.persist(ie2);
            em.persist(company);
            em.persist(company2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testGetAll200() throws Exception {
        given()
                .contentType("application/json")
                .get("/company/all").then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", equalTo(2));
    }
    
    //@Disabled
    @Test
    public void testGetById200() throws Exception {
        given()
                .contentType("application/json")
                .get("/company/" + company.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo(company.getName()));
    }
    
    @Test
    public void testGetById400() throws Exception {
        given()
                .contentType("applicaiton/json")
                .get("/company/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testGetById404() throws Exception {
        given()
                .contentType("application/json")
                .get("/company/87").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
    
    @Test
    public void testAdd200() throws Exception {
        CompanyDTO company = new CompanyDTO("Netbeans Inc", "Making IDE's that semi-work", "7563943", 10, 150, 
                new InfoEntityDTO(0, "netbeans@mail.com", new ArrayList<PhoneDTO>(), 
                        new AddressDTO(0, "Jydevej", new CityInfoDTO(0, "Aalborg", "8000"))));
        
        given()
                .contentType("application/json")
                .body(company)
                .post("company/add").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo("Netbeans Inc"));
    }
    
    @Test
    public void testAdd400() throws Exception {
        CompanyDTO company = new CompanyDTO(null, "Making IDE's that semi-work", "7563943", 10, 150, 
                new InfoEntityDTO(0, "netbeans@mail.com", new ArrayList<PhoneDTO>(), 
                        new AddressDTO(0, "Jydevej", new CityInfoDTO(0, "Aalborg", "8000"))));
        
        given()
                .contentType("application/json")
                .body(company)
                .post("company/add").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
        
        CompanyDTO company2 = new CompanyDTO("Netbeans inc", "Making IDE's that semi-work", "7563943", 10, 150, 
                new InfoEntityDTO(0, "netbeans@mail.com", null, 
                        new AddressDTO(0, "Jydevej", new CityInfoDTO(0, "Aalborg", "8000"))));
        
        given()
                .contentType("application/json")
                .body(company2)
                .post("company/add").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());;
        
    }
    
    @Test
    public void testEdit200() throws Exception {
        company.setName("ChangedName");
        
        given()
                .contentType("application/json")
                .body(company)
                .put("/company/edit/" + company.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo("ChangedName"));
    }
    
    @Test
    public void testEdit400() throws Exception {
        given()
                .contentType("application/json")
                .body(company)
                .put("/company/edit/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testEdit404() throws Exception {
        given()
                .contentType("application/json")
                .body(company)
                .put("/company/edit/88").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
    
    @Test
    public void testDelete200() throws Exception {
        given()
                .contentType("application/json")
                .delete("/company/delete/" + company.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("code", equalTo("200"));
    }
    
    @Test
    public void testDelete400() throws Exception {
        given()
                .contentType("application/json")
                .delete("/company/delete/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testDelete404() throws Exception {
        given()
                .contentType("application/json")
                .delete("/company/delete/88").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
    
}
