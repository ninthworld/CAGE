package biggerfish.ai;

import biggerfish.fish.FishEntity;
import cage.core.model.ExtModel;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import org.joml.*;
import org.joml.Math;

public class AIFishEntity extends FishEntity {

    private Vector2f headingTime;
    private Vector2f headingDelta;

    public AIFishEntity(SceneManager sceneManager, Node parent, ExtModel model, int type, Vector3fc position, float weight, DiscreteDynamicsWorld dynamicsWorld) {
        super(sceneManager, parent, model, type, position, weight, dynamicsWorld);
        this.headingTime = new Vector2f();
        this.headingDelta = new Vector2f();
    }

    private static final float minPitch = Angle.fromDegrees(-45.0f);
    private static final float maxPitch = Angle.fromDegrees(45.0f);
    public void updateDelta(float deltaTime) {
        headingTime.x = Math.max(0.0f, headingTime.x - deltaTime);
        headingTime.y = Math.max(0.0f, headingTime.y - deltaTime);

        moveForward(deltaTime);
        if(headingTime.x > 0.0f) {
            rotate(headingDelta.x * deltaTime, getLocalRotation().invert(new Matrix3f()).getColumn(1, new Vector3f()));
        }

        if(headingTime.y > 0.0f) {
            float pitch = headingDelta.y * deltaTime;
            float pitchAngle = getLocalForward().dot(Direction.UP);
            if((pitch < 0.0f && pitchAngle < maxPitch / Math.PI * 2.0f) || (pitch > 0.0f && pitchAngle > minPitch / Math.PI * 2.0f)) {
                rotate(pitch, Direction.RIGHT);
            }
        }
    }

    public void setHeading(Vector2fc headingTime, Vector2fc headingDelta) {
        this.headingTime.set(headingTime);
        this.headingDelta.set(headingDelta);
    }
}