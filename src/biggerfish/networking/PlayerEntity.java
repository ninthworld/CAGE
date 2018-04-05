package biggerfish.networking;

import cage.core.model.Model;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneManager;

import java.util.UUID;

public class PlayerEntity extends SceneEntity {

    private UUID id;

    public PlayerEntity(UUID id, SceneManager sceneManager, Node parent, Model model) {
        super(sceneManager, parent, model);
        this.id = id;
    }

    public UUID getUUID() {
        return id;
    }
}
