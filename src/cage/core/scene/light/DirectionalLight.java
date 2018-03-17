package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.light.type.AttenuationType;

public class DirectionalLight extends Light {

    public DirectionalLight(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        bufferData.put(16, 2.0f);
        bufferData.rewind();
    }
}
