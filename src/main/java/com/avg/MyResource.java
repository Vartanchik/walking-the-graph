package com.avg;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.Graph;
import models.Link;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Node;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String  getIt() {
        return "Got it!";
    }

    @GET
    @Path("/graph")
    @Produces(MediaType.TEXT_PLAIN)
    public String getGraph() {

        Graph graph = null;
        String response = null;
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src\\main\\java\\resources\\graph.json");

        // Read object from json file for some manipulations with it
        try {
            graph = objectMapper.readValue(file, Graph.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Represent object as string for response
        try {
            response = objectMapper.writeValueAsString(graph);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return response;
    }
}