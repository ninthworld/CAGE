package biggerfish.fish;

import biggerfish.physics.PhysicsEntity;
import cage.core.model.ExtModel;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import org.joml.Vector3fc;

public class FishEntity extends PhysicsEntity {

    private float scale;
    private float mass;
    private float efficiency;
    private int fishType;

    public FishEntity(SceneManager sceneManager, Node parent, ExtModel model, int type, Vector3fc position, float mass, DiscreteDynamicsWorld dynamicsWorld) {
        super(sceneManager, parent, model, position, new BoxShape(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f)), dynamicsWorld);
        setMass(mass);
        this.efficiency = 0.3f;
        this.fishType = type;
    }

    @Override
    public void updateNode() {
        if((getExtModel().isPaused() || getExtModel().isFinishAndPause()) && isMoving()) {
            getExtModel().start();
        }
        else if((!getExtModel().isPaused() || !getExtModel().isFinishAndPause()) && !isMoving()) {
            getExtModel().finishAndPause();
        }

        super.updateNode();
    }

    public ExtModel getExtModel() {
        return (ExtModel) getModel();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.mass = getMassFromScale(scale);
        updateScale();
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        this.scale = getScaleFromMass(mass);
        updateScale();
    }

    public float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
    }

    public int getFishType() {
        return fishType;
    }

    private void updateScale() {
        this.setLocalScale(this.scale, this.scale, this.scale);
        getRigidBody().setCollisionShape(new BoxShape(new javax.vecmath.Vector3f(this.scale, this.scale * 2.0f, this.scale * 5.0f)));
    }

    @Override
    public float getLinearSpeed() {
        return getScale() * super.getLinearSpeed();
    }

    public static float getScaleFromMass(float mass) {
        return (float)Math.pow(mass / 4000.0f, 1.0f / 3.0f);
    }

    public static float getMassFromScale(float scale) {
        return (float)Math.pow(scale, 3) * 4000.0f;
    }
}
