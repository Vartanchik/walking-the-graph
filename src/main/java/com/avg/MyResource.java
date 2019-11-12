package com.avg;

import com.fasterxml.jackson.core.JsonProcessingException;
import dal.link.LinkDao;
import dal.link.LinkDaoImpl;
import dal.node.NodeDao;
import dal.node.NodeDaoImpl;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import models.Graph;
import models.Link;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Node;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.codehaus.groovy.control.CompilerConfiguration;
import resources.GroovyScript;
import rx.Observable;

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

        return listToString(links);
    }

    @GET
    @Path("/tables/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNodes() {

        List<Node> nodes = nodeDao.getAll();

        return listToString(nodes);
    }

    @GET
    @Path("/tables/nodes/out")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLinksByNode(@QueryParam("id") int id) {

        List<Link> links = linkDao.getByNode(id);

        return listToString(links);
    }

    private String listToString(List list){
        // Convert list of objects to string and returning it as json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class GraphAsync implements Runnable {

    private String currentNodeName;
    private int steps;
    private List<Link> links;
    private List<Node> nodes;
    private Binding binding;
    private GroovyShell shell;

    public GraphAsync(String startNode, int depth){

        currentNodeName = startNode;
        steps = depth;
        new Thread(this).start();
    }

    public Observable<Node> move(List<Node> nodes){
        return Observable.just(nodes)
                         .map(n -> findNodeByName(n, currentNodeName))
                         .doOnNext(n -> {
                             binding.setProperty("node", n);
                             binding.setProperty("weight", 105); // here I don't know how to pass this value to groovy script
                             shell.evaluate("goTo links where weight >= 105 until hopcount < 10");
                             //shell.evaluate("goTo2 links where weight >= 105");
                             //shell.evaluate("goTo3 links until hopcount < 10");
                             //shell.evaluate("goTo4 links");
                         })
                         .doOnNext(n -> currentNodeName = findLinkByNode(links, n).getDestination())
                         .repeat(steps);
    }

    private Link findLinkByNode(List<Link> links, Node node){
        return links.parallelStream()
                    .filter(link -> link.getSource().equals(node.getName()))
                    .collect(Collectors.toList())
                    .get(0);
    }

    private Node findNodeByName(List<Node> nodes, String nodeName){
        return nodes.parallelStream()
                    .filter(node -> node.getName().equals(nodeName))
                    .collect(Collectors.toList())
                    .get(0);
    }

    public void run() {

        // Get list of graph's links
        links = MyResource.linkDao.getAll();

        // Set weight field by random value
        links.parallelStream().map(link -> { link.setWeight(new Random().nextInt(110-95+1) + 95); return link; })
                              .collect(Collectors.toList());

        // Get list of graph's nodes
        nodes = MyResource.nodeDao.getAll();

        // Setup connection with groovy script
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(GroovyScript.class.getName());
        binding = new Binding();
        binding.setVariable("links", links);
        binding.setVariable("hopcount", steps);
        shell = new GroovyShell(binding, config);

        // Make steps by links (RxJava)
        move(nodes).subscribe();
    }
}