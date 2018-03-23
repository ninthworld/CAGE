package cage.core.render;

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

import cage.core.window.listener.IResizeWindowListener;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderManager {

    private GraphicsDevice graphicsDevice;
    private GraphicsContext graphicsContext;
    private Window window;
    private SceneManager sceneManager;
    private AssetManager assetManager;
    private List<RenderStage> outputStages;
    private GeometryRenderStage defaultGeometryRenderStage;
    private LightingRenderStage defaultLightingRenderStage;
    private FXAARenderStage defaultFXAARenderStage;
    private UniformBuffer defaultWindowUniformBuffer;
    private UniformBuffer defaultCameraUniformBuffer;
    private UniformBuffer defaultEntityUniformBuffer;
    private UniformBuffer defaultMaterialUniformBuffer;
    private ShaderStorageBuffer defaultLightShaderStorageBuffer;
    private Model defaultFXModel;

    public RenderManager(GraphicsDevice graphicsDevice, GraphicsContext graphicsContext, Window window, SceneManager sceneManager, AssetManager assetManager) {
        this.outputStages = new ArrayList<>();
        this.graphicsDevice = graphicsDevice;
        this.graphicsContext = graphicsContext;
        this.window = window;
        this.sceneManager = sceneManager;
        this.assetManager = assetManager;

        defaultWindowUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultWindowUniformBuffer.setLayout(Window.READ_LAYOUT);
        defaultWindowUniformBuffer.writeData(window.readData());
        window.addListener((IResizeWindowListener) (width, height) -> defaultWindowUniformBuffer.writeData(window.readData()));

        defaultCameraUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultCameraUniformBuffer.setLayout(Camera.READ_LAYOUT);

        defaultEntityUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultEntityUniformBuffer.setLayout(SceneEntity.READ_LAYOUT);

        defaultMaterialUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultMaterialUniformBuffer.setLayout(Material.READ_LAYOUT);

        defaultLightShaderStorageBuffer = graphicsDevice.createShaderStorageBuffer();
        defaultLightShaderStorageBuffer.setLayout(Light.READ_LAYOUT);

        defaultFXModel = createDefaultFXModel();

        defaultGeometryRenderStage = createGeometryRenderStage(sceneManager.getDefaultCamera());
        defaultLightingRenderStage = createLightingRenderStage();
        defaultLightingRenderStage.attachInputStage(defaultGeometryRenderStage);

        defaultFXAARenderStage = (FXAARenderStage)createFXRenderStage(FXAARenderStage::new);
        {
            Shader shader = assetManager.loadShader("fx/fx.vs.glsl", "fx/fxaa.fs.glsl");
            shader.attachUniformBuffer("Window", getDefaultWindowUniformBuffer());
            defaultFXAARenderStage.setShader(shader);
            defaultFXAARenderStage.attachInputStage(defaultLightingRenderStage);
        }
        attachOutputStage(defaultFXAARenderStage);
    }

    public void render() {
        outputStages.forEach(RenderStage::render);
        graphicsContext.bindBackBuffer();
        graphicsContext.clear();
        outputStages.forEach((RenderStage stage) -> graphicsContext.resolveToBackBuffer(stage.getRenderTarget(), stage.getOutputDimensions()));
        outputStages.forEach(RenderStage::postRender);
    }

    public RenderStage createRenderStage(IRenderStageConstructor stage) {
        RenderTarget renderTarget = graphicsDevice.createRenderTarget2D();
    	return stage.init(null, renderTarget, graphicsDevice.getDefaultRasterizer(), graphicsContext);
    }

    public FXRenderStage createFXRenderStage(IFXRenderStageConstructor stage) {
        RenderTarget renderTarget = graphicsDevice.createRenderTarget2D();
        return stage.init(defaultFXModel, null, renderTarget, graphicsDevice.getDefaultFXRasterizer(), graphicsContext);
    }

    public GeometryRenderStage createGeometryRenderStage(Camera camera) {
        GeometryRenderStage renderStage = (GeometryRenderStage)createRenderStage(GeometryRenderStage::new);
        Shader shader = assetManager.getDefaultGeometryShader();
        shader.attachUniformBuffer("Camera", defaultCameraUniformBuffer);
        shader.attachUniformBuffer("Entity", defaultEntityUniformBuffer);
        shader.attachUniformBuffer("Material", defaultMaterialUniformBuffer);
        renderStage.getRenderTarget().attachColorTexture(1,
                graphicsDevice.createTexture2D(renderStage.getRenderTarget().getWidth(), renderStage.getRenderTarget().getHeight()));
        renderStage.getRenderTarget().attachColorTexture(2,
                graphicsDevice.createTexture2D(renderStage.getRenderTarget().getWidth(), renderStage.getRenderTarget().getHeight()));
        renderStage.setShader(shader);
        renderStage.setSceneNode(sceneManager.getRootSceneNode());
        renderStage.setCamera(camera);
        return renderStage;
    }

    public LightingRenderStage createLightingRenderStage() {
        LightingRenderStage renderStage = (LightingRenderStage)createFXRenderStage(LightingRenderStage::new);
        Shader shader = assetManager.getDefaultLightingShader();
        assetManager.getDefaultLightingShader().attachUniformBuffer("Camera", defaultCameraUniformBuffer);
        assetManager.getDefaultLightingShader().attachShaderStorageBuffer("Light", defaultLightShaderStorageBuffer);
        renderStage.setShader(shader);
        renderStage.setSceneManager(sceneManager);
        return renderStage;
    }

    public GeometryRenderStage getDefaultGeometryRenderStage() {
        return defaultGeometryRenderStage;
    }

    public LightingRenderStage getDefaultLightingRenderStage() {
        return defaultLightingRenderStage;
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

    public Model getDefaultFXModel() {
        return defaultFXModel;
    }

    public int getOutputStageCount() {
        return outputStages.size();
    }

    public void attachOutputStage(RenderStage stage) {
        outputStages.add(stage);
    }

    public void detachOutputStage(RenderStage stage) {
        outputStages.remove(stage);
    }

    public void detachAllOutputStages() {
        this.outputStages.forEach(this::detachOutputStage);
    }

    public boolean containsOutputStage(RenderStage stage) {
        return outputStages.contains(stage);
    }

    public RenderStage getOutputStage(int index) {
        return outputStages.get(index);
    }

    public void setOutputStage(int index, RenderStage renderStage) {
        outputStages.add(index, renderStage);
    }

    public Iterator<RenderStage> getOutputStageIterator() {
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
        quadVertexArray.attachVertexBuffer(quadVertexBuffer);

        Model quadModel = new Model(quadVertexArray);
        quadModel.attachMesh(new Mesh(quadIndexBuffer, new Material()));

        return quadModel;
    }
    
    public interface IRenderStageConstructor {
        RenderStage init(Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext);
    }
    
    public interface IFXRenderStageConstructor {
        FXRenderStage init(Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext);
    }
}
