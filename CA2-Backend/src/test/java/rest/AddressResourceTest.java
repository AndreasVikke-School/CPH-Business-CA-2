package rest;

import entities.Address;
import entities.CityInfo;
import entities.dto.AddressDTO;
import entities.dto.CityInfoDTO;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
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
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class AddressResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Address a1, a2;

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
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

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
        CityInfo ci = new CityInfo("1234", "Lyngby");
        a1 = new Address("Streetname 1", ci);
        a2 = new Address("Streetname 2", ci);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InfoEntity.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.persist(ci);
            em.persist(a1);
            em.persist(a2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetById200() throws Exception {
        given()
                .contentType("application/json")
                .get("/address/" + a1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("street", equalTo(a1.getStreet()));
    }

    @Test
    public void testGetById400() throws Exception {
        given()
                .contentType("application/json")
                .get("/address/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }

    @Test
    public void testGetById404() throws Exception {
        given()
                .contentType("application/json")
                .get("/address/9").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }

    @Test
    public void testGetAll200() throws Exception {
        given()
                .contentType("application/json")
                .get("/address/all").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", equalTo(2));
    }

    @Test
    public void testAdd200() throws Exception {
        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "Streetname 3", new CityInfoDTO(0, "4321", "Nærum")))
                .post("/address/add").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("street", equalTo("Streetname 3"));
    }
    
    @Test
    public void testAdd400() throws Exception {
        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "", new CityInfoDTO(0, "4321", "Nærum")))
                .post("/address/add").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
        
        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "12345677", new CityInfoDTO(0, "4321", null)))
                .post("/address/add").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testEdit200() throws Exception {
        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "Streetname 3", new CityInfoDTO(0, "4321", "Nærum")))
                .put("/address/edit/" + a1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("street", equalTo("Streetname 3"));
    }
    
    @Test
    public void testEdit400() throws Exception {
        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "Streetname 3", new CityInfoDTO(0, "4321", "Nærum")))
                .put("/address/edit/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
        
        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "", new CityInfoDTO(0, "4321", "Nærum")))
                .put("/address/edit/" + a1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());

        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "Streetname 3", new CityInfoDTO(0, null, "Nærum")))
                .put("/address/edit/" + a1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testEdit404() throws Exception {
        given()
                .contentType("application/json")
                .body(new AddressDTO(0, "Streetname 3", new CityInfoDTO(0, "4321", "Nærum")))
                .put("/address/edit/99").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
    
    @Test
    public void testDelete200() throws Exception {
        given()
                .contentType("application/json")
                .delete("/address/delete/" + a1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("code", equalTo("200"));
    }
    
    @Test
    public void testDelete400() throws Exception {
        given()
                .contentType("application/json")
                .delete("/address/delete/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testDelete404() throws Exception {
        given()
                .contentType("application/json")
                .delete("/address/delete/99").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
}
