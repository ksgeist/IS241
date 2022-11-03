package com.turtleshelldevelopment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static spark.Spark.*;


public class TestLogin {
    static HttpClient client;
    private static final String serviceURL = "http://localhost:8091";
    private static final String baseURL = "/api";
    @Before
    public void setup() {
        //System.setProperty("user.dir", Path.of(System.getProperty("user.dir")).getParent().toString());
        //Setup backend to Production
        BackendServer.main(new String[]{"-ePROD"});
        client = HttpClient.newHttpClient();
        awaitInitialization();
    }
    @After
    public void tearDown() {
        stop();
        awaitStop();
    }

    @DisplayName("Create Account")
    @Order(100)
    @Test
    public void createAccount() throws URISyntaxException, IOException, InterruptedException {
        assertNotNull(client);
        String loginUrl = baseURL + "/account/new?username=TestUser&password=123&readPatient=true&editUsers=true&writePatient=true";
        System.out.println("Creating User...");
        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + loginUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Body Returned: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @DisplayName("Login Route Exists")
    @Test
    public void loginRouteExists() throws URISyntaxException, IOException, InterruptedException {
        assertNotNull(client);
        String loginUrl = baseURL + "/login";

        HttpRequest request = HttpRequest.newBuilder().uri(new URI(serviceURL + loginUrl)).POST(HttpRequest.BodyPublishers.ofString("")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode());
    }
    @DisplayName("Check Invalid User")
    @Test
    public void loginRouteInvalidUser() throws URISyntaxException, IOException, InterruptedException {
        assertNotNull(client);
        String loginUrl = baseURL + "/login";

        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + loginUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"InvalidUser\",\"password\":\"1234\"}")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode());
    }

    @DisplayName("Check Valid User")
    @Test
    public void loginRouteValidUser() throws URISyntaxException, IOException, InterruptedException, JSONException {
        assertNotNull(client);
        String loginUrl = baseURL + "/login";
        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + loginUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"TestUser\",\"password\":\"123\"}")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Returned Body: " + response.body());
        assertEquals(200, response.statusCode());
        assertNotEquals("", response.body());
        JSONObject bodyJson = new JSONObject(response.body());
        assertNotEquals(null, bodyJson);
        assertEquals(true, bodyJson.get("success"));
    }

    @DisplayName("Login with valid username and invalid password")
    @Test
    public void loginRouteValidUserWithInvalidPassword() throws URISyntaxException, JSONException, IOException, InterruptedException {
        assertNotNull(client);
        String loginUrl = baseURL + "/login";

        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI( serviceURL + loginUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"TestUser\",\"password\":\"1234\"}")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Returned Body: " + response.body());
        assertEquals(401, response.statusCode());
        assertNotEquals("", response.body());
        JSONObject bodyJson = new JSONObject(response.body());
        assertNotEquals(null, bodyJson);
        assertEquals(false, bodyJson.get("success"));
    }

    @DisplayName("Invalid User & Correct Password")
    @Test
    public void checkInvalidUserValidPassword() throws JSONException, IOException, InterruptedException, URISyntaxException {
        assertNotNull(client);
        String loginUrl = baseURL + "/login";

        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + loginUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"InvalidUser\",\"password\":\"123\"}")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Returned Body: " + response.body());
        assertEquals(401, response.statusCode());
        assertNotEquals("", response.body());
        JSONObject bodyJson = new JSONObject(response.body());
        assertNotEquals(null, bodyJson);
        assertEquals(false, bodyJson.get("success"));
    }
    @DisplayName("Check for SQL Injection on Username")
    @Test
    public void testSQLInjectionUsername() throws JSONException, IOException, InterruptedException, URISyntaxException {
        assertNotNull(client);
        String loginUrl = baseURL + "/login";

        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + loginUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"OR%201=1;#\",\"password\":\"123\"}")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Returned Body: " + response.body());
        assertEquals(401, response.statusCode());
        assertNotEquals("", response.body());
        JSONObject bodyJson = new JSONObject(response.body());
        assertNotEquals(null, bodyJson);
        assertEquals(false, bodyJson.get("success"));
        assertNull(bodyJson.get("jwt"));
    }

    @DisplayName("Check for SQL Injection on Password")
    @Test
    public void testSQLInjectionPassword() throws JSONException, IOException, InterruptedException, URISyntaxException {
        assertNotNull(client);
        String loginUrl = baseURL + "/login";

        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + loginUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"TestUser\",\"password\":\"OR%201=1;#\"}")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Returned Body: " + response.body());
        assertEquals(401, response.statusCode());
        assertNotEquals("", response.body());
        JSONObject bodyJson = new JSONObject(response.body());
        assertNotEquals(null, bodyJson);
        assertEquals(false, bodyJson.get("success"));
        assertNull(bodyJson.get("jwt"));
    }

}
