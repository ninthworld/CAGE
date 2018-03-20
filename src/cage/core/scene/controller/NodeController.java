package cage.core.scene.controller;

import cage.core.scene.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class NodeController {

    private List<Node> nodes;
    private boolean enabled;

    public NodeController() {
        this.nodes = new ArrayList<>();
        this.enabled = true;
    }

    public abstract void updateNode(float deltaTime, Node node);

    public void update(float deltaTime) {
        if(enabled) {
            nodes.forEach((Node node) -> updateNode(deltaTime, node));
        }
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public void attachNode(Node node) {
        nodes.add(node);
    }

    public void detachNode(Node node) {
        nodes.remove(node);
    }

    public void detachAllNodes() {
        nodes.forEach(this::detachNode);
    }

    public boolean containsNode(Node node) {
        return nodes.contains(node);
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    public Iterator<Node> getNodeIterator() {
        return nodes.iterator();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
