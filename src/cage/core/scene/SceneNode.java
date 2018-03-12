package cage.core.scene;

import cage.core.model.Model;

public class SceneNode extends Node {

    public SceneNode(Node parent) {
        super(parent);
    }

    public SceneNode addSceneNode() {
        SceneNode node = new SceneNode(null);
        attachNode(node);
        return node;
    }

    public SceneEntity addSceneEntity(Model model) {
        SceneEntity node = new SceneEntity(null, model);
        attachNode(node);
        return node;
    }
}
