package dal.node;

import annotations.InitApp;
import com.avg.MyResource;
import models.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NodeDaoImpl implements NodeDao {

    public static Logger log = LogManager.getLogger(MyResource.class);

    @Override
    public List<Node> getAll() {

        List<Node> nodes = new ArrayList<>();

        // Connect to database
        try (Connection connection = getConnection()) {
            ResultSet resultSet = null;

            // Select data from database and save it in list of node objects
            try {
                resultSet = connection.prepareStatement("SELECT Name FROM Nodes").executeQuery();
                while (resultSet.next()) {
                    nodes.add(new Node(resultSet.getString(1)));
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
        return nodes;
    }

    @Override
    public void add(List<Node> nodes) {

        // Connect to database
        try (Connection connection = getConnection()) {

            // Insert data from graph to database
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement("INSERT INTO Nodes (Name) VALUES (?)");
                for (Node node : nodes) {
                    preparedStatement.setString(1, node.getName());
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

    @InitApp
    private void createNodeTable() {

        String createTable = "CREATE TABLE Nodes(" +
                "Id MEDIUMINT NOT NULL AUTO_INCREMENT," +
                "Name VARCHAR(30) NOT NULL PRIMARY KEY);";

        // Connect to database
        try (Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(createTable);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.info("Node table already exist in database!");
        }
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
