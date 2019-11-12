package resources

import models.Link
import models.Node

int weight
Node node

def goTo(List<Link> links) {
    [where: { weightCondition ->
        [until: { hopcountCondition ->
            links.each { l ->
                if (node.getName() == l.getSource() && l.getWeight() >= weight && hopcountCondition) {
                    l.makeStep(node)
                }
            }
        }]
    }]
};

def goTo2(List<Link> links) {
    [where: { weightCondition ->
        links.each { l ->
            if (node.getName() == l.getSource() && l.getWeight() >= weight) {
                l.makeStep(node)
            }
        }
    }]
};

def goTo3(List<Link> links) {
    [until: { hopcountCondition ->
        links.each { l ->
            if (node.getName() == l.getSource() && hopcountCondition) {
                l.makeStep(node)
            }
        }
    }]
};

def goTo4(List<Link> links) {
    links.each { l ->
        if (node.getName() == l.getSource()) {
            l.makeStep(node)
        }
    }
};

