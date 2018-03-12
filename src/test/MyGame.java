package test;

import cage.core.application.GameEngine;

import cage.core.application.IGame;
import cage.core.graphics.type.CullType;
import cage.core.model.Model;
import cage.core.scene.SceneEntity;
import cage.core.scene.light.PointLight;
import cage.core.scene.light.type.AttenuationType;
import cage.opengl.GLBootstrap;
import org.joml.Vector3f;

import java.awt.*;

public class MyGame implements IGame {

    private PointLight m_light;

    public MyGame(GameEngine engine) {
    }

    @Override
    public void initialize(GameEngine engine) {

        engine.getGraphicsContext().setClearColor(Color.decode("#6495ed"));
        engine.getGraphicsDevice().getDefaultRasterizer().setCullType(CullType.NONE);

        engine.getSceneManager().getDefaultCamera().setLocalPosition(0.0f, 0.0f, 4.0f);

        Model sphereModel = engine.getAssetManager().loadOBJModel("sphere/sphere.obj");
        sphereModel.getMesh(0).getMaterial().setDiffuse(new Vector3f(1.0f, 0.0f, 0.0f));
        sphereModel.getMesh(0).getMaterial().setSpecular(new Vector3f(1.0f, 1.0f, 1.0f), 16.0f);
        SceneEntity sphere = engine.getSceneManager().addSceneEntity(sphereModel);

        m_light = engine.getSceneManager().createPointLight();
        m_light.setRange(20.0f);
        m_light.setAttenuation(AttenuationType.LINEAR);
        m_light.setDiffuseColor(1.0f, 1.0f, 1.0f);
        m_light.setSpecularColor(1.0f, 1.0f, 1.0f);
    }

    @Override
    public void destroy(GameEngine engine) {
    }
    
    @Override
    public void update(GameEngine engine, double deltaTime) {
        float angle = m_light.getLocalRotation().y;
        m_light.setLocalPosition((float)Math.sin(angle) * 8.0f, 4.0f, (float)Math.cos(angle) * 8.0f);
        m_light.setLocalRotation(0.0f, angle + 0.03f, 0.0f);
    }

    @Override
    public void render(GameEngine engine) {
    }

    public static void main(String[] args) {
        new GLBootstrap("Test Game", 1600, 900).run(MyGame::new);
    }
}
