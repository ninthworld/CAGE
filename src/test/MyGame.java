package test;

import cage.core.engine.Engine;

import cage.core.application.Game;
import cage.core.input.action.CloseWindowAction;
import cage.core.input.action.InputAction;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.Key;
import cage.core.input.type.InputActionType;
import cage.core.model.Model;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.light.Light;
import cage.core.scene.light.PointLight;
import cage.core.scene.light.type.AttenuationType;
import cage.glfw.GLFWBootstrap;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Iterator;

public class MyGame implements Game {

    private SceneNode rotateNode;
    private SceneEntity dolphinEntity;
    private FPSMonitor monitor;
    private boolean canLook;

    public MyGame(Engine engine) {
        this.monitor = new FPSMonitor(engine.createTimer());
        this.canLook = false;
    }


    @Override
    public void initialize(Engine engine) {
        engine.getGraphicsContext().setClearColor(Color.decode("#6495ed"));
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.ESCAPE, InputActionType.PRESS, new CloseWindowAction(engine.getWindow()));

        engine.getAssetManager().loadFont("Arial", "arial.ttf");
        engine.getGUIManager().getRootContainer().addComponent(monitor);

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

        InputAction mouseAction = (deltaTime, event) -> {
            if(canLook) {
                if (event.getComponent() == Axis.LEFT_X) {
                    engine.getSceneManager().getDefaultCamera().rotate(0.5f * deltaTime * event.getValue(), new Vector3f(0.0f, 1.0f, 0.0f));
                } else if (event.getComponent() == Axis.LEFT_Y) {
                    engine.getSceneManager().getDefaultCamera().rotate(0.5f * deltaTime * event.getValue(), new Vector3f(engine.getSceneManager().getDefaultCamera().getLocalRight()));
                }
            }
        };
        engine.getInputManager().addAction(engine.getInputManager().getMouseController(), Axis.LEFT_X, InputActionType.NONE, mouseAction);
        engine.getInputManager().addAction(engine.getInputManager().getMouseController(), Axis.LEFT_Y, InputActionType.NONE, mouseAction);
        engine.getInputManager().addAction(engine.getInputManager().getMouseController(), Button.RIGHT, InputActionType.PRESS, ((deltaTime, event) -> {
            canLook = true;
            engine.getWindow().setMouseCentered(true);
            engine.getWindow().setMouseVisible(false);
        }));
        engine.getInputManager().addAction(engine.getInputManager().getMouseController(), Button.RIGHT, InputActionType.RELEASE, ((deltaTime, event) -> {
            canLook = false;
            engine.getWindow().setMouseCentered(false);
            engine.getWindow().setMouseVisible(true);
        }));

        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.W, InputActionType.REPEAT, ((deltaTime, event) -> {
            engine.getSceneManager().getDefaultCamera().moveForward(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.A, InputActionType.REPEAT, ((deltaTime, event) -> {
            engine.getSceneManager().getDefaultCamera().moveLeft(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.S, InputActionType.REPEAT, ((deltaTime, event) -> {
            engine.getSceneManager().getDefaultCamera().moveBackward(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.D, InputActionType.REPEAT, ((deltaTime, event) -> {
            engine.getSceneManager().getDefaultCamera().moveRight(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.SPACE, InputActionType.REPEAT, ((deltaTime, event) -> {
            engine.getSceneManager().getDefaultCamera().moveUp(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.LSHIFT, InputActionType.REPEAT, ((deltaTime, event) -> {
            engine.getSceneManager().getDefaultCamera().moveDown(16.0f * deltaTime);
        }));
    }

    @Override
    public void destroy(Engine engine) {
    }

    @Override
    public void update(Engine engine, float deltaTime) {
        Iterator<Light> it = engine.getSceneManager().getLightIterator();
        while(it.hasNext()) {
            Light light = it.next();
            light.notifyUpdate();
        }

        monitor.update(deltaTime);
    }

    @Override
    public void render(Engine engine) {
    }

    public static void main(String[] args) {
        new GLFWBootstrap("Test Game", 1600, 900).setSamples(2).run(MyGame::new);
    }
}
