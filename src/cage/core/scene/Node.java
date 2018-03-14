package cage.core.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public abstract class Node {

    private Vector3f worldPosition;
    private Vector3f worldRotation;
    private Vector3f worldScale;
    private Vector3f localPosition;
    private Vector3f localRotation;
    private Vector3f localScale;
    private boolean inheritPosition;
    private boolean inheritRotation;
    private boolean inheritScale;
    private List<Node> children;
    private Node parent;

    protected Node(Node parent) {
        this.parent = parent;
        this.worldPosition = new Vector3f();
        this.worldRotation = new Vector3f();
        this.worldScale = new Vector3f(1.0f);
        this.localPosition = new Vector3f();
        this.localRotation = new Vector3f();
        this.localScale = new Vector3f(1.0f);
        this.inheritPosition = true;
        this.inheritRotation = true;
        this.inheritScale = true;
        this.children = new ArrayList<>();
    }

    public void update() {
        if(inheritPosition && parent != null) {
            worldPosition.set(parent.getWorldPosition());
        }
        else {
            worldPosition.set(0.0f);
        }

        if(inheritRotation && parent != null) {
            worldRotation.set(parent.getWorldRotation());
        }
        else {
            worldRotation.set(0.0f);
        }

        if(inheritScale && parent != null) {
            worldScale.set(parent.getWorldScale());
        }
        else {
            worldScale.set(1.0f);
        }

        worldPosition.add(localPosition);
        worldRotation.add(localRotation);
        worldScale.mul(localScale);

        children.forEach(Node::update);
    }

    public int getNodeCount() {
        return children.size();
    }

    public void attachNode(Node node) {
        if(node != this) {
            if(node.getParentNode() != null) {
                node.getParentNode().children.remove(node);
            }
            node.parent = this;
            children.add(node);
        }
    }

    public void detachNode(Node node) {
        children.remove(node);
        node.parent = null;
    }

    public boolean containsNode(Node node) {
        return children.contains(node);
    }

    public Node getNode(int index) {
        return children.get(index);
    }

    public Iterator<Node> getNodeIterator() {
        return children.iterator();
    }

    public Node getParentNode() {
        return parent;
    }

    public void setParentNode(Node parent) {
        if(parent != null) {
            parent.detachNode(this);
        }
        if(parent != null) {
            parent.attachNode(this);
        }
    }

    public Vector3f getLocalPosition() {
        return new Vector3f(localPosition);
    }

    public void setLocalPosition(Vector3f position) {
        localPosition = position;
    }

    public void setLocalPosition(float x, float y, float z) {
        localPosition = new Vector3f(x, y, z);
    }

    public Vector3f getLocalRotation() {
        return new Vector3f(localRotation);
    }

    public void setLocalRotation(Vector3f rotation) {
        localRotation = rotation;
    }

    public void setLocalRotation(float pitch, float yaw, float roll) {
        localRotation = new Vector3f(pitch, yaw, roll);
    }

    public Vector3f getLocalScale() {
        return new Vector3f(localScale);
    }

    public void setLocalScale(Vector3f scale) {
        localScale = scale;
    }

    public void setLocalScale(float x, float y, float z) {
        localScale = new Vector3f(x, y, z);
    }

    public Vector3f getWorldPosition() {
        return new Vector3f(worldPosition).add(localPosition);
    }

    public Vector3f getWorldRotation() {
        return new Vector3f(worldRotation).add(localRotation);
    }

    public Vector3f getWorldScale() {
        return new Vector3f(worldScale).add(localScale);
    }

    public Matrix4f getWorldMatrix() {
        Matrix4f worldMatrix = new Matrix4f();
        worldMatrix.identity();
        worldMatrix.translate(worldPosition);
        worldMatrix.rotate(worldRotation.x, new Vector3f(1, 0, 0));
        worldMatrix.rotate(worldRotation.y, new Vector3f(0, 1, 0));
        worldMatrix.rotate(worldRotation.z, new Vector3f(0, 0, 1));
        worldMatrix.scale(worldScale);
        return worldMatrix;
    }

    public boolean inheritPosition() {
        return inheritPosition;
    }

    public void setInheritPosition(boolean inherit) {
        inheritPosition = inherit;
    }

    public boolean inheritRotation() {
        return inheritRotation;
    }

    public void setInheritRotation(boolean inherit) {
        inheritRotation = inherit;
    }

    public boolean inheritScale() {
        return inheritScale;
    }

    public void setInheritScale(boolean inherit) {
        inheritScale = inherit;
    }
}
