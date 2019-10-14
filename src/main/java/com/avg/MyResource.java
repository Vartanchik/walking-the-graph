package com.avg;

import com.fasterxml.jackson.core.JsonProcessingException;
import dal.link.LinkDao;
import dal.link.LinkDaoImpl;
import dal.node.NodeDao;
import dal.node.NodeDaoImpl;
import models.Graph;
import models.Link;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Node;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    public static Logger log = LogManager.getLogger(MyResource.class);
    public static LinkDao linkDao = new LinkDaoImpl();
    public static NodeDao nodeDao = new NodeDaoImpl();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @GET
    @Path("/graph")
    public void getGraph(@QueryParam("startNode") String startNode, @QueryParam("depth") int depth) {

        new GraphAsync(startNode, depth);
    }

    @POST
    @Path("/tables/fill")
    @Consumes(MediaType.APPLICATION_JSON)
    public void fillTables(Graph graph) {

        nodeDao.add(graph.getNodes());

        linkDao.add(graph.getLinks());
    }

    @GET
    @Path("/tables/links")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLinks() {

        List<Link> links = linkDao.getAll();

        // Convert list of link objects to string and returning it as json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(links);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/tables/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNodes() {

        List<Node> nodes = nodeDao.getAll();

        // Convert list of link objects to string and returning it as json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(nodes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/tables/nodes/out")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLinksByNode(@QueryParam("id") int id) {

        List<Link> links = linkDao.getByNode(id);

        // Convert list of link objects to string and returning it as json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(links);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class GraphAsync implements Runnable {

    private String currentNodeName;
    private int steps;

    public GraphAsync(String startNode, int depth) {
        currentNodeName = startNode;
        steps = depth;
        new Thread(this).start();
    }

    public void run() {

        // Get list of graph's links
        List<Link> links = MyResource.linkDao.getAll();

        // Make steps by links
        while (steps > 0) {
            links.parallelStream().forEach(link -> {
                if (link.getSource().equals(currentNodeName)) {
                    try {
                        Thread.sleep(link.getCost() * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    currentNodeName = link.getDestination();

                    MyResource.log.error(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                    steps--;
                }
            });
        }
    }
}