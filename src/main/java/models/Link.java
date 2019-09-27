package models;

public class Link {
    private String sid;
    private String source;
    private String destination;
    private int cost;

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
}
