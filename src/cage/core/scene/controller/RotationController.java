package cage.core.scene.controller;

import cage.core.scene.Node;
import org.joml.Vector3f;

public class RotationController extends NodeController {

    private Vector3f axis;
    private float speed;

    public RotationController(float speed, Vector3f axis) {
        this.speed = speed;
        this.axis = axis;
    }

    @Override
    public void updateNode(float deltaTime, Node node) {
        node.rotate(deltaTime * speed, axis);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Vector3f getAxis() {
        return axis;
    }

    public void setAxis(Vector3f axis) {
        this.axis = axis;
    }
}
