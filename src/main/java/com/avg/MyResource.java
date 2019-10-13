package com.avg;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.Graph;
import models.Link;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Node;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    public static Logger log = LogManager.getLogger(MyResource.class);

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
    public void getGraph(@QueryParam("startNode") String startNode, @QueryParam("depth") int depth) {

        new GraphAsync(startNode, depth);
    }

    @GET
    @Path("/tables/fill")
    public void fillTables(){

        // Connect to database
        try(Connection connection = getConnection()){

            // Convert json file to graph object
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File("src\\main\\java\\resources\\graph.json");
            Graph graph = null;
            try {
                graph = objectMapper.readValue(file, Graph.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Insert data from graph to database
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement("INSERT INTO Nodes (Name) VALUES (?)");
                for (Node node: graph.getNodes()) {
                    preparedStatement.setString(1, node.getName());
                    preparedStatement.execute();
                }

                preparedStatement = connection.prepareStatement("INSERT INTO Links (Sid, Source, Destination, Cost) VALUES (?, ?, ? ,? )");
                for (Link link: graph.getLinks()) {
                    preparedStatement.setString(1, link.getSid());
                    preparedStatement.setString(2, link.getSource());
                    preparedStatement.setString(3, link.getDestination());
                    preparedStatement.setInt(4, link.getCost());
                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                preparedStatement.close();
            }

        } catch (SQLException e){
            e.getStackTrace();
        }
    }

    @GET
    @Path("/tables/links")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLinks(){

        List<Link> links = new ArrayList<>();

        // Connect to database
        try(Connection connection = getConnection()) {
            ResultSet resultSet = null;

            // Select data from database and save it in list of link objects
            try{
                resultSet = connection.prepareStatement("SELECT * FROM Links").executeQuery();
                while (resultSet.next()){
                    links.add(new Link(
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getInt(5)
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(resultSet != null){
                    resultSet.close();
                } else {
                    log.error("Reading from database error!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

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
    public String getNodes(){

        List<Node> nodes = new ArrayList<>();

        // Connect to database
        try(Connection connection = getConnection()) {
            ResultSet resultSet = null;

            // Select data from database and save it in list of node objects
            try{
                resultSet = connection.prepareStatement("SELECT * FROM Nodes").executeQuery();
                while (resultSet.next()){
                    nodes.add(new Node(resultSet.getString(2)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(resultSet != null){
                    resultSet.close();
                } else {
                    log.error("Reading from database error!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert list of link objects to string and returning it as json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(nodes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Common method of getting connection to database
    private Connection getConnection(){

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:h2:~/wtgDb";
        String user = "root";
        String password = "123";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class GraphAsync implements Runnable{

    private Graph graph = null;
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

                    MyResource.log.error(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                    steps--;
                    break;
                }
            }
        }
    }
}