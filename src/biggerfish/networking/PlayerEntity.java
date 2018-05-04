package biggerfish.networking;

import biggerfish.physics.PhysicsEntity;
import cage.core.model.ExtModel;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import com.bulletphysics.dynamics.RigidBody;

import java.util.UUID;

public class PlayerEntity extends PhysicsEntity {

    private UUID id;

    public PlayerEntity(UUID id, SceneManager sceneManager, Node parent, ExtModel model, RigidBody rigidBody) {
        super(sceneManager, parent, model, rigidBody);
        this.id = id;
    }

    public UUID getUUID() {
        return id;
    }

    public ExtModel getExtModel() {
        return (ExtModel) getModel();
    }
}
