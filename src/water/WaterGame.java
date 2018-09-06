package water;

import biggerfish.TPCameraController;
import cage.core.application.Game;
import cage.core.engine.Engine;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.EdgeType;
import cage.core.input.action.CloseWindowAction;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.Key;
import cage.core.input.controller.InputController;
import cage.core.input.controller.JoystickController;
import cage.core.input.type.InputActionType;
import cage.core.model.Model;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import cage.core.scene.light.DirectionalLight;
import cage.core.utils.math.AABB;
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import cage.glfw.GLFWBootstrap;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class WaterGame implements Game {

    private DirectionalLight sunLight;

    public WaterGame(Engine engine, String[] args) {
    }

    @Override
    public void initialize(Engine engine) {

        engine.getRenderManager().getDefaultLightingRenderStage().setUseSkybox(true);
        engine.getRenderManager().getDefaultLightingRenderStage().setSkyboxTexture(engine.getAssetManager().loadCubeMap("skybox"));

        // Initialize Sun
        sunLight = engine.getSceneManager().getRootSceneNode().createDirectionalLight();
        sunLight.pitch(Angle.fromDegrees(-135.0f));
        sunLight.yawLocal(Angle.fromDegrees(45.0f));
        sunLight.setDiffuseColor(1.0f, 1.0f, 1.0f);
        sunLight.setSpecularColor(1.0f, 1.0f, 1.0f);
        sunLight.setCastShadow(false);

        Model cubeModel = engine.getAssetManager().loadOBJModelFile("cube/cube.obj");
        SceneEntity cubeEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(cubeModel);
        cubeEntity.translate(0.0f, 0.0f, 2.0f);

        Sampler repeatSampler = engine.getGraphicsDevice().createSampler();
        repeatSampler.setEdge(EdgeType.WRAP);
        repeatSampler.setMipmapping(true);

        Model groundModel = engine.getAssetManager().loadOBJModelFile("ground/ground.obj");
        groundModel.getMesh(0).getMaterial().getDiffuseTexture().setSampler(repeatSampler);
        groundModel.getMesh(0).getMaterial().getSpecularTexture().setSampler(repeatSampler);
        SceneEntity groundEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(groundModel);
        groundEntity.setLocalBounds(new AABB(new Vector3f(-100.0f, 0.0f, -100.0f), new Vector3f(100.0f, 0.0f, 100.0f)));
        groundEntity.translate(0.0f, -0.5f, 0.0f);

        initializeInput(engine);

        engine.getRenderManager().setOutputRenderStage(0, engine.getRenderManager().getDefaultLightingRenderStage());
    }

    private boolean canLook = false;
    private void initializeInput(Engine engine) {
        InputController mouse = engine.getInputManager().getMouseController();
        InputController keyboard = engine.getInputManager().getKeyboardController();

        // Exit - ESC
        engine.getInputManager().addAction(keyboard, Key.ESCAPE, InputActionType.PRESS, new CloseWindowAction(engine.getWindow()));

        // Sun Position
//        float sunSpeed = 1.0f;
//        engine.getInputManager().addAction(keyboard, Key.LEFT, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.yawLocal(sunSpeed * deltaTime)));
//        engine.getInputManager().addAction(keyboard, Key.RIGHT, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.yawLocal(-sunSpeed * deltaTime)));
//        engine.getInputManager().addAction(keyboard, Key.UP, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.pitch(-sunSpeed * deltaTime)));
//        engine.getInputManager().addAction(keyboard, Key.DOWN, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.pitch(sunSpeed * deltaTime)));

        // Camera
        Camera defaultCamera = engine.getSceneManager().getDefaultCamera();

        float moveSpeed = 2.0f;
        engine.getInputManager().addAction(keyboard, Key.W, InputActionType.REPEAT,((deltaTime, event) -> defaultCamera.moveForward(moveSpeed * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.S, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.moveBackward(moveSpeed * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.A, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.moveLeft(moveSpeed * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.D, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.moveRight(moveSpeed * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.SPACE, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.translate(0.0f, moveSpeed * deltaTime, 0.0f)));
        engine.getInputManager().addAction(keyboard, Key.LSHIFT, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.translate(0.0f, -moveSpeed * deltaTime, 0.0f)));

        float lookSpeed = 1.0f;
        engine.getInputManager().addAction(mouse, Button.RIGHT, InputActionType.PRESS_AND_RELEASE, ((deltaTime, event) -> { canLook = !canLook; engine.getWindow().setMouseVisible(!canLook); }));
        engine.getInputManager().addAction(mouse, Axis.LEFT_X, InputActionType.NONE, ((deltaTime, event) -> { if(canLook){
            defaultCamera.yaw(lookSpeed * event.getValue() * deltaTime);
        } }));
        engine.getInputManager().addAction(mouse, Axis.LEFT_Y, InputActionType.NONE, ((deltaTime, event) -> { if(canLook){
            defaultCamera.pitchLocal(-lookSpeed * event.getValue() * deltaTime);
        } }));
    }

    @Override
    public void destroy(Engine engine) {

    }

    @Override
    public void update(Engine engine, float deltaTime) {
    }

    @Override
    public void render(Engine engine) {

    }

    public static void main(String[] args) {
        new GLFWBootstrap("Water Game", 1600, 900).run(engine -> new WaterGame(engine, args));
    }
}
