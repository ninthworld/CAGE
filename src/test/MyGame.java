package test;

import cage.core.engine.Engine;

import cage.core.application.IGame;
import cage.core.input.action.CloseWindowAction;
import cage.core.input.component.Key;
import cage.core.input.type.ActionType;
import cage.core.model.Model;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.controller.RotationController;
import cage.core.scene.light.Light;
import cage.core.scene.light.PointLight;
import cage.core.scene.light.type.AttenuationType;
import cage.glfw.GLFWBootstrap;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Iterator;

public class MyGame implements IGame {

    private SceneNode rotateNode;
    private SceneEntity dolphinEntity;

    public MyGame(Engine engine) {
    }

    @Override
    public void initialize(Engine engine) {
        engine.getGraphicsContext().setClearColor(Color.decode("#6495ed"));

        rotateNode = engine.getSceneManager().getRootSceneNode().createSceneNode();

        Model model = engine.getAssetManager().loadOBJModel("dolphin/dolphinHighPoly.obj");
        dolphinEntity = rotateNode.createSceneEntity(model);
        dolphinEntity.translate(4.0f, 0.0f, 0.0f);
        dolphinEntity.scale(2.0f);

        PointLight light = engine.getSceneManager().getRootSceneNode().createPointLight();
        light.setLocalPosition(0.0f, 0.0f, 0.0f);
        light.setRange(32.0f);
        light.setAttenuation(AttenuationType.QUADRATIC);
        light.setDiffuseColor(1.0f, 1.0f, 1.0f);
        light.setSpecularColor(1.0f, 1.0f, 1.0f);

        RotationController rc = new RotationController(0.5f, new Vector3f(0.0f, 1.0f, 0.0f));
        engine.getSceneManager().registerController(rc);
        rc.attachNode(engine.getSceneManager().getDefaultCamera());

        engine.getInputManager().registerAction(engine.getInputManager().getKeyboardController(), Key.ESCAPE, ActionType.PRESS, new CloseWindowAction(engine.getWindow()));
    }

    @Override
    public void destroy(Engine engine) {
    }

    @Override
    public void update(Engine engine, float deltaTime) {
        engine.getWindow().setTitle("FPS: " + engine.getFPS());

        Iterator<Light> it = engine.getSceneManager().getLightIterator();
        while(it.hasNext()) {
            Light light = it.next();
            light.notifyUpdate();
        }
    }

    @Override
    public void render(Engine engine) {
    }

    public static void main(String[] args) {
        new GLFWBootstrap("Test Game", 1600, 900).run(MyGame::new);
    }
}
