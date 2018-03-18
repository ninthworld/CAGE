package test;

import cage.core.application.GameEngine;

import cage.core.application.GameWindow;
import cage.core.application.IGame;
import cage.core.graphics.type.FillType;
import cage.core.input.InputState;
import cage.core.model.Model;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.light.Light;
import cage.core.scene.light.PointLight;
import cage.core.scene.light.type.AttenuationType;
import cage.opengl.GLBootstrap;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Iterator;

public class MyGame implements IGame {

    private SceneNode rotateNode;
    private SceneEntity dolphinEntity;

    public MyGame(GameEngine engine) {
    }

    @Override
    public void initialize(GameEngine engine) {
        engine.getGraphicsContext().setClearColor(Color.decode("#6495ed"));

        engine.getSceneManager().getDefaultCamera().setLocalPosition(0.0f, 0.0f, 8.0f);

        rotateNode = engine.getSceneManager().getRootSceneNode().createSceneNode();

        Model model = engine.getAssetManager().loadOBJModel("dolphin/dolphinHighPoly.obj");
        dolphinEntity = rotateNode.createSceneEntity(model);
        dolphinEntity.translate(4.0f, 0.0f, 0.0f);
        dolphinEntity.scale(new Vector3f(2.0f));

        PointLight light = engine.getSceneManager().getRootSceneNode().createPointLight();
        light.setLocalPosition(0.0f, 0.0f, 0.0f);
        light.setRange(32.0f);
        light.setAttenuation(AttenuationType.QUADRATIC);
        light.setDiffuseColor(1.0f, 1.0f, 1.0f);
        light.setSpecularColor(1.0f, 1.0f, 1.0f);

        engine.getWindow().addListener((GameWindow.IKeyboardListener) (key, state) -> {
            if(key == 256) {
                engine.getWindow().setClosed(true);
            }
            if(key == '1' && state == InputState.RELEASED) {
                engine.getWindow().setFullscreen(!engine.getWindow().isFullscreen());
            }
        });
    }

    @Override
    public void destroy(GameEngine engine) {
    }
    
    @Override
    public void update(GameEngine engine, double deltaTime) {
        engine.getWindow().setTitle("FPS: " + engine.getFPS());

        //dolphinEntity.setLocalRotation(dolphinEntity.getLocalRotation().add(0.0f, (float)deltaTime, 0.0f));
        rotateNode.rotate((float)deltaTime * 0.5f, new Vector3f(0.0f, 1.0f, 0.0f));
        //dolphinEntity.rotate((float)deltaTime, new Vector3f(0.0f, 1.0f, 0.0f));

        Iterator<Light> it = engine.getSceneManager().getLightIterator();
        while(it.hasNext()) {
            Light light = it.next();
            light.notifyUpdate();
        }
    }

    @Override
    public void render(GameEngine engine) {
    }

    public static void main(String[] args) {
        new GLBootstrap("Test Game", 1600, 900).run(MyGame::new);
    }
}
