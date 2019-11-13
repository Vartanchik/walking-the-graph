package resources

import models.Link
import models.Node

Node node

def goTo(List<Link> links) {
    [where: { weight ->
        [greaterOrEqual: { weightValue ->
            [until: { hopcount ->
                [less: { hopcountValue ->
                    links.each { l ->
                        if (node.getName() == l.getSource() && l.getWeight() >= weightValue && hopcount < hopcountValue) {
                            l.makeStep(node)
                        }
                    }
                }]
            }]
        }]
    }]
};

def goTo2(List<Link> links) {
    [where: { weight ->
        [greaterOrEqual: { weightValue ->
            links.each { l ->
                if (node.getName() == l.getSource() && l.getWeight() >= weightValue) {
                    l.makeStep(node)
                }
            }
        }]
    }]
};

def goTo3(List<Link> links) {
    [until: { hopcount ->
        [less: { hopcountValue ->
            links.each { l ->
                if (node.getName() == l.getSource() && hopcount < hopcountValue) {
                    l.makeStep(node)
                }
            }
        }]
    }]
};

def goTo4(List<Link> links) {
    links.each { l ->
        if (node.getName() == l.getSource()) {
            l.makeStep(node)
        }
    }
};

