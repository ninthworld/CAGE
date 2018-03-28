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
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.texture.TextureCubeMap;
import cage.core.graphics.type.EdgeType;
import cage.core.input.action.CloseWindowAction;
import cage.core.input.action.InputAction;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.Key;
import cage.core.input.controller.InputController;
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
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import cage.glfw.GLFWBootstrap;
import org.joml.Vector3f;

import java.util.Iterator;

public class MyGame implements Game {

    private SceneNode rotateNode;
    private SceneEntity dolphinEntity;
    private FPSMonitor monitor;
    private boolean canLook;
    private DirectionalLight sunLight;

    public MyGame(Engine engine) {
        this.monitor = new FPSMonitor(engine.createTimer());
        this.canLook = false;
    }

    @Override
    public void initialize(Engine engine) {
        engine.getGUIManager().getRootContainer().addComponent(monitor);

        engine.getRenderManager().getDefaultLightingRenderStage().setUseAtmosphere(true);

//        engine.getRenderManager().getDefaultLightingRenderStage().setUseSkydome(true);
//        Texture skydome = engine.getAssetManager().loadTextureFile("skydome/skydome.jpg");
//        engine.getRenderManager().getDefaultLightingRenderStage().setSkydomeTexture(skydome);
//
//        engine.getRenderManager().getDefaultLightingRenderStage().setUseSkybox(true);
//        TextureCubeMap skydome = engine.getAssetManager().loadCubeMap("skybox");
//        engine.getRenderManager().getDefaultLightingRenderStage().setSkyboxTexture(skydome);

        RotationController rc = new RotationController(-0.4f, Direction.UP);
        engine.getSceneManager().addController(rc);
        rotateNode = engine.getSceneManager().getRootSceneNode().createSceneNode();
        rc.addNode(rotateNode);

        dolphinEntity = rotateNode.createSceneEntity(engine.getAssetManager().loadOBJModelFile("dolphin/dolphinHighPoly.obj"));
        dolphinEntity.translate(4.0f, 1.0f, 0.0f);

        Model groundModel = engine.getAssetManager().loadOBJModelFile("ground/ground.obj");
        SceneEntity groundEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(groundModel);

        Material tileMaterial = groundModel.getMesh(0).getMaterial();
        Sampler groundSampler = engine.getGraphicsDevice().createSampler();
        groundSampler.setEdge(EdgeType.WRAP);
        groundSampler.setMipmapping(true);
        tileMaterial.setNormalTexture(engine.getAssetManager().loadTextureFile("tiles/tiles_norm.jpg"));
        tileMaterial.getNormalTexture().setSampler(groundSampler);
        tileMaterial.getNormalTexture().setMipmapping(true);
        tileMaterial.getDiffuseTexture().setSampler(groundSampler);
        tileMaterial.getDiffuseTexture().setMipmapping(true);
        tileMaterial.getSpecularTexture().setSampler(groundSampler);
        tileMaterial.getSpecularTexture().setMipmapping(true);

        Material tileMaterial2 = new Material();
        tileMaterial2.setDiffuse(tileMaterial.getDiffuseTexture());
        tileMaterial2.setDiffuse(tileMaterial.getDiffuseColor());
        tileMaterial2.setSpecular(tileMaterial.getSpecularTexture());
        tileMaterial2.setSpecular(tileMaterial.getSpecularColor());
        tileMaterial2.setHighlight(tileMaterial.getHighlightTexture());
        tileMaterial2.setShininess(tileMaterial.getShininess());

        Model cubeModel = engine.getAssetManager().loadOBJModelFile("cube/cube.obj");
        cubeModel.getMesh(0).setMaterial(tileMaterial);
        Model slopeModel = engine.getAssetManager().loadOBJModelFile("slope/slope.obj");
        slopeModel.getMesh(0).setMaterial(tileMaterial2);

        SceneEntity cubeEntity1 = engine.getSceneManager().getRootSceneNode().createSceneEntity(cubeModel);
        cubeEntity1.moveUp(0.5f);

        SceneEntity cubeEntity2 = engine.getSceneManager().getRootSceneNode().createSceneEntity(cubeModel);
        cubeEntity2.moveUp(1.5f);
        cubeEntity2.moveForward(2.0f);

        SceneEntity cubeEntity3 = engine.getSceneManager().getRootSceneNode().createSceneEntity(cubeModel);
        cubeEntity3.moveUp(1.5f);
        cubeEntity3.moveForward(3.0f);

        SceneEntity cubeEntity4 = engine.getSceneManager().getRootSceneNode().createSceneEntity(cubeModel);
        cubeEntity4.moveUp(0.5f);
        cubeEntity4.moveForward(3.0f);

        SceneEntity slopeEntity1 = engine.getSceneManager().getRootSceneNode().createSceneEntity(slopeModel);
        slopeEntity1.moveUp(0.5f);
        slopeEntity1.moveLeft(1.0f);

        SceneEntity slopeEntity2 = engine.getSceneManager().getRootSceneNode().createSceneEntity(slopeModel);
        slopeEntity2.moveUp(0.5f);
        slopeEntity2.moveForward(1.0f);
        slopeEntity2.yawGlobal(Angle.fromDegrees(-90.0f));
        slopeEntity2.pitch(Angle.fromDegrees(180.0f));

        SceneEntity slopeEntity3 = engine.getSceneManager().getRootSceneNode().createSceneEntity(slopeModel);
        slopeEntity3.moveUp(1.5f);
        slopeEntity3.moveForward(1.0f);
        slopeEntity3.yawGlobal(Angle.fromDegrees(90.0f));

        sunLight = engine.getSceneManager().getRootSceneNode().createDirectionalLight();
        sunLight.pitch(Angle.fromDegrees(-135.0f));
        sunLight.yawGlobal(Angle.fromDegrees(45.0f));
        sunLight.setDiffuseColor(1.0f, 1.0f, 1.0f);
        sunLight.setSpecularColor(1.0f, 1.0f, 1.0f);
        sunLight.setCastShadow(true);

        DirectionalLight dirLight = engine.getSceneManager().getRootSceneNode().createDirectionalLight();
        dirLight.pitch(Angle.fromDegrees(-135.0f));
        dirLight.yawGlobal(Angle.fromDegrees(-45.0f));
        dirLight.setDiffuseColor(1.0f, 1.0f, 1.0f);
        dirLight.setSpecularColor(1.0f, 1.0f, 1.0f);
        dirLight.setCastShadow(true);
		
        initializeInput(engine);
    }

    private void initializeInput(Engine engine) {
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.ESCAPE, InputActionType.PRESS, new CloseWindowAction(engine.getWindow()));

        InputController mouse = engine.getInputManager().getMouseController();
        InputController keyboard = engine.getInputManager().getKeyboardController();

        Camera defaultCamera = engine.getSceneManager().getDefaultCamera();
        defaultCamera.moveUp(4.0f);
        defaultCamera.moveBackward(12.0f);
        InputAction mouseAction = (deltaTime, event) -> {
            if(canLook) {
                if (event.getComponent() == Axis.LEFT_X) {
                    defaultCamera.rotate(0.5f * deltaTime * event.getValue(), new Vector3f(0.0f, 1.0f, 0.0f));
                } else if (event.getComponent() == Axis.LEFT_Y) {
                    defaultCamera.rotate(0.5f * deltaTime * event.getValue(), new Vector3f(defaultCamera.getLocalRight()));
                }
            }
        };
        engine.getInputManager().addAction(mouse, Axis.LEFT_X, InputActionType.NONE, mouseAction);
        engine.getInputManager().addAction(mouse, Axis.LEFT_Y, InputActionType.NONE, mouseAction);
        engine.getInputManager().addAction(mouse, Button.RIGHT, InputActionType.PRESS, ((deltaTime, event) -> {
            canLook = true;
            engine.getWindow().setMouseVisible(false);
        }));
        engine.getInputManager().addAction(mouse, Button.RIGHT, InputActionType.RELEASE, ((deltaTime, event) -> {
            canLook = false;
            engine.getWindow().setMouseVisible(true);
        }));

        engine.getInputManager().addAction(keyboard, Key.W, InputActionType.REPEAT, ((deltaTime, event) -> {
            defaultCamera.moveForward(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(keyboard, Key.A, InputActionType.REPEAT, ((deltaTime, event) -> {
            defaultCamera.moveLeft(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(keyboard, Key.S, InputActionType.REPEAT, ((deltaTime, event) -> {
            defaultCamera.moveBackward(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(keyboard, Key.D, InputActionType.REPEAT, ((deltaTime, event) -> {
            defaultCamera.moveRight(16.0f * deltaTime);
        }));
        engine.getInputManager().addAction(keyboard, Key.SPACE, InputActionType.REPEAT, ((deltaTime, event) -> {
            defaultCamera.translate(Direction.UP.mul(16.0f * deltaTime, new Vector3f()));
        }));
        engine.getInputManager().addAction(keyboard, Key.LSHIFT, InputActionType.REPEAT, ((deltaTime, event) -> {
            defaultCamera.translate(Direction.UP.mul(-16.0f * deltaTime, new Vector3f()));
        }));

        // Sun position
        engine.getInputManager().addAction(keyboard, Key.LEFT, InputActionType.REPEAT, ((deltaTime, event) -> {
            sunLight.yawGlobal(2.0f * deltaTime);
        }));
        engine.getInputManager().addAction(keyboard, Key.RIGHT, InputActionType.REPEAT, ((deltaTime, event) -> {
            sunLight.yawGlobal(-2.0f * deltaTime);
        }));
        engine.getInputManager().addAction(keyboard, Key.UP, InputActionType.REPEAT, ((deltaTime, event) -> {
            sunLight.pitch(-2.0f * deltaTime);
        }));
        engine.getInputManager().addAction(keyboard, Key.DOWN, InputActionType.REPEAT, ((deltaTime, event) -> {
            sunLight.pitch(2.0f * deltaTime);
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

        engine.getRenderManager().getDefaultLightingRenderStage().setSunPosition(sunLight.getDirection());

        monitor.update(deltaTime);
    }

    @Override
    public void render(Engine engine) {
    }

    public static void main(String[] args) {
        new GLFWBootstrap("Test Game", 1600, 900).setSamples(2).run(MyGame::new);
    }
}
