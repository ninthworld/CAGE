package biggerfish;

import biggerfish.gui.FPSMonitor;
import biggerfish.networking.BiggerFishClient;
import biggerfish.networking.PlayerEntity;
import biggerfish.terrain.TerrainManager;
import biggerfish.terrain.TerrainRenderStage;
import biggerfish.water.CombineRenderStage;
import biggerfish.water.UnderwaterRenderStage;
import biggerfish.water.WaterManager;
import biggerfish.water.WaterRenderStage;
import cage.core.application.Timer;
import cage.core.engine.Engine;

import cage.core.application.Game;
import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.type.CullType;
import cage.core.graphics.type.EdgeType;
import cage.core.graphics.type.FillType;
import cage.core.graphics.type.FormatType;
import cage.core.graphics.vertexarray.VertexArray;
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
import cage.core.render.stage.RenderStage;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import cage.core.scene.controller.RotationController;
import cage.core.scene.controller.TPCameraController;
import cage.core.scene.light.DirectionalLight;
import cage.core.scene.light.Light;
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import cage.glfw.GLFWBootstrap;
import cage.opengl.engine.GLEngine;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import ray.networking.IGameConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.FloatBuffer;
import java.util.Iterator;

import static org.lwjgl.system.MemoryStack.stackPush;

public class BiggerFishGame implements Game {

    private BiggerFishClient client;
    private boolean multiplayer;
    private boolean connected;

    private PlayerEntity player;

    private FPSMonitor monitor;
    private boolean canLook;
    private DirectionalLight sunLight;
    private TerrainManager terrainManager;
    private WaterManager waterManager;

    public BiggerFishGame(Engine engine, String[] args) {
        this.monitor = new FPSMonitor(engine.createTimer());
        this.canLook = false;

        Model dolphinModel = engine.getAssetManager().loadOBJModelFile("dolphin/dolphinHighPoly.obj");
        this.player = new PlayerEntity(null, engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), dolphinModel);
        this.player.scale(4.0f);
        this.player.moveUp(68.0f);
        this.player.addNode(engine.getSceneManager().getDefaultCamera());

        this.multiplayer = false;
        this.connected = false;
        if(args.length > 0) {
            String[] split = args[0].split(":");
            if(split.length == 2) {
                String serverAddress = split[0];
                int serverPort = Integer.parseInt(split[1]);
                try {
                    this.client = new BiggerFishClient(InetAddress.getByName(serverAddress), serverPort, IGameConnection.ProtocolType.UDP, this, engine);
                    this.multiplayer = true;
                    this.client.sendJoinMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initialize(Engine engine) {
        engine.getGUIManager().getRootContainer().addComponent(monitor);

        engine.getRenderManager().getDefaultLightingRenderStage().setUseAtmosphere(true);
//        engine.getRenderManager().getDefaultLightingRenderStage().setUseSkybox(true);
//        engine.getRenderManager().getDefaultLightingRenderStage().setSkyboxTexture(engine.getAssetManager().loadCubeMap("skybox"));
//        engine.getRenderManager().getDefaultLightingRenderStage().setUseSkydome(true);
//        engine.getRenderManager().getDefaultLightingRenderStage().setSkydomeTexture(engine.getAssetManager().loadTextureFile("skydome/skydome.jpg"));

        sunLight = engine.getSceneManager().getRootSceneNode().createDirectionalLight();
        sunLight.pitch(Angle.fromDegrees(-135.0f));
        sunLight.yawGlobal(Angle.fromDegrees(45.0f));
        sunLight.setDiffuseColor(1.0f, 1.0f, 1.0f);
        sunLight.setSpecularColor(1.0f, 1.0f, 1.0f);
        sunLight.setCastShadow(true);

        terrainManager = new TerrainManager(engine.getSceneManager(), engine.getGraphicsDevice(), engine.getAssetManager());
        Shader terrainShader = engine.getAssetManager().loadShaderFile("terrain/terrain.vs.glsl", "terrain/terrain.gs.glsl", "terrain/terrain.fs.glsl");
        terrainShader.addUniformBuffer("Camera", engine.getRenderManager().getDefaultCameraUniformBuffer());
        terrainShader.addUniformBuffer("Entity", engine.getRenderManager().getDefaultEntityUniformBuffer());
        terrainShader.addUniformBuffer("Material", engine.getRenderManager().getDefaultMaterialUniformBuffer());
        TerrainRenderStage terrainRenderStage = new TerrainRenderStage(
                engine.getAssetManager().loadTextureFile("heightmap.png", FormatType.R_16_UNORM),
                engine.getAssetManager().loadTextureFile("normalmap.png"),
                terrainManager, terrainShader,
                engine.getRenderManager().getDefaultGeometryRenderStage().getRenderTarget(),
                engine.getGraphicsContext());
        engine.getRenderManager().getDefaultShadowRenderStage().addInputRenderStage(terrainRenderStage);

        Sampler dudvSampler = engine.getGraphicsDevice().createSampler();
        dudvSampler.setEdge(EdgeType.MIRROR);
        dudvSampler.setMipmapping(true);
        Texture dudvTexture = engine.getAssetManager().loadTextureFile("dudv.png");
        dudvTexture.setSampler(dudvSampler);
        dudvTexture.setMipmapping(true);

        waterManager = new WaterManager(engine.getSceneManager(), engine.getGraphicsDevice());
        Shader waterShader = engine.getAssetManager().loadShaderFile("water/water.vs.glsl", "water/water.fs.glsl");
        waterShader.addUniformBuffer("Camera", engine.getRenderManager().getDefaultCameraUniformBuffer());
        waterShader.addUniformBuffer("Entity", engine.getRenderManager().getDefaultEntityUniformBuffer());
        waterShader.addUniformBuffer("Skybox", engine.getRenderManager().getDefaultSkyboxUniformBuffer());
        waterShader.addTexture("dudvTexture", dudvTexture);
        WaterRenderStage waterRenderStage = (WaterRenderStage) engine.getRenderManager().createRenderStage(waterShader,
                (shader, renderTarget, graphicsContext) -> new WaterRenderStage(waterManager, shader, renderTarget, graphicsContext));
        waterRenderStage.addInputRenderStage(engine.getRenderManager().getDefaultLightingRenderStage());

        Shader underwaterShader = engine.getAssetManager().loadShaderFile("fx/fx.vs.glsl", "water/underwater.fs.glsl");
        underwaterShader.addUniformBuffer("Camera", engine.getRenderManager().getDefaultCameraUniformBuffer());
        underwaterShader.addUniformBuffer("Skybox", engine.getRenderManager().getDefaultSkyboxUniformBuffer());
        underwaterShader.addTexture("dudvTexture", dudvTexture);
        UnderwaterRenderStage underwaterRenderStage = (UnderwaterRenderStage) engine.getRenderManager().createFXRenderStage(underwaterShader, UnderwaterRenderStage::new);
        underwaterRenderStage.addInputRenderStage(engine.getRenderManager().getDefaultLightingRenderStage());

        Shader combineShader = engine.getAssetManager().loadShaderFile("fx/fx.vs.glsl", "fx/combine.fs.glsl");
        CombineRenderStage combineRenderStage = (CombineRenderStage)engine.getRenderManager().createFXRenderStage(combineShader, CombineRenderStage::new);
        combineRenderStage.addInputRenderStage(waterRenderStage);
        combineRenderStage.addInputRenderStage(underwaterRenderStage);
        combineRenderStage.setSecondDepthTexture(engine.getRenderManager().getDefaultGeometryRenderStage().getDepthTextureOutput());

        engine.getRenderManager().getDefaultFXAARenderStage().setInputRenderStage(0, combineRenderStage);

        initializeInput(engine);
    }

    private void initializeInput(Engine engine) {
        InputController mouse = engine.getInputManager().getMouseController();
        InputController keyboard = engine.getInputManager().getKeyboardController();

        // Exit - ESC
        engine.getInputManager().addAction(keyboard, Key.ESCAPE, InputActionType.PRESS, new CloseWindowAction(engine.getWindow()));

        // Fullscreen - F1
        engine.getInputManager().addAction(keyboard, Key.F1, InputActionType.PRESS, ((deltaTime, event) -> {
        	engine.getWindow().setFullscreen(!engine.getWindow().isFullscreen());
        }));

        // Toggle Wireframe - F2
        engine.getInputManager().addAction(keyboard, Key.F2, InputActionType.PRESS, ((deltaTime, event) -> {
        	if(terrainManager.getModel().getMesh(0).getRasterizer().getFillType() == FillType.SOLID) {
        		terrainManager.getModel().getMesh(0).getRasterizer().setFillType(FillType.WIREFRAME);
        	}
        	else {
        		terrainManager.getModel().getMesh(0).getRasterizer().setFillType(FillType.SOLID);        		
        	}
        }));

        // Sun Position
        engine.getInputManager().addAction(keyboard, Key.LEFT, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.yawGlobal(2.0f * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.RIGHT, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.yawGlobal(-2.0f * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.UP, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.pitch(-2.0f * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.DOWN, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.pitch(2.0f * deltaTime)));

        // Camera
        Camera defaultCamera = engine.getSceneManager().getDefaultCamera();
        TPCameraController cameraController = new TPCameraController(defaultCamera, engine.getWindow());
        engine.getSceneManager().addController(cameraController);
        cameraController.addNode(player);
        cameraController.setLook(false);
        cameraController.setRadius(4.0f);
        cameraController.setAzimuth(Angle.fromDegrees(180.0f));
        cameraController.setElevation(Angle.fromDegrees(30.0f));

        engine.getInputManager().addAction(mouse, Axis.LEFT_X, InputActionType.NONE, cameraController.createAzimuthAction(-0.5f));
        engine.getInputManager().addAction(mouse, Axis.LEFT_Y, InputActionType.NONE, cameraController.createElevationAction(0.5f));
        engine.getInputManager().addAction(mouse, Axis.RIGHT_Y, InputActionType.NONE, cameraController.createRadiusAction(-32.0f));
        engine.getInputManager().addAction(mouse, Button.RIGHT, InputActionType.PRESS_AND_RELEASE, cameraController.createToggleLookAction());
        engine.getInputManager().addAction(keyboard, Key.W, InputActionType.REPEAT, cameraController.createForwardAction(-16.0f));
        engine.getInputManager().addAction(keyboard, Key.S, InputActionType.REPEAT, cameraController.createForwardAction(16.0f));
        engine.getInputManager().addAction(keyboard, Key.A, InputActionType.REPEAT, cameraController.createRightAction(16.0f));
        engine.getInputManager().addAction(keyboard, Key.D, InputActionType.REPEAT, cameraController.createRightAction(-16.0f));
//        engine.getInputManager().addAction(keyboard, Key.A, InputActionType.REPEAT, cameraController.createYawAction(2.0f));
//        engine.getInputManager().addAction(keyboard, Key.D, InputActionType.REPEAT, cameraController.createYawAction(-2.0f));
        engine.getInputManager().addAction(keyboard, Key.SPACE, InputActionType.REPEAT, cameraController.createUpAction(16.0f));
        engine.getInputManager().addAction(keyboard, Key.LSHIFT, InputActionType.REPEAT, cameraController.createUpAction(-16.0f));
    }

    @Override
    public void destroy(Engine engine) {
        if(multiplayer) {
            if(connected) {
                client.sendLeaveMessage();
            }
            try {
                client.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Engine engine, float deltaTime) {
        if(multiplayer) {
            if(player.isLocalUpdated()) {
                client.sendUpdateMessage(player);
            }
            client.processPackets();
        }

        engine.getRenderManager().getDefaultLightingRenderStage().setSunPosition(sunLight.getDirection());

        terrainManager.update(false);
        terrainManager.update(false);

        monitor.update(deltaTime);
    }

    @Override
    public void render(Engine engine) {
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public boolean isMultiplayer() {
        return multiplayer;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public static void main(String[] args) {
        new GLFWBootstrap("Bigger Fish", 1600, 900).setSamples(2).run(engine -> new BiggerFishGame(engine, args));
    }
}
