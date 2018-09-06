package water;

import biggerfish.TPCameraController;
import cage.core.application.Game;
import cage.core.engine.Engine;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.EdgeType;
import cage.core.graphics.vertexarray.VertexArray;
import cage.core.input.action.CloseWindowAction;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.Key;
import cage.core.input.controller.InputController;
import cage.core.input.controller.JoystickController;
import cage.core.input.type.InputActionType;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import cage.core.scene.controller.NodeController;
import cage.core.scene.light.DirectionalLight;
import cage.core.scene.light.Light;
import cage.core.utils.math.AABB;
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import cage.glfw.GLFWBootstrap;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

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
        sunLight.pitch(Angle.fromDegrees(135.0f));
        sunLight.yawLocal(Angle.fromDegrees(45.0f));
        sunLight.setDiffuseColor(1.0f, 1.0f, 1.0f);
        sunLight.setSpecularColor(1.0f, 1.0f, 1.0f);
        sunLight.setCastShadow(false);

        Model cubeModel = engine.getAssetManager().loadOBJModelFile("cube/cube.obj");
        SceneEntity cubeEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(cubeModel);
        cubeEntity.translate(0.0f, 0.0f, -2.0f);

        Sampler repeatSampler = engine.getGraphicsDevice().createSampler();
        repeatSampler.setEdge(EdgeType.WRAP);
        repeatSampler.setMipmapping(true);

        Model groundModel = engine.getAssetManager().loadOBJModelFile("ground/ground.obj");
        groundModel.getMesh(0).getMaterial().getDiffuseTexture().setSampler(repeatSampler);
        groundModel.getMesh(0).getMaterial().getSpecularTexture().setSampler(repeatSampler);
        SceneEntity groundEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(groundModel);
        groundEntity.setLocalBounds(new AABB(new Vector3f(-100.0f, 0.0f, -100.0f), new Vector3f(100.0f, 0.0f, 100.0f)));
        groundEntity.translate(0.0f, -0.5f, 0.0f);

        // START WATER
        float[] waterPositions = new float[] {
                1.0f, 0.0f, -1.0f,
                -1.0f, 0.0f, -1.0f,
                -1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f
        };
        VertexBuffer waterVertexBuffer = engine.getGraphicsDevice().createVertexBuffer();
        waterVertexBuffer.setLayout(new LayoutConfig().float3());
        waterVertexBuffer.setUnitCount(waterPositions.length / 3);
        waterVertexBuffer.writeData((FloatBuffer) BufferUtils.createFloatBuffer(waterPositions.length).put(waterPositions).rewind());

        int[] waterIndices = new int[] { 0, 1, 2, 2, 3, 0 };
        IndexBuffer waterIndexBuffer = engine.getGraphicsDevice().createIndexBuffer();
        waterIndexBuffer.setUnitCount(waterIndices.length);
        waterIndexBuffer.writeData((IntBuffer)BufferUtils.createIntBuffer(waterIndices.length).put(waterIndices).rewind());

        VertexArray quadVertexArray = engine.getGraphicsDevice().createVertexArray();
        quadVertexArray.addVertexBuffer(waterVertexBuffer);

        Material waterMaterial = new Material();
        waterMaterial.setDiffuse(0.0f, 0.05f, 0.8f);
        waterMaterial.setSpecular(0.0f, 0.0f, 0.0f);
        waterMaterial.setShininess(0.0f);
        Model waterModel = new Model(quadVertexArray);
        waterModel.addMesh(new Mesh(waterIndexBuffer, waterMaterial, engine.getGraphicsDevice().getDefaultRasterizer()));

        SceneEntity waterEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(waterModel);
        waterEntity.moveUp(0.4f);
        waterEntity.scale(10.0f);

        // END WATER

        initializeInput(engine);

        //engine.getRenderManager().setOutputRenderStage(0, engine.getRenderManager().getDefaultLightingRenderStage());
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

        float moveSpeed = 0.002f;
        engine.getInputManager().addAction(keyboard, Key.W, InputActionType.REPEAT,((deltaTime, event) -> defaultCamera.moveForward(-moveSpeed)));
        engine.getInputManager().addAction(keyboard, Key.S, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.moveBackward(-moveSpeed)));
        engine.getInputManager().addAction(keyboard, Key.A, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.moveLeft(moveSpeed)));
        engine.getInputManager().addAction(keyboard, Key.D, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.moveRight(moveSpeed)));
        engine.getInputManager().addAction(keyboard, Key.SPACE, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.translate(0.0f, moveSpeed, 0.0f)));
        engine.getInputManager().addAction(keyboard, Key.LSHIFT, InputActionType.REPEAT, ((deltaTime, event) -> defaultCamera.translate(0.0f, -moveSpeed, 0.0f)));

        float lookSpeed = 0.006f;
        engine.getInputManager().addAction(mouse, Button.RIGHT, InputActionType.PRESS_AND_RELEASE, ((deltaTime, event) -> { canLook = !canLook; engine.getWindow().setMouseVisible(!canLook); }));
        engine.getInputManager().addAction(mouse, Axis.LEFT_X, InputActionType.NONE, ((deltaTime, event) -> { if(canLook){
            defaultCamera.yawLocal(-lookSpeed * event.getValue());
        } }));
        engine.getInputManager().addAction(mouse, Axis.LEFT_Y, InputActionType.NONE, ((deltaTime, event) -> { if(canLook){
            defaultCamera.pitch(lookSpeed * event.getValue());
        } }));
    }

    @Override
    public void destroy(Engine engine) {

    }

    @Override
    public void update(Engine engine, float deltaTime) {
        // Force Lighting Update
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
        new GLFWBootstrap("Water Game", 1600, 900).run(engine -> new WaterGame(engine, args));
    }
}
