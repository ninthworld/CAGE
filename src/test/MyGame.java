package test;

import cage.core.engine.Engine;

import cage.core.application.Game;
import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.rendertarget.RenderTarget2D;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.type.EdgeType;
import cage.core.input.action.CloseWindowAction;
import cage.core.input.action.InputAction;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.Key;
import cage.core.input.type.InputActionType;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.render.RenderManager;
import cage.core.render.stage.FXRenderStage;
import cage.core.render.stage.GeometryRenderStage;
import cage.core.render.stage.RenderStage;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import cage.core.scene.camera.OrthographicCamera;
import cage.core.scene.controller.RotationController;
import cage.core.scene.light.DirectionalLight;
import cage.core.scene.light.Light;
import cage.core.scene.light.PointLight;
import cage.core.scene.light.type.AttenuationType;
import cage.glfw.GLFWBootstrap;
import org.joml.Vector3f;

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
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.ESCAPE, InputActionType.PRESS, new CloseWindowAction(engine.getWindow()));

        engine.getGUIManager().getRootContainer().addComponent(monitor);

        RotationController rc = new RotationController(1.0f, Node.UP);
        engine.getSceneManager().addController(rc);
        rotateNode = engine.getSceneManager().getRootSceneNode().createSceneNode();
        rc.addNode(rotateNode);

        dolphinEntity = rotateNode.createSceneEntity(engine.getAssetManager().loadOBJModelFile("dolphin/dolphinHighPoly.obj"));
        dolphinEntity.translate(4.0f, 2.0f, 0.0f);
        dolphinEntity.scale(2.0f);

        Sampler groundSampler = engine.getGraphicsDevice().createSampler();
        groundSampler.setEdge(EdgeType.WRAP);
        groundSampler.setMipmapping(true);
        groundSampler.setMipmapMinLOD(0.0f);
        groundSampler.setMipmapMaxLOD(8.0f);
        Model groundModel = engine.getAssetManager().loadOBJModelFile("ground/ground.obj");
        Material groundMaterial = groundModel.getMesh(0).getMaterial();
        groundMaterial.setNormalTexture(engine.getAssetManager().loadTextureFile("tiles/tiles_norm.jpg"));
        groundMaterial.getDiffuseTexture().setSampler(groundSampler);
        groundMaterial.getDiffuseTexture().setMipmapping(true);
        groundMaterial.getSpecularTexture().setSampler(groundSampler);
        groundMaterial.getSpecularTexture().setMipmapping(true);
        groundMaterial.getNormalTexture().setSampler(groundSampler);
        groundMaterial.getNormalTexture().setMipmapping(true);
        SceneEntity groundEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(groundModel);

        DirectionalLight dirLight = engine.getSceneManager().getRootSceneNode().createDirectionalLight();
        dirLight.setDirection(-1.0f, -1.0f, -1.0f);
        dirLight.setDiffuseColor(1.0f, 1.0f, 1.0f);
        dirLight.setSpecularColor(1.0f, 1.0f, 1.0f);
        dirLight.setCastShadow(true);

        DirectionalLight dirLight2 = engine.getSceneManager().getRootSceneNode().createDirectionalLight();
        dirLight2.setDirection(1.0f, -1.0f, 1.0f);
        //dirLight2.setDiffuseColor(1.0f, 1.0f, 1.0f);
        //dirLight2.setSpecularColor(1.0f, 1.0f, 1.0f);
        dirLight2.setCastShadow(true);

        engine.getSceneManager().getDefaultCamera().moveUp(4.0f);
        engine.getSceneManager().getDefaultCamera().moveBackward(12.0f);
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
            engine.getSceneManager().getDefaultCamera().translate(Node.UP.mul(16.0f * deltaTime, new Vector3f()));
        }));
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.LSHIFT, InputActionType.REPEAT, ((deltaTime, event) -> {
            engine.getSceneManager().getDefaultCamera().translate(Node.UP.mul(-16.0f * deltaTime, new Vector3f()));
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
