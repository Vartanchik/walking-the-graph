package com.avg;

import dal.link.LinkDao;
import dal.link.LinkDaoImpl;
import dal.node.NodeDao;
import dal.node.NodeDaoImpl;
import models.Graph;
import models.Link;
import models.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import java.util.List;

@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    public static Logger log = LogManager.getLogger(MyResource.class);
    @Autowired
    public static LinkDao linkDao = new LinkDaoImpl();
    @Autowired
    public static NodeDao nodeDao = new NodeDaoImpl();

    @GetMapping("/go")
    public void go(@QueryParam("startNode") String startNode, @QueryParam("depth") int depth) {
        new GraphAsync(startNode, depth);
    }

    @PostMapping("/tables/fill")
    public void fillTables(@RequestBody Graph graph) {

        nodeDao.add(graph.getNodes());

        linkDao.add(graph.getLinks());
    }

    @GetMapping("/tables/links")
    public List<Link> getLinks() {

        return linkDao.getAll();
    }

    @GetMapping("/tables/nodes")
    public List<Node> getNodes() {

        return nodeDao.getAll();
    }

    @GetMapping("/tables/nodes/out")
    public List<Link> getLinksByNode(@QueryParam("id") int id) {

        return linkDao.getByNode(id);
    }
}