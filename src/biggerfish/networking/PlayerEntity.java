package biggerfish.networking;

import biggerfish.fish.FishEntity;
import biggerfish.fish.FishManager;
import cage.core.model.ExtModel;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import org.joml.Vector3fc;

import java.util.UUID;

public class PlayerEntity extends FishEntity {

    private UUID id;

    public PlayerEntity(UUID id, SceneManager sceneManager, Node parent, ExtModel model, int type, Vector3fc position, float weight, DiscreteDynamicsWorld dynamicsWorld) {
        super(sceneManager, parent, model, type, position, weight, dynamicsWorld);
        this.id = id;
    }

    public UUID getUUID() {
        return id;
    }

    @Override
    public float getEfficiency() {
        return super.getEfficiency() * (getFishType() == FishManager.GREATWHITE_TYPE ? 1.2f : 1.0f);
    }

    @Override
    public float getLinearSpeed() {
        return (super.getLinearSpeed() * 1.5f) * (getFishType() == FishManager.HAMMERHEAD_TYPE ? 1.2f : 1.0f);
    }
}
