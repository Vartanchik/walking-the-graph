package dal.link;

import com.avg.MyResource;
import models.Link;
import models.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LinkDaoImpl implements LinkDao {

    public static Logger log = LogManager.getLogger(MyResource.class);

    @Override
    public List<Link> getAll() {

        List<Link> links = new ArrayList<>();

        // Connect to database
        try (Connection connection = getConnection()) {
            ResultSet resultSet = null;

            // Select data from database and save it in list of link objects
            try {
                resultSet = connection.prepareStatement("SELECT Sid, Source, Destination, Cost FROM Links").executeQuery();
                while (resultSet.next()) {
                    links.add(new Link(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getInt(4)
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                } else {
                    log.error("Reading from database error!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return links;
    }

    @Override
    public void add(List<Link> links) {

        // Connect to database
        try (Connection connection = getConnection()) {

            // Insert data from graph to database
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement("INSERT INTO Links (Sid, Source, Destination, Cost) VALUES (?, ?, ? ,? )");
                for (Link link : links) {
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

        } catch (SQLException e) {
            e.getStackTrace();
        }
    }

    @Override
    public List<Link> getByNode(int nodeId) {

        List<Link> links = new ArrayList<>();
        String nodeName = "";
        String selectNode = "SELECT Name FROM Nodes WHERE id=?";
        String selectLinks = "SELECT Sid, Source, Destination, Cost FROM Links WHERE Source=? OR Destination=?";

        // Connect to database
        try (Connection connection = getConnection()) {

            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            // Select data from database and save it in list of link objects
            try {
                // Select node by id to get node's name
                preparedStatement = connection.prepareStatement(selectNode);
                preparedStatement.setInt(1, nodeId);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    nodeName = resultSet.getString(1);
                }

                // Select links by node's name
                preparedStatement = connection.prepareStatement(selectLinks);
                preparedStatement.setString(1, nodeName);
                preparedStatement.setString(2, nodeName);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    links.add(new Link(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getInt(4)
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                } else {
                    log.error("Reading from database error!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return links;
    }

    // Common method of getting connection to database
    private Connection getConnection() {

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
