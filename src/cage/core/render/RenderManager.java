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

    private IGraphicsDevice m_graphicsDevice;
    private IGraphicsContext m_graphicsContext;
    private GameWindow m_window;
    private SceneManager m_sceneManager;
    private AssetManager m_assetManager;
    private List<RenderStage> m_outputStages;
    private GeometryRenderStage m_defaultGeometryRenderStage;
    private FXRenderStage m_defaultLightingRenderStage;
    private UniformBuffer m_defaultCameraUniformBuffer;
    private UniformBuffer m_defaultEntityUniformBuffer;
    private UniformBuffer m_defaultMaterialUniformBuffer;
    private UniformBuffer m_defaultLightUniformBuffer;
    private Model m_defaultFXModel;

    public RenderManager(IGraphicsDevice graphicsDevice, IGraphicsContext graphicsContext, GameWindow window, SceneManager sceneManager, AssetManager assetManager) {
        m_outputStages = new ArrayList<>();
        m_graphicsDevice = graphicsDevice;
        m_graphicsContext = graphicsContext;
        m_window = window;
        m_sceneManager = sceneManager;
        m_assetManager = assetManager;

        m_defaultCameraUniformBuffer = graphicsDevice.createUniformBuffer();
        m_defaultCameraUniformBuffer.setLayout(Camera.BUFFER_LAYOUT);

        m_defaultEntityUniformBuffer = graphicsDevice.createUniformBuffer();
        m_defaultEntityUniformBuffer.setLayout(SceneEntity.BUFFER_LAYOUT);

        m_defaultMaterialUniformBuffer = graphicsDevice.createUniformBuffer();
        m_defaultMaterialUniformBuffer.setLayout(Material.BUFFER_LAYOUT);

        m_defaultLightUniformBuffer = graphicsDevice.createUniformBuffer();
        m_defaultLightUniformBuffer.setLayout(Light.BUFFER_LAYOUT);

        m_defaultFXModel = createDefaultFXModel();

        m_defaultGeometryRenderStage = createGeometryRenderStage(m_sceneManager, m_sceneManager.getDefaultCamera());
        RenderTarget renderTarget = m_graphicsDevice.createRenderTarget(m_window.getWidth(), m_window.getHeight());
        assetManager.getDefaultLightingShader().attachUniformBuffer("Camera", m_defaultCameraUniformBuffer);
        assetManager.getDefaultLightingShader().attachUniformBuffer("Light", m_defaultLightUniformBuffer);
        m_defaultLightingRenderStage = new LightingRenderStage(sceneManager, assetManager.getDefaultLightingShader(), renderTarget, m_defaultFXModel, m_graphicsContext);
        m_defaultLightingRenderStage.attachInputStage(m_defaultGeometryRenderStage);
        attachOutputStage(m_defaultLightingRenderStage);
    }

    public void render() {
        m_outputStages.forEach(RenderStage::render);
        m_graphicsContext.bindBackBuffer();
        m_graphicsContext.clear();
        m_outputStages.forEach((RenderStage stage) -> m_graphicsContext.resolveToBackBuffer(stage.getRenderTarget()));
    }

    public GeometryRenderStage createGeometryRenderStage(SceneNode node, Camera camera) {
        Shader shader = m_assetManager.getDefaultGeometryShader();
        shader.attachUniformBuffer("Camera", m_defaultCameraUniformBuffer);
        shader.attachUniformBuffer("Entity", m_defaultEntityUniformBuffer);
        shader.attachUniformBuffer("Material", m_defaultMaterialUniformBuffer);
        RenderTarget renderTarget = m_graphicsDevice.createRenderTarget(m_window.getWidth(), m_window.getHeight());
        renderTarget.attachColorTexture(1, m_graphicsDevice.createTexture(m_window.getWidth(), m_window.getHeight()));
        renderTarget.attachColorTexture(2, m_graphicsDevice.createTexture(m_window.getWidth(), m_window.getHeight()));
        return new GeometryRenderStage(node, camera, shader, renderTarget, m_graphicsContext);
    }

    public FXRenderStage createFXRenderStage(Shader shader, RenderTarget renderTarget) {
        return new FXRenderStage(shader, renderTarget, m_defaultFXModel, m_graphicsContext);
    }

    public GeometryRenderStage getDefaultGeometryRenderStage() {
        return m_defaultGeometryRenderStage;
    }

    public FXRenderStage getDefaultLightingRenderStage() {
        return m_defaultLightingRenderStage;
    }

    public UniformBuffer getDefaultCameraUniformBuffer() {
        return m_defaultCameraUniformBuffer;
    }

    public UniformBuffer getDefaultEntityUniformBuffer() {
        return m_defaultEntityUniformBuffer;
    }

    public UniformBuffer getDefaultLightUniformBuffer() {
        return m_defaultLightUniformBuffer;
    }

    public UniformBuffer getDefaultMaterialUniformBuffer() {
        return m_defaultMaterialUniformBuffer;
    }

    public Model getDefaultFXModel() {
        return m_defaultFXModel;
    }

    public int getOutputStageCount() {
        return m_outputStages.size();
    }

    public void attachOutputStage(RenderStage stage) {
        m_outputStages.add(stage);
    }

    public void detachOutputStage(RenderStage stage) {
        m_outputStages.remove(stage);
    }

    public boolean containsOutputStage(RenderStage stage) {
        return m_outputStages.contains(stage);
    }

    public RenderStage getOutputStage(int index) {
        return m_outputStages.get(index);
    }

    public Iterator<RenderStage> getOutputStageIterator() {
        return m_outputStages.iterator();
    }

    private Model createDefaultFXModel() {
        float[] quadPositions = new float[] {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                1.0f, 1.0f,
                -1.0f, 1.0f
        };
        VertexBuffer quadVertexBuffer = m_graphicsDevice.createVertexBuffer();
        quadVertexBuffer.setLayout(new LayoutConfig().float2());
        quadVertexBuffer.setUnitCount(quadPositions.length / 2);
        quadVertexBuffer.setData((FloatBuffer) BufferUtils.createFloatBuffer(quadPositions.length).put(quadPositions).rewind());

        int[] quadIndices = new int[] { 0, 1, 2, 2, 3, 0 };
        IndexBuffer quadIndexBuffer = m_graphicsDevice.createIndexBuffer();
        quadIndexBuffer.setUnitCount(quadIndices.length);
        quadIndexBuffer.setData((IntBuffer)BufferUtils.createIntBuffer(quadIndices.length).put(quadIndices).rewind());

        VertexArray quadVertexArray = m_graphicsDevice.createVertexArray();
        quadVertexArray.attachVertexBuffer(quadVertexBuffer);

        Model quadModel = new Model(quadVertexArray);
        quadModel.attachMesh(new Mesh(quadIndexBuffer, new Material()));

        return quadModel;
    }
}
