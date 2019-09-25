package com.avg;

import models.Graph;
import models.Link;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public void getGraph(@QueryParam("startNode") String startNode, @QueryParam("depth") int depth) {

        new GraphAsync(startNode, depth);
    }
}

class GraphAsync implements Runnable{

    private Graph graph = null;
    private List<String> log = new ArrayList<>();
    private String currentNodeName;
    private int steps;
    private ObjectMapper objectMapper = new ObjectMapper();
    private File file = new File("src\\main\\java\\resources\\graph.json");

    public GraphAsync(String startNode, int depth){
        currentNodeName = startNode;
        steps = depth;
        new Thread(this).start();
    }

    public void run(){

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
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                    steps--;
                    break;
                }
            }
        }
    }
}