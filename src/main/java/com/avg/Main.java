package com.avg;

import annotations.InitApp;
import dal.link.LinkDaoImpl;
import dal.node.NodeDaoImpl;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.avg package
        final ResourceConfig rc = new ResourceConfig().packages("com.avg");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {

        // Adding node table to DB
        Class<?> nodeDaoClass = Class.forName("dal.node.NodeDaoImpl");
        for (Method method : nodeDaoClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(InitApp.class)){
                method.setAccessible(true);
                method.invoke(new NodeDaoImpl());
            }
        }
        // Adding link table to DB
        Class<?> linkDaoClass = Class.forName("dal.link.LinkDaoImpl");
        for (Method method : linkDaoClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(InitApp.class)){
                method.setAccessible(true);
                method.invoke(new LinkDaoImpl());
            }
        }

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

