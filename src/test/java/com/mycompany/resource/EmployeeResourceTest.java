package com.mycompany.resource;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.json.JSONException;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeResourceTest {

    //private static HttpServer server;
    private static WebTarget target;

    private static MongoCollection mongoCollection;


    @BeforeAll
    public static void beforeAllTests() throws IOException {
        //server = MainApp.startHttpServer();
        Client c = ClientBuilder.newClient();
        target = c.target("http://localhost:8002");

        String propFileName = "application.properties";
        InputStream inputStream = EmployeeResourceTest.class.getClassLoader().getResourceAsStream(propFileName);
        Properties props = new Properties();
        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        MongoClient mongoClient = new MongoClient(props.getProperty("MONGO_HOST_NAME"),
                Integer.parseInt(props.getProperty("MONGO_PORT")));
        mongoCollection = mongoClient.getDatabase(props.getProperty("MONGO_DB")).getCollection("employees");
    }

    @AfterAll
    public static void afterAllTests() {
        mongoCollection.drop();
    }

    @Order(1)
    @Test
    public void testSuccessfulCreation() throws JSONException {
        /**
         * An employee is successfully created
         */
        String json = "{\"id\":1000,\"name\":\"mike\",\"designation\":\"dev\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDuplicateKeyError() throws JSONException {
        /**
         * Employee id is unique and a Status code 500 should be returned a duplicate
         * id is used to create.
         */
        String json = "{\"id\":2111,\"name\":\"mike\",\"designation\":\"dev\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json, MediaType.valueOf("application/json")));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        response = target.path("api/employee/2111")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Order(2)
    @Test
    public void testSuccessfulGetEmployee(){
        /**
         * Fetch employee details of a valid employee
         */
        Response response = target.path("api/employee/1000")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetInvalidEmployee(){
        /**
         * Throw error with appropriate message if the employee id is invalid
         */
        Response response = target.path("api/employee/1")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Order(3)
    @Test
    public void testSuccessfulEditEmployee(){
        /**
         * An employee is successfully edited
         */
        String json = "{\"id\":1000,\"name\":\"mike_edited\",\"designation\":\"dev_edited\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(json, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEditInvalidEmployee(){
        /**
         * An employee is successfully edited
         */
        String json = "{\"id\":110,\"name\":\"mike_edited\",\"designation\":\"dev_edited\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(json, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Order(4)
    @Test
    public void testErrorPutEmployeeName(){
        /**
         * An employee is successfully patched with new name
         */
        String json = "{\"id\":1000,\"name\":\"mike_patched\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON).property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .put(Entity.entity(json, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Order(5)
    @Test
    public void testErrorPutEmployeeDesignation(){
        /**
         * An employee is successfully patched with new designation
         */
        String json = "{\"id\":1000,\"designation\":\"dev_patched\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(json, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPatchEmployeeDesignation(){
        /**
         * Invalid id when for patching employee designation
         */
        String json = "{\"id\":1,\"designation\":\"dev_patched\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(json, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPatchEmployeeName(){
        /**
         * Invalid id when for patching employee name
         */
        String json = "{\"id\":1,\"designation\":\"dev_patched\"}";
        Response response = target.path("api/employee")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(json, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Order(6)
    @Test
    public void testSuccessfulDeleteEmployee(){
        /**
         * Successfully delete employee
         */
        Response response = target.path("api/employee/1000")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteInvalidEmployee(){
        /**
         * Throw error with appropriate message if the employee id is invalid
         */
        Response response = target.path("api/employee/1")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Order(7)
    @Test
    public void testPublish(){
        /**
         * Successfully publish employee object to kafka
         */
        String json1 = "{\"id\":111,\"name\":\"publish_test1\",\"designation\":\"dev\"}";
        String json2 = "{\"id\":112,\"name\":\"publish_test2\",\"designation\":\"dev\"}";
        String json3 = "{\"id\":113,\"name\":\"publish_test3\",\"designation\":\"dev\"}";

        Response response = target.path("api/employee/publish")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json1, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        response = target.path("api/employee/publish")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json2, MediaType.valueOf("application/json")));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        response = target.path("api/employee/publish")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json3, MediaType.valueOf("application/json")));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Order(8)
    @Test
    public void testConsume(){
        Response response = target.path("api/employee/consume")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

}
