package resources

import models.Link
import models.Node
import rx.Observable

import java.util.stream.Collectors

def currentNodeName
def steps
def links
def nodes
def weight

def goTo(List<Link> links) {
    [where: { w ->
        [until: { h ->
            move(nodes).subscribe({ n ->
                def link = findLinkByNode(links, n)
                if (link.getWeight() >= weight && h) {
                    link.makeStep(n)
                }
                currentNodeName = findLinkByNode(links, n).getDestination()
            })
        }]
    }]
};


def goTo2(List<Link> links) {
    [where: { w ->
        move(nodes).subscribe({ n ->
            def link = findLinkByNode(links, n)
            if (link.getWeight() >= weight) {
                link.makeStep(n)
            }
            currentNodeName = findLinkByNode(links, n).getDestination()
        })
    }]
};

def goTo3(List<Link> links) {
    [until: { h ->
        move(nodes).subscribe({ n ->
            def link = findLinkByNode(links, n)
            if (h) {
                link.makeStep(n)
            }
            currentNodeName = findLinkByNode(links, n).getDestination()
        })
    }]
};

def goTo4(List<Link> links) {
    move(nodes).subscribe({ n ->
        def link = findLinkByNode(links, n)
        link.makeStep(n)
        currentNodeName = findLinkByNode(links, n).getDestination()
    })
};

Observable<Node> move(List<Node> nodes) {
    return Observable.just(nodes)
            .map({ n -> findNodeByName(n, currentNodeName) })
            .repeat(steps);
}

Link findLinkByNode(List<Link> links, Node node) {
    return links.parallelStream()
            .filter({ link -> link.getSource().equals(node.getName()) })
            .collect(Collectors.toList())
            .get(0);
}

Node findNodeByName(List<Node> nodes, String nodeName) {
    return nodes.parallelStream()
            .filter({ node -> node.getName().equals(nodeName) })
            .collect(Collectors.toList())
            .get(0);
}
