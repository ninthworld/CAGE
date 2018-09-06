package biggerfish.physics;

import cage.core.model.ExtModel;
import cage.core.model.Model;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneManager;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PhysicsEntity extends SceneEntity {

    private RigidBody rigidBody;
    private Vector3f linearVelocity;
    private Vector3f angularVelocity;
    private boolean moving;
    private boolean dummy;
    private DiscreteDynamicsWorld dynamicsWorld;

    public PhysicsEntity(SceneManager sceneManager, Node parent, Model model, Vector3fc position, CollisionShape shape, DiscreteDynamicsWorld dynamicsWorld) {
        super(sceneManager, parent, model);

        this.dynamicsWorld = dynamicsWorld;

        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(position.x(), position.y(), position.z());
        MotionState motion = new DefaultMotionState(transform);
        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(1.0f, motion, shape);
        this.rigidBody = new RigidBody(ci);
        this.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        if(this.dynamicsWorld != null) {
            this.dynamicsWorld.addRigidBody(this.rigidBody, (short)0b100, (short)0b111);
        }

        this.linearVelocity = new Vector3f();
        this.angularVelocity = new Vector3f();
        this.moving = false;
        this.dummy = false;

        this.rigidBody.setUserPointer(this);
    }

    @Override
    public void update(boolean forced) {
        super.update(forced);
        notifyUpdate();
    }

    @Override
    public void updateNode() {
        moving = (linearVelocity.length() > 0.0f || angularVelocity.length() > 0.0f);

        if(rigidBody != null) {
            setLinearVelocity(linearVelocity);
            setAngularVelocity(angularVelocity);

            Transform transform = rigidBody.getMotionState().getWorldTransform(new Transform());
            setLocalPosition(new Vector3f(transform.origin.x, transform.origin.y, transform.origin.z));
            setLocalRotation(new Matrix3f(transform.basis.m00, transform.basis.m01, transform.basis.m02, transform.basis.m10, transform.basis.m11, transform.basis.m12, transform.basis.m20, transform.basis.m21, transform.basis.m22));
        }
        super.updateNode();

        if(!dummy) {
            linearVelocity.set(0.0f);
            angularVelocity.set(0.0f);
        }
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
        linearVelocity.add(offset.mul(getLinearSpeed(), new Vector3f()));
    }

    @Override
    public void rotate(float amount, Vector3fc axis) {
        angularVelocity.add(axis.mul(amount * getAngularSpeed(), new Vector3f()));
    }

    public void setPosition(Vector3fc position) {
        Transform transform = rigidBody.getMotionState().getWorldTransform(new Transform());
        transform.origin.set(position.x(), position.y(), position.z());
        MotionState motion = new DefaultMotionState(transform);
        rigidBody.setMotionState(motion);
    }

    public void setRotation(Matrix3fc rotation) {
        Transform transform = rigidBody.getMotionState().getWorldTransform(new Transform());
        transform.basis.set(new javax.vecmath.Matrix3f(rotation.m00(), rotation.m01(), rotation.m02(), rotation.m10(), rotation.m11(), rotation.m12(), rotation.m20(), rotation.m21(), rotation.m22()));
        MotionState motion = new DefaultMotionState(transform);
        rigidBody.setMotionState(motion);
    }

    public boolean isMoving() {
        return moving;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    public DiscreteDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    public float getLinearSpeed() {
        return 1600.0f;
    }

    public float getAngularSpeed() {
        return -80.0f;
    }

    public void setLinearVelocity(Vector3fc velocity) {
        linearVelocity.set(velocity);
        rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(velocity.x(), velocity.y(), velocity.z()));
    }

    public void setAngularVelocity(Vector3fc velocity) {
        angularVelocity.set(velocity);
        rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(velocity.x(), velocity.y(), velocity.z()));
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
}
