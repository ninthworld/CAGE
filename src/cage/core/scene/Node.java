package cage.core.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public abstract class Node {

    private Vector3f m_worldPosition;
    private Vector3f m_worldRotation;
    private Vector3f m_worldScale;
    private Vector3f m_localPosition;
    private Vector3f m_localRotation;
    private Vector3f m_localScale;
    private boolean m_inheritPosition;
    private boolean m_inheritRotation;
    private boolean m_inheritScale;
    private List<Node> m_children;
    private Node m_parent;

    protected Node(Node parent) {
        m_parent = parent;
        m_worldPosition = new Vector3f();
        m_worldRotation = new Vector3f();
        m_worldScale = new Vector3f(1.0f);
        m_localPosition = new Vector3f();
        m_localRotation = new Vector3f();
        m_localScale = new Vector3f(1.0f);
        m_inheritPosition = true;
        m_inheritRotation = true;
        m_inheritScale = true;
        m_children = new ArrayList<>();
    }

    public void update() {
        if(m_inheritPosition && m_parent != null) {
            m_worldPosition.set(m_parent.getWorldPosition());
        }
        else {
            m_worldPosition.set(0.0f);
        }

        if(m_inheritRotation && m_parent != null) {
            m_worldRotation.set(m_parent.getWorldRotation());
        }
        else {
            m_worldRotation.set(0.0f);
        }

        if(m_inheritScale && m_parent != null) {
            m_worldScale.set(m_parent.getWorldScale());
        }
        else {
            m_worldScale.set(1.0f);
        }

        m_worldPosition.add(m_localPosition);
        m_worldRotation.add(m_localRotation);
        m_worldScale.mul(m_localScale);

        m_children.forEach(Node::update);
    }

    public int getNodeCount() {
        return m_children.size();
    }

    public void attachNode(Node node) {
        if(node != this) {
            if(node.getParentNode() != null) {
                node.getParentNode().m_children.remove(node);
            }
            node.m_parent = this;
            m_children.add(node);
        }
    }

    public void detachNode(Node node) {
        m_children.remove(node);
        node.m_parent = null;
    }

    public boolean containsNode(Node node) {
        return m_children.contains(node);
    }

    public Node getNode(int index) {
        return m_children.get(index);
    }

    public Iterator<Node> getNodeIterator() {
        return m_children.iterator();
    }

    public Node getParentNode() {
        return m_parent;
    }

    public void setParentNode(Node parent) {
        if(m_parent != null) {
            m_parent.detachNode(this);
        }
        if(parent != null) {
            parent.attachNode(this);
        }
    }

    public Vector3f getLocalPosition() {
        return new Vector3f(m_localPosition);
    }

    public void setLocalPosition(Vector3f position) {
        m_localPosition = position;
    }

    public void setLocalPosition(float x, float y, float z) {
        m_localPosition = new Vector3f(x, y, z);
    }

    public Vector3f getLocalRotation() {
        return new Vector3f(m_localRotation);
    }

    public void setLocalRotation(Vector3f rotation) {
        m_localRotation = rotation;
    }

    public void setLocalRotation(float pitch, float yaw, float roll) {
        m_localRotation = new Vector3f(pitch, yaw, roll);
    }

    public Vector3f getLocalScale() {
        return new Vector3f(m_localScale);
    }

    public void setLocalScale(Vector3f scale) {
        m_localScale = scale;
    }

    public void setLocalScale(float x, float y, float z) {
        m_localScale = new Vector3f(x, y, z);
    }

    public Vector3f getWorldPosition() {
        return new Vector3f(m_worldPosition).add(m_localPosition);
    }

    public Vector3f getWorldRotation() {
        return new Vector3f(m_worldRotation).add(m_localRotation);
    }

    public Vector3f getWorldScale() {
        return new Vector3f(m_worldScale).add(m_localScale);
    }

    public Matrix4f getWorldMatrix() {
        Matrix4f worldMatrix = new Matrix4f();
        worldMatrix.identity();
        worldMatrix.translate(m_worldPosition);
        worldMatrix.rotate(m_worldRotation.x, new Vector3f(1, 0, 0));
        worldMatrix.rotate(m_worldRotation.y, new Vector3f(0, 1, 0));
        worldMatrix.rotate(m_worldRotation.z, new Vector3f(0, 0, 1));
        worldMatrix.scale(m_worldScale);
        return worldMatrix;
    }

    public boolean inheritPosition() {
        return m_inheritPosition;
    }

    public void setInheritPosition(boolean inherit) {
        m_inheritPosition = inherit;
    }

    public boolean inheritRotation() {
        return m_inheritRotation;
    }

    public void setInheritRotation(boolean inherit) {
        m_inheritRotation = inherit;
    }

    public boolean inheritScale() {
        return m_inheritScale;
    }

    public void setInheritScale(boolean inherit) {
        m_inheritScale = inherit;
    }
}
