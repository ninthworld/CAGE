package biggerfish;

import biggerfish.gui.FPSMonitor;
import biggerfish.terrain.TerrainManager;
import biggerfish.terrain.TerrainRenderStage;
import biggerfish.water.CombineRenderStage;
import biggerfish.water.WaterManager;
import biggerfish.water.WaterRenderStage;
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
import cage.core.scene.light.DirectionalLight;
import cage.core.scene.light.Light;
import cage.core.utils.math.Angle;
import cage.core.utils.math.Direction;
import cage.glfw.GLFWBootstrap;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.Iterator;

import static org.lwjgl.system.MemoryStack.stackPush;

public class BiggerFishGame implements Game {

    private FPSMonitor monitor;
    private boolean canLook;
    private DirectionalLight sunLight;

    private TerrainManager terrainManager;
    private WaterManager waterManager;

    public BiggerFishGame(Engine engine) {
        this.monitor = new FPSMonitor(engine.createTimer());
        this.canLook = false;
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

        Shader combineShader = engine.getAssetManager().loadShaderFile("fx/fx.vs.glsl", "fx/combine.fs.glsl");
        CombineRenderStage combineRenderStage = (CombineRenderStage)engine.getRenderManager().createFXRenderStage(combineShader, CombineRenderStage::new);
        combineRenderStage.addInputRenderStage(waterRenderStage);
        combineRenderStage.addInputRenderStage(engine.getRenderManager().getDefaultLightingRenderStage());

        engine.getRenderManager().getDefaultFXAARenderStage().setInputRenderStage(0, combineRenderStage);

        engine.getAssetManager().getDefaultLightingShader().addTexture("dudvTexture", dudvTexture);

        initializeInput(engine);
    }

    private void initializeInput(Engine engine) {
        InputController mouse = engine.getInputManager().getMouseController();
        InputController keyboard = engine.getInputManager().getKeyboardController();
        
        engine.getInputManager().addAction(keyboard, Key.ESCAPE, InputActionType.PRESS, new CloseWindowAction(engine.getWindow()));
        engine.getInputManager().addAction(keyboard, Key.F1, InputActionType.PRESS, ((deltaTime, event) -> {
        	engine.getWindow().setFullscreen(!engine.getWindow().isFullscreen());
        }));
        engine.getInputManager().addAction(keyboard, Key.F2, InputActionType.PRESS, ((deltaTime, event) -> {
        	if(terrainManager.getModel().getMesh(0).getRasterizer().getFillType() == FillType.SOLID) {
        		terrainManager.getModel().getMesh(0).getRasterizer().setFillType(FillType.WIREFRAME);
        	}
        	else {
        		terrainManager.getModel().getMesh(0).getRasterizer().setFillType(FillType.SOLID);        		
        	}
        }));
        
        Camera defaultCamera = engine.getSceneManager().getDefaultCamera();
        defaultCamera.moveUp(72.0f);
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

        terrainManager.update(false);
        terrainManager.update(false);

        monitor.update(deltaTime);
    }

    @Override
    public void render(Engine engine) {
    }

    public static void main(String[] args) {
        new GLFWBootstrap("Bigger Fish", 1600, 900).setSamples(2).run(BiggerFishGame::new);
    }
}
