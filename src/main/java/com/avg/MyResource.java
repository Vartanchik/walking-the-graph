package com.avg;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.Graph;
import models.Link;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Node;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public String getGraph(@QueryParam("startNode") String startNode, @QueryParam("depth") int depth) {

        Graph graph = null;
        List<String> log = new ArrayList<>();
        String currentNodeName = startNode;
        int steps = depth;
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src\\main\\java\\resources\\graph.json");

        // Read object from json file for some manipulations with it
        try {
            graph = objectMapper.readValue(file, Graph.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get list of graph's links
        List<Link> links = graph.getLinks();

        // Make steps by links
        while (steps > 0) {
            for (Link link : links) {
                if (link.getSource().equals(currentNodeName)) {
                    try {
                        Thread.sleep(link.getCost() * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    currentNodeName = link.getDestination();
                    log.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                    steps--;
                    break;
                }
            }
        }

        return log.toString();
    }
}