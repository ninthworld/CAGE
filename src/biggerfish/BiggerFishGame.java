package biggerfish;

import biggerfish.ai.AIFishEntity;
import biggerfish.ai.AIManager;
import biggerfish.audio.Audio;
import biggerfish.audio.AudioManager;
import biggerfish.fish.FishEntity;
import biggerfish.fish.FishManager;
import biggerfish.gui.FPSMonitor;
import biggerfish.networking.BiggerFishClient;
import biggerfish.networking.PlayerEntity;
import biggerfish.physics.PhysicsEntity;
import biggerfish.terrain.EnvironmentRenderStage;
import biggerfish.terrain.TerrainManager;
import biggerfish.terrain.TerrainRenderStage;
import biggerfish.water.CombineRenderStage;
import biggerfish.water.UnderwaterRenderStage;
import biggerfish.water.WaterManager;
import biggerfish.water.WaterRenderStage;
import cage.core.engine.Engine;

import cage.core.application.Game;
import cage.core.graphics.blender.Blender;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.type.*;
import cage.core.gui.component.GUIComponent;
import cage.core.gui.graphics.GUIFont;
import cage.core.gui.graphics.GUIGraphics;
import cage.core.gui.graphics.GUIImage;
import cage.core.gui.graphics.TextAlign;
import cage.core.input.ActionState;
import cage.core.input.action.CloseWindowAction;
import cage.core.input.action.InputAction;
import cage.core.input.action.InputEvent;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.Key;
import cage.core.input.controller.InputController;
import cage.core.input.controller.JoystickController;
import cage.core.input.type.InputActionType;
import cage.core.model.ExtModel;
import cage.core.model.Model;
import cage.core.scene.InstancedSceneEntity;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import cage.core.scene.controller.RotationController;
import cage.core.scene.light.DirectionalLight;
import cage.core.scene.light.Light;
import cage.core.scene.light.PointLight;
import cage.core.scene.light.type.AttenuationType;
import cage.core.utils.math.AABB;
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import cage.glfw.GLFWBootstrap;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.ContactSolverInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import org.joml.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import ray.networking.IGameConnection;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.net.InetAddress;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.*;

public class BiggerFishGame implements Game {

    // Multiplayer
    private static final float MP_TICK_RATE = 1.0f / 20.0f;
    private BiggerFishClient client;
    private boolean multiplayer;
    private boolean connected;
    private boolean host;

    // Audio
    private Audio ambientAudio;
    private Audio biteAudio;
    private Audio dieAudio;

    // Textures
    private Texture2D sandColorTexture;
    private Texture2D sandSpecularTexture;
    private Texture2D sandNormalTexture;
    private Texture2D rockColorTexture;
    private Texture2D rockSpecularTexture;
    private Texture2D rockNormalTexture;
    private Texture2D terrainHeightTexture;
    private Texture2D terrainNormalTexture;
    private Texture2D noiseTexture;
    private Texture2D dudvTexture;

    // Texture Data
    private int terrainWidth;
    private int terrainHeight;
    private float[] terrainHeightData;
    
    // Models
    private List<ExtModel> animationModels;
    private ExtModel kelpModel;
    private Model coralModel;
    private Model rock1Model;
    private Model rock2Model;
    private Model grassModel;

    // Shaders
    private Shader terrainShader;
    private Shader grassShader;
    private Shader waterShader;
    private Shader underwaterShader;
    private Shader combineShader;
    
    // Managers
    private TerrainManager terrainManager;
    private WaterManager waterManager;
    private AIManager aiManager;
    private FishManager fishManager;
    private ScriptEngine jsEngine;
    private AudioManager audioManager;

    // Physics
    private DiscreteDynamicsWorld dynamicsWorld;
    private PlayerEntity player;

    // Other
    private DirectionalLight sunLight;
    private FPSMonitor monitor;
    private Random random = new Random(987654321098765432L);

    public BiggerFishGame(Engine engine, String[] args) {
    	this.jsEngine = new ScriptEngineManager().getEngineByName("js");
        this.monitor = new FPSMonitor(engine.createTimer());
        this.animationModels = new ArrayList<>();
        this.audioManager = new AudioManager();

        // Initialize Multiplayer
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
        // Exit - ESC
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.ESCAPE, InputActionType.PRESS, new CloseWindowAction(engine.getWindow()));

        // Fullscreen - F1
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.F1, InputActionType.PRESS, ((deltaTime, event) -> {
            engine.getWindow().setFullscreen(!engine.getWindow().isFullscreen());
        }));

        // Toggle FPS Monitor - F2
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.F2, InputActionType.PRESS, ((deltaTime, event) -> {
            monitor.setEnabled(!monitor.isEnabled());
        }));

		try {
			FileReader fileReader = new FileReader(Paths.get("assets/scripts/script.js").toFile());
			jsEngine.eval(fileReader);
			fileReader.close();
		} catch (ScriptException | IOException e) {
			e.printStackTrace();
		}

        this.fishManager = new FishManager(engine.getAssetManager(), this.animationModels);
        initializeTexturesAndModels(engine);
        initializeShaders(engine);
        
		try {
			((Invocable)jsEngine).invokeFunction("initialize", engine);
        } catch (ScriptException | NoSuchMethodException e) {
			e.printStackTrace();
		}

		// Initialize Audio
        ambientAudio = audioManager.createAudio("ambient.ogg");
        ambientAudio.setLooped(true);
        ambientAudio.play();
        biteAudio = audioManager.createAudio("bite.ogg");
        dieAudio = audioManager.createAudio("die.ogg");

		
		// Initialize Sun
        sunLight = engine.getSceneManager().getRootSceneNode().createDirectionalLight();
        sunLight.pitch(Angle.fromDegrees(-135.0f));
        sunLight.yawLocal(Angle.fromDegrees(45.0f));
        sunLight.setDiffuseColor(1.0f, 1.0f, 1.0f);
        sunLight.setSpecularColor(1.0f, 1.0f, 1.0f);
        sunLight.setCastShadow(true);
        
        // Initialize Terrain
        terrainManager = new TerrainManager(engine.getSceneManager(), engine.getGraphicsDevice(), engine.getAssetManager());
        TerrainRenderStage terrainRenderStage = new TerrainRenderStage(
                terrainManager, terrainShader,
                engine.getRenderManager().getDefaultGeometryRenderStage().getRenderTarget(),
                engine.getGraphicsContext());
        engine.getRenderManager().getDefaultShadowRenderStage().addInputRenderStage(terrainRenderStage);

        // Initialize Grass
        EnvironmentRenderStage grassRenderStage = new EnvironmentRenderStage(
        		engine.getSceneManager().getDefaultCamera(), 
        		grassModel, grassShader,
                engine.getRenderManager().getDefaultGeometryRenderStage().getRenderTarget(),
                engine.getGraphicsContext());
        terrainRenderStage.addInputRenderStage(grassRenderStage);

        // Initialize Water Surface
        waterManager = new WaterManager(engine.getSceneManager(), engine.getGraphicsDevice());
        WaterRenderStage waterRenderStage = (WaterRenderStage) engine.getRenderManager().createRenderStage(waterShader,
                (shader, renderTarget, graphicsContext) -> new WaterRenderStage(waterManager, shader, renderTarget, graphicsContext));
        waterRenderStage.addInputRenderStage(engine.getRenderManager().getDefaultLightingRenderStage());

        // Initialize Underwater
        UnderwaterRenderStage underwaterRenderStage = (UnderwaterRenderStage) engine.getRenderManager().createFXRenderStage(underwaterShader, UnderwaterRenderStage::new);
        underwaterRenderStage.addInputRenderStage(engine.getRenderManager().getDefaultLightingRenderStage());

        // Initialize Final Combine
        CombineRenderStage combineRenderStage = (CombineRenderStage)engine.getRenderManager().createFXRenderStage(combineShader, CombineRenderStage::new);
        combineRenderStage.addInputRenderStage(waterRenderStage);
        combineRenderStage.addInputRenderStage(underwaterRenderStage);
        combineRenderStage.setSecondDepthTexture(engine.getRenderManager().getDefaultGeometryRenderStage().getDepthTextureOutput());
        engine.getRenderManager().getDefaultFXAARenderStage().setInputRenderStage(0, combineRenderStage);

        // Initialize Physics World
        CollisionConfiguration config = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(config);
        DbvtBroadphase broadphase = new DbvtBroadphase();
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        this.dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, config);
        this.dynamicsWorld.setGravity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));

        // Initialize AI
        this.aiManager = new AIManager(engine.getSceneManager(), this.fishManager, this.dynamicsWorld, this);

        initializePhysicsBounds(engine);
        initializeInstancedClutter(engine);

        engine.getAssetManager().loadFontFile("Arial Black", "ariblk.ttf");
        engine.getGUIManager().getRootContainer().addComponent(monitor);

        initializeSelectModel(engine);
    }

    private void initializeTexturesAndModels(Engine engine) {
        Sampler smoothWrapSampler = engine.getGraphicsDevice().createSampler();
        smoothWrapSampler.setMipmapping(true);
        smoothWrapSampler.setEdge(EdgeType.WRAP);

        Sampler smoothMirrorSampler = engine.getGraphicsDevice().createSampler();
        smoothMirrorSampler.setEdge(EdgeType.MIRROR);
        smoothMirrorSampler.setMipmapping(true);

        Rasterizer noCullRasterizer = engine.getGraphicsDevice().createRasterizer();
        noCullRasterizer.setCullType(CullType.NONE);

        Texture2D kelpColorTexture = engine.getAssetManager().loadTextureFile("kelp/kelp_color.png");
        kelpColorTexture.setSampler(smoothWrapSampler);

        Texture2D coralColorTexture = engine.getAssetManager().loadTextureFile("coral/coral_color.png");
        Texture2D coralSpecularTexture = engine.getAssetManager().loadTextureFile("coral/coral_spec.png");
        Texture2D coralNormalTexture = engine.getAssetManager().loadTextureFile("coral/coral_norm.png");
        coralColorTexture.setSampler(smoothWrapSampler);
        coralSpecularTexture.setSampler(smoothWrapSampler);
        coralNormalTexture.setSampler(smoothWrapSampler);

        rockColorTexture = engine.getAssetManager().loadTextureFile("rock/rock_color.jpg");
        rockSpecularTexture = engine.getAssetManager().loadTextureFile("rock/rock_spec.jpg");
        rockNormalTexture = engine.getAssetManager().loadTextureFile("rock/rock_norm.jpg");
        rockColorTexture.setSampler(smoothWrapSampler);
        rockSpecularTexture.setSampler(smoothWrapSampler);
        rockNormalTexture.setSampler(smoothWrapSampler);

        sandColorTexture = engine.getAssetManager().loadTextureFile("sand/sand_color.png");
        sandSpecularTexture = engine.getAssetManager().loadTextureFile("sand/sand_spec.png");
        sandNormalTexture = engine.getAssetManager().loadTextureFile("sand/sand_norm.png");
        sandColorTexture.setSampler(smoothWrapSampler);
        sandSpecularTexture.setSampler(smoothWrapSampler);
        sandNormalTexture.setSampler(smoothWrapSampler);

        terrainHeightTexture = engine.getAssetManager().loadTextureFile("heightmap.png", FormatType.R_16_UNORM);
        terrainNormalTexture = engine.getAssetManager().loadTextureFile("normalmap.png");
        terrainHeightTexture.setSampler(smoothWrapSampler);
        terrainNormalTexture.setSampler(smoothWrapSampler);

        noiseTexture = engine.getAssetManager().loadTextureFile("noise_color.png");
        noiseTexture.setSampler(smoothWrapSampler);

        dudvTexture = engine.getAssetManager().loadTextureFile("dudv.png");
        dudvTexture.setSampler(smoothMirrorSampler);

        kelpModel = engine.getAssetManager().loadColladaModelFile("kelp/kelp.dae");
        kelpModel.getMesh(0).setRasterizer(noCullRasterizer);
        kelpModel.getMesh(0).getMaterial().setDiffuse(1.0f, 1.0f, 1.0f);
        kelpModel.getMesh(0).getMaterial().setSpecular(0.1f, 0.1f, 0.1f);
        kelpModel.getMesh(0).getMaterial().setShininess(8.0f);
        kelpModel.getMesh(0).getMaterial().setDiffuse(kelpColorTexture);
        kelpModel.setAnimationSpeed(0.05f);
        animationModels.add(kelpModel);

        coralModel = engine.getAssetManager().loadOBJModelFile("coral/coral.obj");
        coralModel.getMesh(0).setRasterizer(noCullRasterizer);
        coralModel.getMesh(0).getMaterial().setNormal(coralNormalTexture);

        rock1Model = engine.getAssetManager().loadOBJModelFile("rock/rock1.obj");
        rock1Model.getMesh(0).getMaterial().setNormal(rockNormalTexture);

        rock2Model = engine.getAssetManager().loadOBJModelFile("rock/rock2.obj");
        rock2Model.getMesh(0).getMaterial().setNormal(rockNormalTexture);

        grassModel = engine.getAssetManager().loadOBJModelFile("kelp/kelp_grass.obj");
        grassModel.getMesh(0).setRasterizer(noCullRasterizer);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            ShortBuffer data = STBImage.stbi_load_16(Paths.get("assets/textures/heightmap.png").toString(), w, h, comp, 1);
            terrainWidth = w.get();
            terrainHeight = h.get();
            terrainHeightData = new float[terrainWidth * terrainHeight];
            for(int y = 0; y < terrainHeight; ++y) {
                for(int x = 0; x < terrainWidth; ++x) {
                    int index = y * terrainWidth + x;
                    terrainHeightData[index] = (data.get(index) & 0xffff) / (float)Short.MAX_VALUE - 1.0f;
                }
            }
        }
    }

    private void initializeShaders(Engine engine) {
        terrainShader = engine.getAssetManager().loadShaderFile("terrain/terrain.vs.glsl", "terrain/terrain.gs.glsl", "terrain/terrain.fs.glsl");
        terrainShader.addUniformBuffer("Camera", engine.getRenderManager().getDefaultCameraUniformBuffer());
        terrainShader.addUniformBuffer("Entity", engine.getRenderManager().getDefaultEntityUniformBuffer());
        terrainShader.addTexture("terrainHeightTexture", terrainHeightTexture);
        terrainShader.addTexture("terrainNormalTexture", terrainNormalTexture);
        terrainShader.addTexture("diffuseTexture0", sandColorTexture);
        terrainShader.addTexture("specularTexture0", sandSpecularTexture);
        terrainShader.addTexture("normalTexture0", sandNormalTexture);
        terrainShader.addTexture("diffuseTexture1", rockColorTexture);
        terrainShader.addTexture("specularTexture1", rockSpecularTexture);
        terrainShader.addTexture("normalTexture1", rockNormalTexture);

        grassShader = engine.getAssetManager().loadShaderFile("environment/environment.vs.glsl", "geometry/material.gs.glsl", "geometry/material.fs.glsl");
        grassShader.addUniformBuffer("Camera", engine.getRenderManager().getDefaultCameraUniformBuffer());
        grassShader.addUniformBuffer("Material", engine.getRenderManager().getDefaultMaterialUniformBuffer());
        grassShader.addUniformBuffer("Skybox", engine.getRenderManager().getDefaultSkyboxUniformBuffer());
        grassShader.addTexture("terrainHeightTexture", terrainHeightTexture);
        grassShader.addTexture("terrainNormalTexture", terrainNormalTexture);
        grassShader.addTexture("noiseTexture", noiseTexture);

        waterShader = engine.getAssetManager().loadShaderFile("water/water.vs.glsl", "water/water.fs.glsl");
        waterShader.addUniformBuffer("Camera", engine.getRenderManager().getDefaultCameraUniformBuffer());
        waterShader.addUniformBuffer("Entity", engine.getRenderManager().getDefaultEntityUniformBuffer());
        waterShader.addUniformBuffer("Skybox", engine.getRenderManager().getDefaultSkyboxUniformBuffer());
        waterShader.addTexture("dudvTexture", dudvTexture);

        underwaterShader = engine.getAssetManager().loadShaderFile("fx/fx.vs.glsl", "water/underwater.fs.glsl");
        underwaterShader.addUniformBuffer("Camera", engine.getRenderManager().getDefaultCameraUniformBuffer());
        underwaterShader.addUniformBuffer("Skybox", engine.getRenderManager().getDefaultSkyboxUniformBuffer());
        underwaterShader.addTexture("dudvTexture", dudvTexture);

        combineShader = engine.getAssetManager().loadShaderFile("fx/fx.vs.glsl", "fx/combine.fs.glsl");
    }

    private void initializePhysicsBounds(Engine engine) {
        HeightfieldTerrainShape terrainShape = new HeightfieldTerrainShape(terrainWidth, terrainHeight, terrainHeightData, 128.0f, -128.0f, 128.0f, 1, false);
        PhysicsEntity terrainEntity = new PhysicsEntity(engine.getSceneManager(), null, null, new Vector3f(0, 0, 0), terrainShape, null);
        terrainEntity.getRigidBody().setMassProps(0.0f, new javax.vecmath.Vector3f());
        dynamicsWorld.addRigidBody(terrainEntity.getRigidBody(), (short)0b001, (short)0b100);

        CollisionShape waterShape = new StaticPlaneShape(new javax.vecmath.Vector3f(0, -1, 0), 0);
        PhysicsEntity waterEntity = new PhysicsEntity(engine.getSceneManager(), null, null, new Vector3f(0, 128.0f + 16.0f, 0), waterShape, null);
        waterEntity.getRigidBody().setMassProps(0.0f, new javax.vecmath.Vector3f());
        dynamicsWorld.addRigidBody(waterEntity.getRigidBody(), (short)0b001, (short)0b100);

        float margin = 256.0f;
        CollisionShape wallFrontShape = new StaticPlaneShape(new javax.vecmath.Vector3f(0, 0, -1), 0);
        PhysicsEntity wallFrontEntity = new PhysicsEntity(engine.getSceneManager(), null, null, new Vector3f(0, 0, terrainWidth / 2.0f - margin), wallFrontShape, null);
        wallFrontEntity.getRigidBody().setMassProps(0.0f, new javax.vecmath.Vector3f());
        dynamicsWorld.addRigidBody(wallFrontEntity.getRigidBody(), (short)0b001, (short)0b100);

        CollisionShape wallBackShape = new StaticPlaneShape(new javax.vecmath.Vector3f(0, 0, 1), 0);
        PhysicsEntity wallBackEntity = new PhysicsEntity(engine.getSceneManager(), null, null, new Vector3f(0, 0, -terrainWidth / 2.0f + margin), wallBackShape, null);
        wallBackEntity.getRigidBody().setMassProps(0.0f, new javax.vecmath.Vector3f());
        dynamicsWorld.addRigidBody(wallBackEntity.getRigidBody(), (short)0b001, (short)0b100);

        CollisionShape wallRightShape = new StaticPlaneShape(new javax.vecmath.Vector3f(-1, 0, 0), 0);
        PhysicsEntity wallRightEntity = new PhysicsEntity(engine.getSceneManager(), null, null, new Vector3f(terrainWidth / 2.0f - margin, 0, 0), wallRightShape, null);
        wallRightEntity.getRigidBody().setMassProps(0.0f, new javax.vecmath.Vector3f());
        dynamicsWorld.addRigidBody(wallRightEntity.getRigidBody(), (short)0b001, (short)0b100);

        CollisionShape wallLeftShape = new StaticPlaneShape(new javax.vecmath.Vector3f(1, 0, 0), 0);
        PhysicsEntity wallLeftEntity = new PhysicsEntity(engine.getSceneManager(), null, null, new Vector3f(-terrainWidth / 2.0f + margin, 0, 0), wallLeftShape, null);
        wallLeftEntity.getRigidBody().setMassProps(0.0f, new javax.vecmath.Vector3f());
        dynamicsWorld.addRigidBody(wallLeftEntity.getRigidBody(), (short)0b001, (short)0b100);
    }

    private void initializeInstancedClutter(Engine engine) {
        final int numInstanceGroups = 8;
        for(int i=0; i<numInstanceGroups; ++i) {
            for(int j=0; j<numInstanceGroups; ++j) {
                int xi = i * terrainWidth / numInstanceGroups - terrainWidth / 2;
                int zi = j * terrainHeight / numInstanceGroups - terrainHeight / 2;
                AABB bounds = new AABB(
                        new Vector3f(xi, -128.0f, zi),
                        new Vector3f(xi + terrainWidth / numInstanceGroups, 128.0f, zi + terrainHeight / numInstanceGroups));

                InstancedSceneEntity kelpInstance = new InstancedSceneEntity(engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), kelpModel);
                kelpInstance.setLocalBounds(bounds);
                Matrix4f[] kelpInstances = new Matrix4f[random.nextInt(64) + 64];
                for(int k=0; k<kelpInstances.length; ++k) {
                    int x = xi + random.nextInt(terrainWidth / numInstanceGroups);
                    int z = zi + random.nextInt(terrainHeight / numInstanceGroups);
                    int index = (z + terrainHeight / 2) * terrainWidth + (x + terrainWidth / 2);
                    float y = terrainHeightData[index] * 128.0f;
                    kelpInstances[k] = new Matrix4f().identity();
                    kelpInstances[k].translate(new Vector3f(x, y, z));
                    kelpInstances[k].scale(random.nextFloat() * 0.4f + 0.8f);
                    kelpInstances[k].rotate(Angle.fromRadians(random.nextFloat() * (float)Math.PI * 2.0f), Direction.UP);
                }
                kelpInstance.setInstanceBuffer(kelpInstances);

                SceneNode coralLightNode = engine.getSceneManager().getRootSceneNode().createSceneNode();
                engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.F3, InputActionType.PRESS, (deltaTime, event) -> coralLightNode.getNodeIterator().forEachRemaining((node -> node.setEnabled(!node.isEnabled()))));
                InstancedSceneEntity coralInstance = new InstancedSceneEntity(engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), coralModel);
                coralInstance.setLocalBounds(bounds);
                Matrix4f[] coralInstances = new Matrix4f[random.nextInt(64) + 64];
                for(int k=0; k<coralInstances.length; ++k) {
                    int x = xi + random.nextInt(terrainWidth / numInstanceGroups);
                    int z = zi + random.nextInt(terrainHeight / numInstanceGroups);
                    int index = (z + terrainHeight / 2) * terrainWidth + (x + terrainWidth / 2);
                    float y = terrainHeightData[index] * 128.0f;
                    coralInstances[k] = new Matrix4f().identity();
                    coralInstances[k].translate(new Vector3f(x, y, z));
                    coralInstances[k].scale(random.nextFloat() * 0.8f + 0.6f);
                    coralInstances[k].rotate(Angle.fromRadians(random.nextFloat() * (float)Math.PI * 2.0f), Direction.UP);
                    if(k % 25 == 0) {
                        PointLight light = coralLightNode.createPointLight();
                        light.setAttenuation(AttenuationType.LINEAR);
                        light.setRange(8.0f);
                        light.setDiffuseColor(1.0f, 0.05f, 0.6f);
                        light.setLocalPosition(x, y, z);
                    }
                }
                coralInstance.setInstanceBuffer(coralInstances);

                InstancedSceneEntity rock1Instance = new InstancedSceneEntity(engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), rock1Model);
                rock1Instance.setLocalBounds(bounds);
                Matrix4f[] rock1Instances = new Matrix4f[random.nextInt(32) + 32];
                for(int k=0; k<rock1Instances.length; ++k) {
                    int x = xi + random.nextInt(terrainWidth / numInstanceGroups);
                    int z = zi + random.nextInt(terrainHeight / numInstanceGroups);
                    int index = (z + terrainHeight / 2) * terrainWidth + (x + terrainWidth / 2);
                    float y = terrainHeightData[index] * 128.0f;

                    float scale = random.nextFloat() * 2.0f + 0.25f;

                    CollisionShape shape = new SphereShape(scale);
                    Transform transform = new Transform();
                    transform.setIdentity();
                    transform.origin.set(x, y, z);
                    DefaultMotionState motion = new DefaultMotionState(transform);
                    RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(0.0f, motion, shape);
                    RigidBody rigidBody = new RigidBody(ci);
                    rigidBody.setActivationState(0);
                    dynamicsWorld.addRigidBody(rigidBody, (short)0b010, (short)0b110);

                    rock1Instances[k] = new Matrix4f().identity();
                    rock1Instances[k].translate(new Vector3f(x, y, z));
                    rock1Instances[k].scale(scale);
                    rock1Instances[k].rotate(Angle.fromRadians(random.nextFloat() * (float)Math.PI * 2.0f), Direction.FORWARD);
                }
                rock1Instance.setInstanceBuffer(rock1Instances);

                InstancedSceneEntity rock2Instance = new InstancedSceneEntity(engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), rock2Model);
                rock2Instance.setLocalBounds(bounds);
                Matrix4f[] rock2Instances = new Matrix4f[random.nextInt(32) + 32];
                for(int k=0; k<rock2Instances.length; ++k) {
                    int x = xi + random.nextInt(terrainWidth / numInstanceGroups);
                    int z = zi + random.nextInt(terrainHeight / numInstanceGroups);
                    int index = (z + terrainHeight / 2) * terrainWidth + (x + terrainWidth / 2);
                    float y = terrainHeightData[index] * 128.0f;

                    float scale = random.nextFloat() * 2.0f + 0.25f;

                    CollisionShape shape = new SphereShape(scale);
                    Transform transform = new Transform();
                    transform.setIdentity();
                    transform.origin.set(x, y, z);
                    DefaultMotionState motion = new DefaultMotionState(transform);
                    RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(0.0f, motion, shape);
                    RigidBody rigidBody = new RigidBody(ci);
                    rigidBody.setActivationState(0);
                    dynamicsWorld.addRigidBody(rigidBody, (short)0b010, (short)0b110);

                    rock2Instances[k] = new Matrix4f().identity();
                    rock2Instances[k].translate(new Vector3f(x, y, z));
                    rock2Instances[k].scale(scale);
                    rock2Instances[k].rotate(Angle.fromRadians(random.nextFloat() * (float)Math.PI * 2.0f), Direction.FORWARD);
                }
                rock2Instance.setInstanceBuffer(rock2Instances);
            }
        }
    }

    private void initializeInput(Engine engine) {
        InputController mouse = engine.getInputManager().getMouseController();
        InputController keyboard = engine.getInputManager().getKeyboardController();

        engine.getInputManager().addAction(keyboard, Key.F4, InputActionType.PRESS, (deltaTime, event) -> respawnPlayer());

        // Sun Position
        engine.getInputManager().addAction(keyboard, Key.LEFT, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.yawLocal(2.0f * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.RIGHT, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.yawLocal(-2.0f * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.UP, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.pitch(-2.0f * deltaTime)));
        engine.getInputManager().addAction(keyboard, Key.DOWN, InputActionType.REPEAT, ((deltaTime, event) -> sunLight.pitch(2.0f * deltaTime)));

        // Camera
        Camera defaultCamera = engine.getSceneManager().getDefaultCamera();
        TPCameraController cameraController = new TPCameraController(defaultCamera, engine.getWindow());
        engine.getSceneManager().addController(cameraController);
        cameraController.addNode(player);
        cameraController.setLook(false);
        cameraController.setMaxRadius(64.0f);
        cameraController.setRadius(32.0f);
        cameraController.setAzimuth(Angle.fromDegrees(180.0f));
        cameraController.setElevation(Angle.fromDegrees(30.0f));

        engine.getInputManager().addAction(mouse, Axis.LEFT_X, InputActionType.NONE, cameraController.createAzimuthAction(-0.5f));
        engine.getInputManager().addAction(mouse, Axis.LEFT_Y, InputActionType.NONE, cameraController.createElevationAction(0.5f));
        engine.getInputManager().addAction(mouse, Axis.RIGHT_Y, InputActionType.NONE, cameraController.createRadiusAction(-64.0f));
        engine.getInputManager().addAction(mouse, Button.RIGHT, InputActionType.PRESS_AND_RELEASE, cameraController.createToggleLookAction());

        engine.getInputManager().addAction(keyboard, Key.W, InputActionType.REPEAT, cameraController.createForwardAction(1.0f));
        engine.getInputManager().addAction(keyboard, Key.S, InputActionType.REPEAT, cameraController.createForwardAction(-1.0f));
        engine.getInputManager().addAction(keyboard, Key.A, InputActionType.REPEAT, cameraController.createYawAction(1.0f));
        engine.getInputManager().addAction(keyboard, Key.D, InputActionType.REPEAT, cameraController.createYawAction(-1.0f));
        engine.getInputManager().addAction(keyboard, Key.SPACE, InputActionType.REPEAT, cameraController.createPitchAction(-1.0f));
        engine.getInputManager().addAction(keyboard, Key.LSHIFT, InputActionType.REPEAT, cameraController.createPitchAction(1.0f));

        Iterator<JoystickController> it = engine.getInputManager().getJoystickControllerIterator();
        while(it.hasNext()) {
            JoystickController joystick = it.next();

            engine.getInputManager().addAction(joystick, Axis.LEFT_X, InputActionType.NONE, cameraController.createAzimuthAction(-0.5f));
            engine.getInputManager().addAction(joystick, Axis.LEFT_Y, InputActionType.NONE, cameraController.createElevationAction(0.5f));
            engine.getInputManager().addAction(joystick, Button.DPAD_LEFT, InputActionType.REPEAT, cameraController.createRadiusAction(-1.0f));
            engine.getInputManager().addAction(joystick, Button.DPAD_RIGHT, InputActionType.REPEAT, cameraController.createRadiusAction(1.0f));
            engine.getInputManager().addAction(joystick, Button.LEFT_BUMPER, InputActionType.PRESS_AND_RELEASE, cameraController.createToggleLookAction());

            engine.getInputManager().addAction(joystick, Axis.RIGHT_Y, InputActionType.REPEAT, cameraController.createForwardAction(1.0f));
            engine.getInputManager().addAction(joystick, Axis.RIGHT_Y, InputActionType.REPEAT, cameraController.createForwardAction(-1.0f));
            engine.getInputManager().addAction(joystick, Axis.RIGHT_X, InputActionType.REPEAT, cameraController.createYawAction(1.0f));
            engine.getInputManager().addAction(joystick, Axis.RIGHT_X, InputActionType.REPEAT, cameraController.createYawAction(-1.0f));
            engine.getInputManager().addAction(joystick, Button.DPAD_UP, InputActionType.REPEAT, cameraController.createPitchAction(-1.0f));
            engine.getInputManager().addAction(joystick, Button.DPAD_DOWN, InputActionType.REPEAT, cameraController.createPitchAction(1.0f));
            break;
        }
    }

    private int selectedModel = FishManager.GREATWHITE_TYPE;
    private ActionState selectMouseX;
    private ActionState selectMouseY;
    private ActionState selectButton;
    private GUIComponent selectGUI;
    private boolean playerModelSelected = false;
    public void initializeSelectModel(Engine engine) {
        engine.getSceneManager().getDefaultCamera().setLocalPosition(200.0f, -8.0f, 1000.0f);
        engine.getSceneManager().getDefaultCamera().setLocalRotation(new Matrix3f().identity());

        SceneEntity greatWhiteEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(fishManager.getGreatWhiteModel());
        SceneEntity hammerheadEntity = engine.getSceneManager().getRootSceneNode().createSceneEntity(fishManager.getHammerheadModel());

        final float largeScale = 0.06f;
        final float smallScale = 0.04f;
        greatWhiteEntity.scale(largeScale);
        hammerheadEntity.scale(smallScale);

        greatWhiteEntity.setLocalPosition(engine.getSceneManager().getDefaultCamera().getLocalPosition().add(-0.45f, 0.0f, -1.0f, new Vector3f()));
        hammerheadEntity.setLocalPosition(engine.getSceneManager().getDefaultCamera().getLocalPosition().add(0.45f, 0.0f, -1.0f, new Vector3f()));

        RotationController rotationController = new RotationController(0.5f, Direction.UP);
        engine.getSceneManager().addController(rotationController);
        rotationController.addNode(greatWhiteEntity);
        rotationController.addNode(hammerheadEntity);

        selectGUI = new GUIComponent() {
            @Override
            public void render(GUIGraphics g) {
                g.setFont("Arial Black");
                g.setTextAlign(TextAlign.CENTER, TextAlign.MIDDLE);

                float w = engine.getWindow().getWidth() / 4.0f;
                float h = engine.getWindow().getHeight() / 4.0f - 32.0f;

                g.setFill(0.0f, 0.0f, 0.0f, 0.5f);
                g.beginPath();
                g.rect(0.0f, h - 32.0f, engine.getWindow().getWidth(), 80.0f);
                g.closePath();
                g.fill();

                float mod = (selectedModel == FishManager.GREATWHITE_TYPE ? 1.0f : 3.0f);
                g.setFill(1.0f, 1.0f, 1.0f, 1.0f);
                g.beginPath();
                g.moveTo(w * mod, h - 64.0f);
                g.lineTo(w * mod + 24.0f, h - 64.0f - 32.0f);
                g.lineTo(w * mod - 24.0f, h - 64.0f - 32.0f);
                g.closePath();
                g.fill();

                g.setFill(1.0f, 1.0f, 1.0f, 1.0f);
                g.setFontSize(48);
                g.drawText(w, h, "GREAT WHITE SHARK");
                g.drawText(w * 3.0f, h, "HAMMERHEAD SHARK");

                g.setFill(1.0f, 0.1f, 0.0f, 1.0f);
                g.setFontSize(24);
                g.drawText(w, h + 32.0f, "+20% EFFICIENCY");

                g.setFill(0.0f, 1.0f, 0.0f, 1.0f);
                g.setFontSize(24);
                g.drawText(w * 3.0f, h + 32.0f, "+20% SPEED");
            }
        };
        engine.getGUIManager().getRootContainer().addComponent(selectGUI);

        InputAction moveAction = (deltaTime, event) -> {
            Vector2f pos = engine.getWindow().getMousePosition();
            if(pos.x > engine.getWindow().getWidth() / 2.0f) {
                selectedModel = FishManager.HAMMERHEAD_TYPE;
            }
            else {
                selectedModel = FishManager.GREATWHITE_TYPE;
            }

            if(selectedModel == FishManager.GREATWHITE_TYPE) {
                greatWhiteEntity.setLocalScale(largeScale, largeScale, largeScale);
                hammerheadEntity.setLocalScale(smallScale, smallScale, smallScale);
            }
            else if(selectedModel == FishManager.HAMMERHEAD_TYPE) {
                greatWhiteEntity.setLocalScale(smallScale, smallScale, smallScale);
                hammerheadEntity.setLocalScale(largeScale, largeScale, largeScale);
            }
        };

        InputController mouse = engine.getInputManager().getMouseController();
        selectMouseX = engine.getInputManager().addAction(mouse, Axis.LEFT_X, InputActionType.NONE, moveAction);
        selectMouseY = engine.getInputManager().addAction(mouse, Axis.LEFT_Y, InputActionType.NONE, moveAction);
        selectButton = engine.getInputManager().addAction(mouse, Button.LEFT, InputActionType.RELEASE, (deltaTime, event) -> {
            greatWhiteEntity.destroy();
            hammerheadEntity.destroy();
            playerModelSelected = true;
        });
    }

    public void initializePlayer(Engine engine) {
        playerModelSelected = false;
        engine.getInputManager().removeAction(selectButton);
        engine.getInputManager().removeAction(selectMouseX);
        engine.getInputManager().removeAction(selectMouseY);
        engine.getGUIManager().getRootContainer().removeComponent(selectGUI);

        player = new PlayerEntity(null, engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), fishManager.createPlayerModel(selectedModel), selectedModel, new Vector3f(), FishManager.PLAYER_MASS, dynamicsWorld);
        player.addNode(engine.getSceneManager().getDefaultCamera());
        respawnPlayer();
        if(multiplayer && connected) {
            client.sendCreatePlayerMessage(player);
        }

        initializeInput(engine);

        GUIImage weightImage = engine.getAssetManager().loadImageFile("weight.png");
        weightImage.setAlpha(0.9f);
        engine.getGUIManager().getRootContainer().addComponent(new GUIComponent() {
            @Override
            public void render(GUIGraphics g) {
                float h = engine.getWindow().getHeight();

                g.setFill(0.0f, 0.0f, 0.0f, 0.5f);
                g.beginPath();
                g.rect(0.0f, h - 72.0f, 256.0f, 72.0f);
                g.closePath();
                g.fill();

                g.setFill(weightImage);
                g.beginPath();
                weightImage.setBounds(new Rectangle(12, (int)h - 60, 48, 48));
                g.rect(12, h - 60, 48, 48);
                g.closePath();
                g.fill();

                g.setFont("Arial Black");
                g.setTextAlign(TextAlign.LEFT, TextAlign.MIDDLE);
                g.setFontSize(48);
                g.setFill(1.0f, 1.0f, 1.0f, 1.0f);
                float mass = (player.getMass() < 1.0f ? player.getMass() * 1000.0f : (player.getMass() >= 1000.0f ? player.getMass() / 1000.0f : player.getMass()));
                String unit = (player.getMass() < 1.0f ? "g" : (player.getMass() >= 1000.0f ? "t" : "kg"));
                g.drawText(80.0f, h - 36.0f, String.format("%5.1f %s", mass, unit));
            }
        });
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

        audioManager.destroy();
    }

    float accumTime = 0.0f;

    @Override
    public void update(Engine engine, float deltaTime) {
        if(playerModelSelected) {
            initializePlayer(engine);
        }

        // Physics Simulation
        dynamicsWorld.stepSimulation(1.0f / 60.0f);

        List<FishEntity> eaten = new ArrayList<>();
        Dispatcher dispatcher = dynamicsWorld.getDispatcher();
        for(int i=0; i<dispatcher.getNumManifolds(); ++i) {
            PersistentManifold manifold = dispatcher.getManifoldByIndexInternal(i);
            RigidBody object1 = (RigidBody)manifold.getBody0();
            if(object1.getUserPointer() instanceof FishEntity) {
                FishEntity fish = (FishEntity) object1.getUserPointer();
                RigidBody object2 = (RigidBody)manifold.getBody1();
                if(player != null && object2.getUserPointer() == player) {
                    for(int j=0; j<manifold.getNumContacts(); ++j) {
                        ManifoldPoint point = manifold.getContactPoint(j);
                        if(point.getDistance() < 0.0f) {
                            eaten.add(fish);
                            break;
                        }
                    }
                }
            }
        }

        Iterator<FishEntity> itEat = eaten.iterator();
        while(player != null && itEat.hasNext()) {
            FishEntity fish = itEat.next();
            if(fish instanceof AIFishEntity) {
                if(fish.getMass() > player.getMass()) {
                    respawnPlayer();
                    dieAudio.play();
                }
                else if(fish.getMass() < player.getMass()) {
                    aiManager.eatFish(player, (AIFishEntity) fish);
                }
            }
            else if(fish instanceof PlayerEntity) {
                if(fish.getMass() > player.getMass()) {
                    respawnPlayer();
                    dieAudio.play();
                }
                else if(fish.getMass() < player.getMass()) {
                    if(fish.getParentNode() != null) {
                        player.setMass(player.getMass() + fish.getMass() * player.getEfficiency());
                        fish.setParentNode(null);
                        playBiteSound();
                    }
                }
            }
        }

        // AI Simulation
        aiManager.update(deltaTime);

        // Shadow Size
        Vector4f shadowRanges = new Vector4f();
        shadowRanges.x = (player != null ? player.getScale() : 0.05f) * 80.0f;
        shadowRanges.y = shadowRanges.x * 2.0f;
        shadowRanges.z = shadowRanges.y * 2.0f;
        shadowRanges.w = shadowRanges.z * 2.0f;
        engine.getRenderManager().getDefaultShadowRenderStage().setShadowRanges(shadowRanges);

        // Force Lighting Update
        Iterator<Light> it = engine.getSceneManager().getLightIterator();
        while(it.hasNext()) {
            Light light = it.next();
            light.notifyUpdate();
        }

        // Animation
        for(ExtModel model : animationModels) {
            model.update(deltaTime);
        }

        // Send Multiplayer Packets
        if(multiplayer) {
            if(connected) {
                accumTime += deltaTime;
                if (accumTime >= MP_TICK_RATE) {
                    accumTime = 0.0f;
                    if (player != null && player.isLocalUpdated()) {
                        client.sendUpdatePlayerMessage(player);
                    }
                }
            }
            client.processPackets();
        }

        // Sun Position
        engine.getRenderManager().getDefaultLightingRenderStage().setSunPosition(sunLight.getDirection());

        // Double-Edged Quadtree Update
        terrainManager.update(false);
        terrainManager.update(false);

        // FPS Display
        monitor.update(deltaTime);

        // Audio
        audioManager.setListenerPosition(engine.getSceneManager().getDefaultCamera().getWorldPosition());
        ambientAudio.setPosition(engine.getSceneManager().getDefaultCamera().getWorldPosition());
        if(player != null) {
            biteAudio.setPosition(player.getLocalPosition());
            dieAudio.setPosition(player.getLocalPosition());
        }
    }

    @Override
    public void render(Engine engine) {
    }

    public float getTerrainHeightAt(float x, float z) {
        int xi = Math.min(Math.max((int)Math.floor(x + terrainWidth / 2), 0), terrainWidth);
        int zi = Math.min(Math.max((int)Math.floor(z + terrainHeight / 2), 0), terrainHeight);
        return terrainHeightData[zi * terrainWidth + xi] * 128.0f;
    }

    private Random respawnRandom = new Random(System.currentTimeMillis());
    private void respawnPlayer() {
        final float radius = 8.0f;
        final Vector2f[] origins = new Vector2f[]{
            new Vector2f(-528.0f, -236.0f),
            new Vector2f(-128.0f, -616.0f),
            new Vector2f(404.0f, -692.0f)
        };

        Vector2f pos = origins[respawnRandom.nextInt(origins.length)];
        pos.add(new Vector2f(respawnRandom.nextFloat() * radius * 2.0f - radius, respawnRandom.nextFloat() * radius * 2.0f - radius));
        float y = 4.0f + getTerrainHeightAt(pos.x, pos.y);
        player.setPosition(new Vector3f(pos.x, y, pos.y));
        player.setRotation(new Matrix3f().identity());
        player.setMass(FishManager.PLAYER_MASS);
    }

    public void playBiteSound() {
        biteAudio.play();
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public DiscreteDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    public FishManager getFishManager() {
        return fishManager;
    }

    public AIManager getAIManager() {
        return aiManager;
    }

    public BiggerFishClient getClient() {
        return client;
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

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public static void main(String[] args) {
        new GLFWBootstrap("Bigger Fish", 1600, 900).setSamples(2).run(engine -> new BiggerFishGame(engine, args));
    }
}
