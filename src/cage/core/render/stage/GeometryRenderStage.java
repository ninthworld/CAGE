package cage.core.render.stage;

import cage.core.graphics.*;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.model.ExtModel;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.scene.InstancedSceneEntity;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;

public class GeometryRenderStage extends RenderStage {

    private Shader boneShader;
    private Shader instancedShader;
    private Shader instancedBoneShader;
    private UniformBuffer cameraUniform;
    private UniformBuffer entityUniform;
    private UniformBuffer materialUniform;
    private ShaderStorageBuffer boneShaderStorage;
    private ShaderStorageBuffer entityShaderStorage;
    private Camera camera;
    private SceneNode node;

    public GeometryRenderStage(Camera camera, SceneNode node,
                               Shader instancedBoneShader, Shader instancedShader, Shader boneShader, Shader shader,
                               RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(shader, renderTarget, graphicsContext);
        this.boneShader = boneShader;
        this.instancedShader = instancedShader;
        this.instancedBoneShader = instancedBoneShader;
        this.camera = camera;
        this.node = node;
    }

    @Override
    protected void preRender() {
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }
        if(entityUniform == null) {
            entityUniform = getShader().getUniformBuffer("Entity");
        }
        if(materialUniform == null) {
            materialUniform = getShader().getUniformBuffer("Material");
        }
        if(boneShaderStorage == null) {
            boneShaderStorage = boneShader.getShaderStorageBuffer("Bone");
        }
        if(entityShaderStorage == null) {
            entityShaderStorage = instancedShader.getShaderStorageBuffer("Entity");
        }

        cameraUniform.writeData(camera.readData());
    }

    @Override
    protected void midRender() {
        super.midRender();

        getGraphicsContext().bindRenderTarget(getRenderTarget());
        getGraphicsContext().clear();

        renderNode(node);
    }

    private void renderNode(Node node) {
        if(!node.isEnabled()) {
            return;
        }
        node.getNodeIterator().forEachRemaining(this::renderNode);
        if(node instanceof SceneEntity) {
            SceneEntity entity = (SceneEntity)node;
            if(camera.getFrustum().inFrustum(entity.getWorldBounds())) {
                if(entity instanceof InstancedSceneEntity) {
                    entityShaderStorage.writeData(entity.readData());
                }
                else {
                    entityUniform.writeData(entity.readData());
                }
	            Model model = entity.getModel();
	            getGraphicsContext().bindVertexArray(model.getVertexArray());
	            model.getMeshIterator().forEachRemaining((Mesh mesh) -> {
	                Material material = mesh.getMaterial();
	                materialUniform.writeData(material.readData());

	                Shader shader = getShader();
                    if(entity instanceof InstancedSceneEntity) {
                        shader = instancedShader;
                    }
                    if(model instanceof ExtModel) {
                        shader = boneShader;
                        if(entity instanceof InstancedSceneEntity) {
                            shader = instancedBoneShader;
                        }
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

	                if(material.getDiffuseTexture() != null && material.getDiffuseTexture() instanceof Texture2D) {
                        shader.addTexture("diffuseTexture", material.getDiffuseTexture());
	                }
	
	                if(material.getNormalTexture() != null && material.getNormalTexture() instanceof Texture2D) {
                        shader.addTexture("normalTexture", material.getNormalTexture());
	                }
	
	                if(material.getSpecularTexture() != null && material.getSpecularTexture() instanceof Texture2D) {
                        shader.addTexture("specularTexture", material.getSpecularTexture());
	                }
	
	                if(material.getHighlightTexture() != null && material.getHighlightTexture() instanceof Texture2D) {
	                    shader.addTexture("highlightTexture", material.getHighlightTexture());
	                }

	                if(mesh.getBlender() != null) {
                        getGraphicsContext().bindBlender(mesh.getBlender());
                    }
	                getGraphicsContext().setPrimitive(mesh.getPrimitive());
	                getGraphicsContext().bindRasterizer(mesh.getRasterizer());
                    getGraphicsContext().bindShader(shader);
                    if(entity instanceof InstancedSceneEntity) {
                        getGraphicsContext().drawIndexedInstanced(((InstancedSceneEntity) entity).getInstanceCount(), mesh.getIndexBuffer());
                    }
                    else {
                        getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
                    }
                    if(mesh.getBlender() != null) {
                        getGraphicsContext().unbindBlender(mesh.getBlender());
                    }
	            });
	        }
        }
    }

    public Camera getCamera() {
         return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public SceneNode getSceneNode() {
        return node;
    }

    public void setSceneNode(SceneNode node) {
        this.node = node;
    }

    public Texture getDiffuseTextureOutput() {
        return getRenderTarget().getColorTexture(0);
    }

    public Texture getSpecularTextureOutput() {
        return getRenderTarget().getColorTexture(1);
    }

    public Texture getNormalTextureOutput() {
        return getRenderTarget().getColorTexture(2);
    }

    public Texture getDepthTextureOutput() {
        return getRenderTarget().getDepthTexture();
    }
}
