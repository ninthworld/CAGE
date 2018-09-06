package biggerfish;

import cage.core.input.action.InputAction;
import cage.core.input.component.Axis;
import cage.core.scene.Node;
import cage.core.scene.camera.Camera;
import cage.core.scene.controller.NodeController;
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import cage.core.window.Window;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class TPCameraController extends NodeController {

    private Camera camera;
    private Window window;
    private float radius;
    private float azimuth;
    private float elevation;
    private float maxRadius;
    private float minElevation;
    private float maxElevation;
    private float minPitch;
    private float maxPitch;
    private boolean look;
    private boolean updated;
    private float forward;
    private float right;
    private float up;
    private float yaw;
    private float pitch;

    public TPCameraController(Camera camera, Window window) {
        this.camera = camera;
        this.window = window;
        this.radius = 1.0f;
        this.azimuth = 0.0f;
        this.elevation = 0.0f;
        this.maxRadius = 32.0f;
        this.minElevation = Angle.fromDegrees(-89.0f);
        this.maxElevation = Angle.fromDegrees(89.0f);
        this.minPitch = Angle.fromDegrees(-45.0f);
        this.maxPitch = Angle.fromDegrees(45.0f);
        this.look = true;
        this.updated = false;
        this.forward = 0.0f;
        this.right = 0.0f;
        this.up = 0.0f;
        this.yaw = 0.0f;
        this.pitch = 0.0f;

        if(!containsNode(camera)) {
            addNode(camera);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updated = false;
        forward = 0.0f;
        right = 0.0f;
        up = 0.0f;
        yaw = 0.0f;
        pitch = 0.0f;
    }

    @Override
    public void updateNode(float deltaTime, Node node) {
        if(updated) {
            node.moveForward(forward);
            node.moveRight(right);
            node.translate(Direction.UP.mul(up, new Vector3f()));
            node.rotate(yaw, node.getLocalRotation().invert(new Matrix3f()).getColumn(1, new Vector3f()));
            float pitchAngle = node.getLocalForward().dot(Direction.UP);
            if((pitch < 0.0f && pitchAngle < maxPitch / Math.PI * 2.0f) || (pitch > 0.0f && pitchAngle > minPitch / Math.PI * 2.0f)) {
                node.rotate(pitch, Direction.RIGHT);
            }
        }
        
        if (node == camera) {
            float x = radius * (float) (Math.cos(elevation) * Math.sin(azimuth));
            float y = radius * (float) Math.sin(elevation);
            float z = radius * (float) (Math.cos(elevation) * Math.cos(azimuth));
            camera.setLocalPosition(x, y, z);
            camera.update(true);
            
            Vector3f along = camera.getParentNode().getWorldPosition().sub(camera.getWorldPosition(), new Vector3f());
            if(along.length() > 0.0f) {
            	camera.lookAlong(along);
            }
        }
    }

    public InputAction createAzimuthAction(float speed) {
        return (deltaTime, event) -> { if(look) { setAzimuth(azimuth + speed * deltaTime * event.getValue()); } };
    }

    public InputAction createElevationAction(float speed) {
        return (deltaTime, event) -> { if(look) { setElevation(elevation + speed * deltaTime * event.getValue()); } };
    }

    public InputAction createRadiusAction(float speed) {
        return (deltaTime, event) -> setRadius(radius + speed * deltaTime * event.getValue());
    }

    public InputAction createToggleLookAction() {
        return (deltaTime, event) -> setLook(!look);
    }

    public InputAction createForwardAction(float speed) {
        return (deltaTime, event) -> setForward(forward + speed * deltaTime * event.getValue());
    }

    public InputAction createRightAction(float speed) {
        return (deltaTime, event) -> setRight(right + speed * deltaTime * event.getValue());
    }

    public InputAction createUpAction(float speed) {
        return (deltaTime, event) -> setUp(up + speed * deltaTime * event.getValue());
    }

    public InputAction createYawAction(float speed) {
        return (deltaTime, event) -> setYaw(yaw + speed * deltaTime * event.getValue());
    }

    public InputAction createPitchAction(float speed) {
        return (deltaTime, event) -> setPitch(pitch + speed * deltaTime * event.getValue());
    }

    public boolean isLook() {
        return look;
    }

    public void setLook(boolean look) {
        this.look = look;
        this.window.setMouseVisible(!look);
        notifyUpdate();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        notifyUpdate();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        if (radius > 0.0f && radius < maxRadius) {
            this.radius = radius;
            notifyUpdate();
        }
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
        notifyUpdate();
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        if (elevation > minElevation && elevation < maxElevation) {
            this.elevation = elevation;
            notifyUpdate();
        }
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
    }

    public float getMinElevation() {
        return minElevation;
    }

    public void setMinElevation(float minElevation) {
        this.minElevation = minElevation;
    }

    public float getMaxElevation() {
        return maxElevation;
    }

    public void setMaxElevation(float maxElevation) {
        this.maxElevation = maxElevation;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
        notifyUpdate();
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
        notifyUpdate();
    }

    public float getUp() {
        return up;
    }

    public void setUp(float up) {
        this.up = up;
        notifyUpdate();
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        notifyUpdate();
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        notifyUpdate();
    }

    public void notifyUpdate() {
        this.updated = true;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public float getMinPitch() {
        return minPitch;
    }

    public void setMinPitch(float minPitch) {
        this.minPitch = minPitch;
    }

    public float getMaxPitch() {
        return maxPitch;
    }

    public void setMaxPitch(float maxPitch) {
        this.maxPitch = maxPitch;
    }
}
