package cage.core.scene;

import cage.core.common.Destroyable;
import org.joml.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Node implements Destroyable {

    public static final Vector3fc FORWARD = new Vector3f(0.0f, 0.0f, 1.0f);
    public static final Vector3fc RIGHT = new Vector3f(-1.0f, 0.0f, 0.0f);
    public static final Vector3fc UP = new Vector3f(0.0f, 1.0f, 0.0f);

    private Vector3f worldPosition;
    private Vector3f worldScale;
    private Matrix3f worldRotation;
    private Matrix4f worldTransform;

    private Vector3f localPosition;
    private Vector3f localScale;
    private Matrix3f localRotation;
    private Matrix4f localTransform;

    private List<Node> children;
    private Node parent;
    private boolean localUpdated;
    private boolean enabled;

    protected Node(Node parent) {
        this.worldPosition = new Vector3f();
        this.worldScale = new Vector3f(1.0f);
        this.worldRotation = new Matrix3f().identity();
        this.worldTransform = new Matrix4f().identity();

        this.localPosition = new Vector3f();
        this.localScale = new Vector3f(1.0f);
        this.localRotation = new Matrix3f().identity();
        this.localTransform = new Matrix4f().identity();

        this.children = new ArrayList<>();
        this.localUpdated = true;
        this.enabled = true;
        this.parent = parent;
        if(this.parent != null) {
            this.parent.addNode(this);
        }
    }

    public void update(boolean forced) {
        boolean shouldUpdate = (forced || localUpdated);
        if(shouldUpdate) {
            updateNode();
            localUpdated = false;
        }

        children.forEach((Node node) -> node.update(shouldUpdate));
    }

    protected void updateNode() {
        // Update local transform
        localTransform.identity();
        localTransform.translate(localPosition);
        localTransform.mul(new Matrix4f().identity().set(localRotation));
        localTransform.scale(localScale);

        // Update world transform
        if(parent != null) {
            worldPosition.set(parent.getWorldPosition());
            worldRotation.set(parent.getWorldRotation());
            worldScale.set(parent.getWorldScale());
            worldTransform.set(parent.getWorldTransform());
        }
        else {
            worldTransform.identity();
            worldPosition.set(new Vector3f());
            worldRotation.identity();
            worldScale.set(new Vector3f(1.0f));
        }
        worldPosition.add(localPosition);
        worldRotation.mul(localRotation);
        worldScale.mul(localScale);
        worldTransform.mul(localTransform);
    }

    public Node getParentNode() {
        return parent;
    }

    public void setParentNode(Node parent) {
        if(this.parent != null) {
            this.parent.removeNode(this);
        }
        if(parent != null) {
            parent.addNode(this);
        }
    }

    public Vector3fc getLocalPosition() {
        return localPosition;
    }

    public void setLocalPosition(Vector3f position) {
        localPosition.set(position);
        notifyUpdate();
    }

    public void setLocalPosition(float x, float y, float z) {
        setLocalPosition(new Vector3f(x, y, z));
    }

    public Matrix3f getLocalRotation() {
        return new Matrix3f(localRotation);
    }

    public void setLocalRotation(Matrix3f rotation) {
        localRotation = rotation;
        notifyUpdate();
    }

    public Vector3fc getLocalScale() {
        return localScale;
    }

    public void setLocalScale(Vector3f scale) {
        localScale.set(scale);
        notifyUpdate();
    }

    public void setLocalScale(float x, float y, float z) {
        setLocalScale(new Vector3f(x, y, z));
    }

    public Matrix4fc getLocalTransform() {
        return localTransform;
    }

    public Vector3fc getLocalRight() {
        return localRotation.getRow(0, new Vector3f());
    }

    public Vector3fc getLocalForward() {
        return localRotation.getRow(2, new Vector3f()).mul(-1.0f);
    }

    public Vector3fc getLocalUp() {
        return localRotation.getRow(1, new Vector3f());
    }

    public Vector3fc getWorldPosition() {
        return worldPosition;
    }

    public Matrix3fc getWorldRotation() {
        return worldRotation;
    }

    public Vector3fc getWorldScale() {
        return worldScale;
    }

    public Matrix4fc getWorldTransform() {
        return worldTransform;
    }

    public Vector3fc getWorldRight() {
        return worldRotation.getRow(0, new Vector3f());
    }

    public Vector3fc getWorldForward() {
        return worldRotation.getRow(1, new Vector3f());
    }

    public Vector3fc getWorldUp() {
        return worldRotation.getRow(2, new Vector3f());
    }

    public void translate(Vector3fc offset) {
        localPosition.add(offset);
        notifyUpdate();
    }

    public void translate(float x, float y, float z) {
        translate(new Vector3f(x, y, z));
    }

    public void moveForward(float amount) {
        translate(getLocalForward().mul(amount, new Vector3f()));
    }

    public void moveBackward(float amount) {
        translate(getLocalForward().mul(-amount, new Vector3f()));
    }

    public void moveRight(float amount) {
        translate(getLocalRight().mul(amount, new Vector3f()));
    }

    public void moveLeft(float amount) {
        translate(getLocalRight().mul(-amount, new Vector3f()));
    }

    public void moveUp(float amount) {
        translate(getLocalUp().mul(amount, new Vector3f()));
    }

    public void moveDown(float amount) {
        translate(getLocalUp().mul(-amount, new Vector3f()));
    }

    public void rotate(float angle, Vector3fc axis) {
        localRotation.rotate(angle, axis);
        notifyUpdate();
    }

    public void scale(Vector3f mul) {
        localScale.mul(mul);
        notifyUpdate();
    }

    public void scale(float x, float y, float z) {
        scale(new Vector3f(x, y, z));
    }

    public void scale(float mul) {
        scale(new Vector3f(mul));
    }

    public void pitch(float angle) {
        rotate(angle, RIGHT);
    }

    public void yaw(float angle) {
        rotate(angle, UP);
    }

    public void roll(float angle) {
        rotate(angle, FORWARD);
    }

    public void lookAt(Vector3fc target, Vector3fc up) {
        localRotation.lookAlong(target.sub(localPosition, new Vector3f()), up);
        notifyUpdate();
    }

    public void lookAt(Vector3f target) {
        lookAt(target, UP);
    }

    public void lookAt(float x, float y, float z) {
        lookAt(new Vector3f(x, y, z), UP);
    }

    public void notifyUpdate() {
        localUpdated = true;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addNode(Node node) {
        if(node != this) {
            if(node.parent != null) {
                node.parent.removeNode(this);
            }
            node.parent = this;
            children.add(node);
        }
    }

    public void removeNode(Node node) {
        children.remove(node);
        node.parent = null;
    }

    public void removeNode(int index) {
        children.remove(index).parent = null;
    }

    public void removeAllNodes() {
        children.forEach(this::removeNode);
    }

    public int getNodeCount() {
        return children.size();
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
}
