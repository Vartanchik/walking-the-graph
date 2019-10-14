package dal.link;

import models.Link;

import java.util.List;

public interface LinkDao {
    List<Link> getAll();
    void add(List<Link> links);
    List<Link> getByNode(int nodeId);

}
