package cage.core.render;

import cage.core.common.listener.ResizeListener;
import cage.core.graphics.GraphicsDevice;
import cage.core.graphics.blender.Blender;
import cage.core.graphics.type.BlendOpType;
import cage.core.graphics.type.BlendType;
import cage.core.window.Window;
import cage.core.asset.AssetManager;
import cage.core.graphics.*;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.vertexarray.VertexArray;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.render.stage.*;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneManager;
import cage.core.scene.camera.Camera;
import cage.core.scene.light.Light;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.lwjgl.system.MemoryStack.stackPush;

public class RenderManager {

    public static final LayoutConfig SHADOW_READ_LAYOUT = new LayoutConfig().mat4().mat4().mat4().mat4();
    public static final int SHADOW_READ_SIZE = SHADOW_READ_LAYOUT.getUnitSize() / 4;

    public static final LayoutConfig SKYBOX_READ_LAYOUT = new LayoutConfig().float4().float4().float1().float1().float2();
    public static final int SKYBOX_READ_SIZE = SKYBOX_READ_LAYOUT.getUnitSize() / 4;

    public static final int SSAO_KERNEL_SIZE = 32;
    public static final LayoutConfig SSAO_READ_LAYOUT = new LayoutConfig().mat4().mat4().mat4().mat4().mat4().mat4().mat4().mat4();
    public static final int SSAO_READ_SIZE = SSAO_READ_LAYOUT.getUnitSize() / 4;

    private GraphicsDevice graphicsDevice;
    private GraphicsContext graphicsContext;
    private Window window;
    private SceneManager sceneManager;
    private AssetManager assetManager;
    private List<RenderStage> outputStages;
    private Model defaultFXModel;

    private GeometryRenderStage defaultGeometryRenderStage;
    private ShadowRenderStage defaultShadowRenderStage;
    private LightingRenderStage defaultLightingRenderStage;
    private FXAARenderStage defaultFXAARenderStage;
    private SSAORenderStage defaultSSAORenderStage;

    private UniformBuffer defaultWindowUniformBuffer;
    private UniformBuffer defaultCameraUniformBuffer;
    private UniformBuffer defaultEntityUniformBuffer;
    private UniformBuffer defaultMaterialUniformBuffer;
    private ShaderStorageBuffer defaultLightShaderStorageBuffer;
    private UniformBuffer defaultShadowUniformBuffer;
    private UniformBuffer defaultSkyboxUniformBuffer;
    private UniformBuffer defaultSSAOUniformBuffer;

    public RenderManager(GraphicsDevice graphicsDevice, GraphicsContext graphicsContext, Window window, SceneManager sceneManager, AssetManager assetManager) {
        this.outputStages = new ArrayList<>();
        this.graphicsDevice = graphicsDevice;
        this.graphicsContext = graphicsContext;
        this.window = window;
        this.sceneManager = sceneManager;
        this.assetManager = assetManager;
        this.defaultFXModel = createDefaultFXModel();

        defaultWindowUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultWindowUniformBuffer.setLayout(Window.READ_LAYOUT);
        defaultWindowUniformBuffer.writeData(window.readData());
        window.addListener((ResizeListener) (width, height) -> defaultWindowUniformBuffer.writeData(window.readData()));

        defaultCameraUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultCameraUniformBuffer.setLayout(Camera.READ_LAYOUT);

        defaultEntityUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultEntityUniformBuffer.setLayout(SceneEntity.READ_LAYOUT);

        defaultMaterialUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultMaterialUniformBuffer.setLayout(Material.READ_LAYOUT);

        defaultLightShaderStorageBuffer = graphicsDevice.createShaderStorageBuffer();
        defaultLightShaderStorageBuffer.setLayout(Light.READ_LAYOUT);

        defaultShadowUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultShadowUniformBuffer.setLayout(SHADOW_READ_LAYOUT);

        defaultSkyboxUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultSkyboxUniformBuffer.setLayout(SKYBOX_READ_LAYOUT);

        defaultSSAOUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultSSAOUniformBuffer.setLayout(SSAO_READ_LAYOUT);


        try(MemoryStack stack = stackPush()) {
            FloatBuffer ssaoBuffer = stack.callocFloat(SSAO_READ_SIZE);
            Random rand = new Random();
            Vector3f kernel = new Vector3f();
            for(int i=0; i<SSAO_KERNEL_SIZE; ++i) {
                kernel.x = (rand.nextFloat() / (float)Integer.MAX_VALUE) * 2.0f - 1.0f;
                kernel.y = (rand.nextFloat() / (float)Integer.MAX_VALUE) * 2.0f - 1.0f;
                kernel.z = (rand.nextFloat() / (float)Integer.MAX_VALUE);
                kernel.normalize();
                kernel.mul((float)Math.max(Math.min(Math.pow(i / 32.0f, 2), 1.0f), 0.1f));
                ssaoBuffer.put(i * 4, kernel.x).put(i * 4 + 1, kernel.y).put(i * 4 + 2, kernel.z);
            }
            ssaoBuffer.rewind();
            defaultSSAOUniformBuffer.writeData(ssaoBuffer);
        }

        assetManager.getDefaultGeometryShader().addUniformBuffer("Camera", defaultCameraUniformBuffer);
        assetManager.getDefaultGeometryShader().addUniformBuffer("Entity", defaultEntityUniformBuffer);
        assetManager.getDefaultGeometryShader().addUniformBuffer("Material", defaultMaterialUniformBuffer);

        UniformBuffer simpleCameraUniform = graphicsDevice.createUniformBuffer();
        simpleCameraUniform.setLayout(Camera.READ_LAYOUT);
        assetManager.getDefaultSimpleGeometryShader().addUniformBuffer("Camera", simpleCameraUniform);
        assetManager.getDefaultSimpleGeometryShader().addUniformBuffer("Entity", defaultEntityUniformBuffer);

        assetManager.getDefaultLightingShader().addUniformBuffer("Skybox", defaultSkyboxUniformBuffer);
        assetManager.getDefaultLightingShader().addUniformBuffer("Camera", defaultCameraUniformBuffer);
        assetManager.getDefaultLightingShader().addShaderStorageBuffer("Light", defaultLightShaderStorageBuffer);

        assetManager.getDefaultShadowShader().addUniformBuffer("Camera", defaultCameraUniformBuffer);
        assetManager.getDefaultShadowShader().addUniformBuffer("Shadow", defaultShadowUniformBuffer);

        assetManager.getDefaultFXAAShader().addUniformBuffer("Window", defaultWindowUniformBuffer);

        assetManager.getDefaultSSAOShader().addUniformBuffer("Camera", defaultCameraUniformBuffer);
        assetManager.getDefaultSSAOShader().addUniformBuffer("Window", defaultWindowUniformBuffer);
        assetManager.getDefaultSSAOShader().addUniformBuffer("SSAO", defaultSSAOUniformBuffer);

        defaultGeometryRenderStage = createGeometryRenderStage(sceneManager.getDefaultCamera());

        defaultShadowRenderStage = createShadowRenderStage();
        defaultShadowRenderStage.addInputRenderStage(defaultGeometryRenderStage);

        defaultSSAORenderStage = createSSAORenderStage();
        defaultSSAORenderStage.addInputRenderStage(defaultGeometryRenderStage);

        defaultLightingRenderStage = createLightingRenderStage();
        defaultLightingRenderStage.addInputRenderStage(defaultGeometryRenderStage);
        defaultLightingRenderStage.addInputRenderStage(defaultShadowRenderStage);
        defaultLightingRenderStage.addInputRenderStage(defaultSSAORenderStage);

        defaultFXAARenderStage = createFXAARenderStage();
        defaultFXAARenderStage.addInputRenderStage(defaultLightingRenderStage);

        addOutputRenderStage(defaultFXAARenderStage);
    }

    public void render() {
        outputStages.forEach(RenderStage::render);
        graphicsContext.bindBackBuffer();
        graphicsContext.clear();
        outputStages.forEach((RenderStage stage) -> graphicsContext.resolveToBackBuffer(stage.getRenderTarget(), new Rectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())));
        outputStages.forEach(RenderStage::postRender);
    }

    public RenderStage createRenderStage(Shader shader, RenderStageConstructor stage) {
        RenderTarget renderTarget = graphicsDevice.createRenderTarget2D();
    	RenderStage renderStage = stage.init(shader, renderTarget, graphicsContext);
    	renderStage.setSizableParent(window);
    	return renderStage;
    }

    public FXRenderStage createFXRenderStage(Shader shader, FXRenderStageConstructor stage) {
        RenderTarget renderTarget = graphicsDevice.createRenderTarget2D();
        FXRenderStage renderStage = stage.init(defaultFXModel, shader, renderTarget, graphicsContext);
        renderStage.setSizableParent(window);
        return renderStage;
    }

    public GeometryRenderStage createGeometryRenderStage(Camera camera) {
        return (GeometryRenderStage)createRenderStage(assetManager.getDefaultGeometryShader(), (shader, renderTarget, graphicsContext) -> {
            renderTarget.addColorTexture(1, graphicsDevice.createTexture2D(renderTarget.getWidth(), renderTarget.getHeight()));
            renderTarget.addColorTexture(2, graphicsDevice.createTexture2D(renderTarget.getWidth(), renderTarget.getHeight()));
            return new GeometryRenderStage(camera, sceneManager.getRootSceneNode(), shader, renderTarget, graphicsContext);
        });
    }

    public ShadowRenderStage createShadowRenderStage() {
        return (ShadowRenderStage)createRenderStage(assetManager.getDefaultShadowShader(), (shader, renderTarget, graphicsContext) -> {
            RenderTarget[] shadowRenderTargets = new RenderTarget[4];
            for(int i=0; i<shadowRenderTargets.length; ++i) {
                shadowRenderTargets[i] = graphicsDevice.createRenderTarget2D(ShadowRenderStage.SHADOW_RESOLUTION, ShadowRenderStage.SHADOW_RESOLUTION);
            }
            return new ShadowRenderStage(
                    sceneManager, defaultFXModel,
                    assetManager.getDefaultSimpleGeometryShader(), shader,
                    shadowRenderTargets, renderTarget,
                    graphicsDevice.getDefaultBlender(), graphicsContext);
        });
    }

    public LightingRenderStage createLightingRenderStage() {
        return (LightingRenderStage)createFXRenderStage(assetManager.getDefaultLightingShader(), (fxModel, shader, renderTarget, graphicsContext) -> {
            LightingRenderStage renderStage = new LightingRenderStage(sceneManager, fxModel, shader, renderTarget, graphicsContext);
            renderStage.setSkyboxTexture(graphicsDevice.getDefaultTextureCubeMap());
            return renderStage;
        });
    }

    public FXAARenderStage createFXAARenderStage() {
        return (FXAARenderStage)createFXRenderStage(assetManager.getDefaultFXAAShader(), (fxModel, shader, renderTarget, graphicsContext) ->
                new FXAARenderStage(fxModel, shader, renderTarget, graphicsContext));
    }

    public SSAORenderStage createSSAORenderStage() {
        return (SSAORenderStage)createFXRenderStage(assetManager.getDefaultSSAOShader(), (fxModel, shader, renderTarget, graphicsContext) ->
                new SSAORenderStage(assetManager.getDefaultNoiseTexture(), fxModel, shader, renderTarget, graphicsContext));
    }

    public GeometryRenderStage getDefaultGeometryRenderStage() {
        return defaultGeometryRenderStage;
    }

    public ShadowRenderStage getDefaultShadowRenderStage() {
        return defaultShadowRenderStage;
    }

    public LightingRenderStage getDefaultLightingRenderStage() {
        return defaultLightingRenderStage;
    }

    public FXAARenderStage getDefaultFXAARenderStage() {
        return defaultFXAARenderStage;
    }

    public UniformBuffer getDefaultWindowUniformBuffer() {
        return defaultWindowUniformBuffer;
    }

    public UniformBuffer getDefaultCameraUniformBuffer() {
        return defaultCameraUniformBuffer;
    }

    public UniformBuffer getDefaultEntityUniformBuffer() {
        return defaultEntityUniformBuffer;
    }

    public UniformBuffer getDefaultMaterialUniformBuffer() {
        return defaultMaterialUniformBuffer;
    }

    public ShaderStorageBuffer getDefaultLightShaderStorageBuffer() {
        return defaultLightShaderStorageBuffer;
    }

    public UniformBuffer getDefaultSkyboxUniformBuffer() {
        return defaultSkyboxUniformBuffer;
    }

    public UniformBuffer getDefaultSSAOUniformBuffer() {
        return defaultSSAOUniformBuffer;
    }

    public Model getDefaultFXModel() {
        return defaultFXModel;
    }

    public void addOutputRenderStage(RenderStage renderStage) {
        outputStages.add(renderStage);
    }

    public void removeOutputRenderStage(RenderStage renderStage) {
        outputStages.remove(renderStage);
    }

    public void removeOutputRenderStage(int index) {
        outputStages.remove(index);
    }

    public void removeAllOutputRenderStages() {
        outputStages.forEach(this::removeOutputRenderStage);
    }

    public int getOutputRenderStageCount() {
        return outputStages.size();
    }

    public boolean containsOutputRenderStage(RenderStage renderStage) {
        return outputStages.contains(renderStage);
    }

    public RenderStage getOutputRenderStage(int index) {
        return outputStages.get(index);
    }

    public void setOutputRenderStage(int index, RenderStage renderStage) {
        outputStages.set(index, renderStage);
    }

    public Iterator<RenderStage> getOutputRenderStageIterator() {
        return outputStages.iterator();
    }
    
    private Model createDefaultFXModel() {
        float[] quadPositions = new float[] {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                1.0f, 1.0f,
                -1.0f, 1.0f
        };
        VertexBuffer quadVertexBuffer = graphicsDevice.createVertexBuffer();
        quadVertexBuffer.setLayout(new LayoutConfig().float2());
        quadVertexBuffer.setUnitCount(quadPositions.length / 2);
        quadVertexBuffer.writeData((FloatBuffer) BufferUtils.createFloatBuffer(quadPositions.length).put(quadPositions).rewind());

        int[] quadIndices = new int[] { 0, 1, 2, 2, 3, 0 };
        IndexBuffer quadIndexBuffer = graphicsDevice.createIndexBuffer();
        quadIndexBuffer.setUnitCount(quadIndices.length);
        quadIndexBuffer.writeData((IntBuffer)BufferUtils.createIntBuffer(quadIndices.length).put(quadIndices).rewind());

        VertexArray quadVertexArray = graphicsDevice.createVertexArray();
        quadVertexArray.addVertexBuffer(quadVertexBuffer);

        Model quadModel = new Model(quadVertexArray);
        quadModel.addMesh(new Mesh(quadIndexBuffer, new Material(), graphicsDevice.getDefaultFXRasterizer()));

        return quadModel;
    }
    
    public interface RenderStageConstructor {
        RenderStage init(Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext);
    }
    
    public interface FXRenderStageConstructor {
        FXRenderStage init(Model fxModel, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext);
    }
}
