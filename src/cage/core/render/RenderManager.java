package cage.core.render;

import cage.core.application.GameWindow;
import cage.core.asset.AssetManager;
import cage.core.graphics.*;
import cage.core.graphics.config.LayoutConfig;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.render.stage.FXRenderStage;
import cage.core.render.stage.GeometryRenderStage;
import cage.core.render.stage.LightingRenderStage;
import cage.core.render.stage.RenderStage;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneManager;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import cage.core.scene.light.Light;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderManager {

    private IGraphicsDevice graphicsDevice;
    private IGraphicsContext graphicsContext;
    private GameWindow window;
    private SceneManager sceneManager;
    private AssetManager assetManager;
    private List<RenderStage> outputStages;
    private GeometryRenderStage defaultGeometryRenderStage;
    private FXRenderStage defaultLightingRenderStage;
    private UniformBuffer defaultCameraUniformBuffer;
    private UniformBuffer defaultEntityUniformBuffer;
    private UniformBuffer defaultMaterialUniformBuffer;
    private UniformBuffer defaultLightUniformBuffer;
    private Model defaultFXModel;

    public RenderManager(IGraphicsDevice graphicsDevice, IGraphicsContext graphicsContext, GameWindow window, SceneManager sceneManager, AssetManager assetManager) {
        this.outputStages = new ArrayList<>();
        this.graphicsDevice = graphicsDevice;
        this.graphicsContext = graphicsContext;
        this.window = window;
        this.sceneManager = sceneManager;
        this.assetManager = assetManager;

        defaultCameraUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultCameraUniformBuffer.setLayout(Camera.BUFFER_LAYOUT);

        defaultEntityUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultEntityUniformBuffer.setLayout(SceneEntity.BUFFER_LAYOUT);

        defaultMaterialUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultMaterialUniformBuffer.setLayout(Material.BUFFER_LAYOUT);

        defaultLightUniformBuffer = graphicsDevice.createUniformBuffer();
        defaultLightUniformBuffer.setLayout(Light.BUFFER_LAYOUT);

        defaultFXModel = createDefaultFXModel();

        defaultGeometryRenderStage = createGeometryRenderStage(sceneManager, sceneManager.getDefaultCamera());
        RenderTarget renderTarget = graphicsDevice.createRenderTarget2D(window.getWidth(), window.getHeight());
        assetManager.getDefaultLightingShader().attachUniformBuffer("Camera", defaultCameraUniformBuffer);
        assetManager.getDefaultLightingShader().attachUniformBuffer("Light", defaultLightUniformBuffer);
        defaultLightingRenderStage = new LightingRenderStage(sceneManager, assetManager.getDefaultLightingShader(), renderTarget, defaultFXModel, graphicsContext);
        defaultLightingRenderStage.attachInputStage(defaultGeometryRenderStage);
        attachOutputStage(defaultLightingRenderStage);
    }

    public void render() {
        outputStages.forEach(RenderStage::render);
        graphicsContext.bindBackBuffer();
        graphicsContext.clear();
        outputStages.forEach((RenderStage stage) -> graphicsContext.resolveToBackBuffer(stage.getRenderTarget()));
    }

    public GeometryRenderStage createGeometryRenderStage(SceneNode node, Camera camera) {
        Shader shader = assetManager.getDefaultGeometryShader();
        shader.attachUniformBuffer("Camera", defaultCameraUniformBuffer);
        shader.attachUniformBuffer("Entity", defaultEntityUniformBuffer);
        shader.attachUniformBuffer("Material", defaultMaterialUniformBuffer);
        RenderTarget renderTarget = graphicsDevice.createRenderTarget2D(window.getWidth(), window.getHeight());
        renderTarget.attachColorTexture(1, graphicsDevice.createTexture2D(window.getWidth(), window.getHeight()));
        renderTarget.attachColorTexture(2, graphicsDevice.createTexture2D(window.getWidth(), window.getHeight()));
        return new GeometryRenderStage(node, camera, shader, renderTarget, graphicsContext);
    }

    public FXRenderStage createFXRenderStage(Shader shader, RenderTarget renderTarget) {
        return new FXRenderStage(shader, renderTarget, defaultFXModel, graphicsContext);
    }

    public GeometryRenderStage getDefaultGeometryRenderStage() {
        return defaultGeometryRenderStage;
    }

    public FXRenderStage getDefaultLightingRenderStage() {
        return defaultLightingRenderStage;
    }

    public UniformBuffer getDefaultCameraUniformBuffer() {
        return defaultCameraUniformBuffer;
    }

    public UniformBuffer getDefaultEntityUniformBuffer() {
        return defaultEntityUniformBuffer;
    }

    public UniformBuffer getDefaultLightUniformBuffer() {
        return defaultLightUniformBuffer;
    }

    public UniformBuffer getDefaultMaterialUniformBuffer() {
        return defaultMaterialUniformBuffer;
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

    public boolean containsOutputStage(RenderStage stage) {
        return outputStages.contains(stage);
    }

    public RenderStage getOutputStage(int index) {
        return outputStages.get(index);
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
        quadVertexBuffer.setData((FloatBuffer) BufferUtils.createFloatBuffer(quadPositions.length).put(quadPositions).rewind());

        int[] quadIndices = new int[] { 0, 1, 2, 2, 3, 0 };
        IndexBuffer quadIndexBuffer = graphicsDevice.createIndexBuffer();
        quadIndexBuffer.setUnitCount(quadIndices.length);
        quadIndexBuffer.setData((IntBuffer)BufferUtils.createIntBuffer(quadIndices.length).put(quadIndices).rewind());

        VertexArray quadVertexArray = graphicsDevice.createVertexArray();
        quadVertexArray.attachVertexBuffer(quadVertexBuffer);

        Model quadModel = new Model(quadVertexArray);
        quadModel.attachMesh(new Mesh(quadIndexBuffer, new Material()));

        return quadModel;
    }
}
