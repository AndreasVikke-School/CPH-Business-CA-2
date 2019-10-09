package rest;

import entities.Hobby;
import entities.dto.PersonDTO;
import entities.dto.HobbyDTO;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
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
@Disabled
public class HobbyResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Hobby h1, h2;

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
        h1 = new Hobby("Foodball", "Ball Foot");
        h2 = new Hobby("Fishing", "Lake Fishing");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.persist(h1);
            em.persist(h2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetById200() throws Exception {
        given()
                .contentType("application/json")
                .get("/hobby/" + h1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo(h1.getName()));
    }

    @Test
    public void testGetById400() throws Exception {
        given()
                .contentType("application/json")
                .get("/hobby/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }

    @Test
    public void testGetById404() throws Exception {
        given()
                .contentType("application/json")
                .get("/hobby/9").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }

    @Test
    public void testGetAll200() throws Exception {
        given()
                .contentType("application/json")
                .get("/hobby/all").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", equalTo(2));
    }

    @Test
    public void testAdd200() throws Exception {
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, "Riding", "Test Hobby", new ArrayList<PersonDTO>()))
                .post("/hobby/add").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo("Riding"));
    }
    
    @Test
    public void testAdd400() throws Exception {
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, "", "Test Hobby", new ArrayList<PersonDTO>()))
                .post("/hobby/add").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, "Riding", null, new ArrayList<PersonDTO>()))
                .post("/hobby/add").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testEdit200() throws Exception {
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, "Programming", "Test Edit", new ArrayList<PersonDTO>()))
                .put("/hobby/edit/" + h1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo("Programming"));
    }
    
    @Test
    public void testEdit400() throws Exception {
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, "Programming", "Test Edit", new ArrayList<PersonDTO>()))
                .put("/hobby/edit/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
        
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, "Programming", "", new ArrayList<PersonDTO>()))
                .put("/hobby/edit/" + h1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
        
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, null, "Test Edit", new ArrayList<PersonDTO>()))
                .put("/hobby/edit/" + h1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testEdit404() throws Exception {
        given()
                .contentType("application/json")
                .body(new HobbyDTO(0, "Programming", "Test Edit", new ArrayList<PersonDTO>()))
                .put("/hobby/edit/99").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
    
    @Test
    public void testDelete200() throws Exception {
        given()
                .contentType("application/json")
                .delete("/hobby/delete/" + h1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("code", equalTo("200"));
    }
    
    @Test
    public void testDelete400() throws Exception {
        given()
                .contentType("application/json")
                .delete("/hobby/delete/0").then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode());
    }
    
    @Test
    public void testDelete404() throws Exception {
        given()
                .contentType("application/json")
                .delete("/hobby/delete/99").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
}
