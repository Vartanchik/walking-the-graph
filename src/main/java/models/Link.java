package models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Link {
    private String sid;
    private String source;
    private String destination;
    private int weight;
    private int cost;

    public static Logger log = LogManager.getLogger(Link.class);

    public Link(String sid, String source, String destination, int cost) {
        this.sid = sid;
        this.source = source;
        this.destination = destination;
        this.cost = cost;
    }

    public Link() {
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void makeStep(Node node) {
        if(!node.getName().equals(source)){
            return;
        }

        try {
            Thread.sleep(cost * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

}
