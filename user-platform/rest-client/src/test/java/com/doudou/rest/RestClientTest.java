package com.doudou.rest;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public class RestClientTest {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Test
    public void testGetMethod() {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response
        String content = response.readEntity(String.class);
        logger.info("get method: " + content);
    }

    @Test
    public void testPostMethod() {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hi")      // WebTarget
                .request() // Invocation.Builder
                .post(Entity.json("name=admin"));                                     //  Response
        String content = response.readEntity(String.class);
        logger.info("post method: " + content);
    }
}
