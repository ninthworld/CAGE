package cage.core.scene.controller;

import cage.core.scene.Node;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class RotationController extends NodeController {

    private Vector3fc axis;
    private float speed;

    public RotationController(float speed, Vector3fc axis) {
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

    public Vector3fc getAxis() {
        return axis;
    }

    public void setAxis(Vector3f axis) {
        this.axis = axis;
    }
}
