package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.blender.Blender;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.model.ExtModel;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneManager;
import cage.core.scene.camera.OrthographicCamera;
import cage.core.scene.light.DirectionalLight;
import cage.core.scene.light.Light;
import cage.core.scene.light.ShadowCastableLight;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;

public class ShadowRenderStage extends RenderStage {

    public static final int SHADOW_RESOLUTION = 2048;
    private static final float[] RANGES = new float[] { 4.0f, 8.0f, 16.0f, 32.0f };

    private Shader simpleShader;
    private Shader simpleBoneShader;
    private RenderTarget[] shadowRenderTargets;
    private Model fxModel;
    private Blender blender;
    private SceneManager sceneManager;
    private OrthographicCamera shadowCamera;
    private UniformBuffer simpleCameraUniform;
    private UniformBuffer simpleEntityUniform;
    private UniformBuffer cameraUniform;
    private UniformBuffer shadowUniform;
    private ShaderStorageBuffer boneShaderStorage;
    private FloatBuffer shadowData;

    public ShadowRenderStage(
            SceneManager sceneManager, Model fxModel,
            Shader simpleShader, Shader simpleBoneShader, Shader shadowShader,
            RenderTarget[] shadowRenderTargets, RenderTarget outputRenderTarget,
            Blender blender, GraphicsContext graphicsContext) {
        super(shadowShader, outputRenderTarget, graphicsContext);
        this.simpleShader = simpleShader;
        this.simpleBoneShader = simpleBoneShader;
        this.shadowRenderTargets = shadowRenderTargets;
        this.fxModel = fxModel;
        this.blender = blender;
        this.sceneManager = sceneManager;
        this.shadowCamera = sceneManager.getRootSceneNode().createOrthographicCamera();
        this.shadowData = BufferUtils.createFloatBuffer(16 * RANGES.length);
    }

    @Override
    protected void preRender() {
        if(simpleCameraUniform == null) {
            simpleCameraUniform = simpleShader.getUniformBuffer("Camera");
        }
        if(simpleEntityUniform == null) {
            simpleEntityUniform = simpleShader.getUniformBuffer("Entity");
        }
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }
        if(shadowUniform == null) {
            shadowUniform = getShader().getUniformBuffer("Shadow");
        }
        if(boneShaderStorage == null) {
            boneShaderStorage = simpleBoneShader.getShaderStorageBuffer("Bone");
        }

        getShader().addTexture("shadowTexture[0]", shadowRenderTargets[0].getDepthTexture());
        getShader().addTexture("shadowTexture[1]", shadowRenderTargets[1].getDepthTexture());
        getShader().addTexture("shadowTexture[2]", shadowRenderTargets[2].getDepthTexture());
        getShader().addTexture("shadowTexture[3]", shadowRenderTargets[3].getDepthTexture());
    }

    @Override
    protected void midRender() {
        super.midRender();

        if(getInputRenderStageCount() > 0 && getInputRenderStage(0) instanceof GeometryRenderStage) {
            GeometryRenderStage geometryRenderStage = (GeometryRenderStage) getInputRenderStage(0);
            getShader().addTexture("depthTexture", geometryRenderStage.getDepthTextureOutput());
            cameraUniform.writeData(geometryRenderStage.getCamera().readData());
            shadowCamera.setLocalPosition(geometryRenderStage.getCamera().getWorldPosition());

            // Clear Output
            getGraphicsContext().bindRenderTarget(getRenderTarget());
            getGraphicsContext().setClearColor(new Color(0, 0, 0, 0));
            getGraphicsContext().clear();

            sceneManager.getLightIterator().forEachRemaining((Light light) -> {
                if (light instanceof ShadowCastableLight && ((ShadowCastableLight) light).isCastShadow() && light instanceof DirectionalLight) {
                    // Setup the Shadow Camera
                    shadowCamera.lookAlong(((DirectionalLight) light).getDirection());

                    shadowData.clear();
                    for(int i=0; i<RANGES.length; ++i) {
                        float radius = RANGES[i];
                        shadowCamera.setLeft(-radius);
                        shadowCamera.setRight(radius);
                        shadowCamera.setBottom(-radius);
                        shadowCamera.setTop(radius);
                        shadowCamera.setZNear(-radius);
                        shadowCamera.setZFar(radius);
                        shadowCamera.update(true);

                        simpleCameraUniform.writeData(shadowCamera.readData());

                        shadowCamera.getProjectionMatrix().mul(shadowCamera.getViewMatrix(), new Matrix4f()).get(i * 16, shadowData);

                        // Render the scene simply from the Shadow Camera
                        getGraphicsContext().bindRenderTarget(shadowRenderTargets[i]);
                        getGraphicsContext().clear();
                        renderNode(geometryRenderStage.getSceneNode());
                    }

                    // Setup the Output
                    shadowData.rewind();
                    shadowUniform.writeData(shadowData);

                    // Render to Output
                    getGraphicsContext().bindRenderTarget(getRenderTarget());

                    Mesh mesh = fxModel.getMesh(0);
                    getGraphicsContext().setPrimitive(mesh.getPrimitive());
                    getGraphicsContext().bindRasterizer(mesh.getRasterizer());
                    getGraphicsContext().bindVertexArray(fxModel.getVertexArray());
                    getGraphicsContext().bindShader(getShader());
                    getGraphicsContext().bindBlender(blender);
                    getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
                    getGraphicsContext().unbindBlender(blender);
                }
            });
        }
    }

    private void renderNode(Node node) {
        if(!node.isEnabled()) {
            return;
        }
        node.getNodeIterator().forEachRemaining(this::renderNode);
        if(node instanceof SceneEntity) {
            SceneEntity entity = (SceneEntity)node;
            if(entity.isCastShadow() && shadowCamera.getFrustum().inFrustum(entity.getWorldBounds())) {
                Model model = entity.getModel();

                Shader shader = simpleShader;
                if(model instanceof ExtModel) {
                    shader = simpleBoneShader;
                    ExtModel extModel = (ExtModel) model;
                    Matrix4f[] jointTransforms = extModel.getAnimatedModel().getJointTransforms();
                    try (MemoryStack stack = stackPush()) {
                        FloatBuffer boneBuffer = stack.callocFloat(jointTransforms.length * 16);
                        for(int i = 0; i < jointTransforms.length; ++i) {
                            jointTransforms[i].get(i * 16, boneBuffer);
                        }
                        boneBuffer.rewind();
                        boneShaderStorage.writeData(boneBuffer);
                    }
                }

                simpleEntityUniform.writeData(entity.readData());
                getGraphicsContext().bindVertexArray(model.getVertexArray());
                getGraphicsContext().bindShader(shader);
                model.getMeshIterator().forEachRemaining((Mesh mesh) -> {
                    getGraphicsContext().setPrimitive(mesh.getPrimitive());
                    getGraphicsContext().bindRasterizer(mesh.getRasterizer());
                    getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
                });
            }
        }
    }

    public Shader getSimpleShader() {
        return simpleShader;
    }

    public void setSimpleShader(Shader simpleShader) {
        this.simpleShader = simpleShader;
    }

    public Shader getSimpleBoneShader() {
        return simpleBoneShader;
    }

    public void setSimpleBoneShader(Shader simpleBoneShader) {
        this.simpleBoneShader = simpleBoneShader;
    }

    public Model getFXModel() {
        return fxModel;
    }

    public void setFXModel(Model fxModel) {
        this.fxModel = fxModel;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public Texture getDiffuseTextureOutput() {
        return getRenderTarget().getColorTexture(0);
    }
}
