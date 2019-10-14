package dal.node;

import models.Node;

import java.util.List;

public interface NodeDao {
    List<Node> getAll();
    void add(List<Node> nodes);
}
