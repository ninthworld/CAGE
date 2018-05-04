package biggerfish.physics;

import cage.core.model.ExtModel;
import cage.core.model.Model;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneManager;
import cage.core.utils.math.Direction;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.vecmath.Matrix4f;

public class PhysicsEntity extends SceneEntity {

    private RigidBody rigidBody;
    private Vector3f linearVelocity;
    private Vector3f angularVelocity;

    public PhysicsEntity(SceneManager sceneManager, Node parent, Model model, RigidBody rigidBody) {
        super(sceneManager, parent, model);
        this.rigidBody = rigidBody;
        this.linearVelocity = new Vector3f();
        this.angularVelocity = new Vector3f();
    }

    @Override
    public void update(boolean forced) {
        super.update(forced);
        notifyUpdate();
    }

    @Override
    public void updateNode() {
        if(getModel() instanceof ExtModel) {
            ExtModel model = (ExtModel) getModel();
            if((model.isPaused() || model.isFinishAndPause()) && (linearVelocity.length() > 0.0f || angularVelocity.length() > 0.0f)) {
                model.start();
            }
            else if((!model.isPaused() || !model.isFinishAndPause()) && (linearVelocity.length() == 0.0f && angularVelocity.length() == 0.0f)) {
                model.finishAndPause();
            }
        }

        setLinearVelocity(linearVelocity);
        setAngularVelocity(angularVelocity);

        Transform transform = rigidBody.getMotionState().getWorldTransform(new Transform());
        setLocalPosition(new Vector3f(transform.origin.x, transform.origin.y, transform.origin.z));
        setLocalRotation(new Matrix3f(transform.basis.m00, transform.basis.m01, transform.basis.m02, transform.basis.m10, transform.basis.m11, transform.basis.m12, transform.basis.m20, transform.basis.m21, transform.basis.m22));
        super.updateNode();

        linearVelocity.set(0.0f);
        angularVelocity.set(0.0f);
    }

    @Override
    public void moveForward(float amount) {
        translate(getLocalForward().mul(amount, new Vector3f()));
    }

    @Override
    public void moveRight(float amount) {
        translate(getLocalRight().mul(amount, new Vector3f()));
    }

    @Override
    public void translate(Vector3fc offset) {
        linearVelocity.add(offset);
    }

    @Override
    public void rotate(float amount, Vector3fc axis) {
        angularVelocity.add(axis.mul(amount, new Vector3f()));
    }

    private void setLinearVelocity(Vector3fc velocity) {
        rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(velocity.x(), velocity.y(), velocity.z()));
    }

    private void setAngularVelocity(Vector3f velocity) {
        rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(velocity.x(), velocity.y(), velocity.z()));
    }
}
