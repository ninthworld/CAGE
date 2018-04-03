package cage.core.render.stage;

import cage.core.graphics.*;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;

public class GeometryRenderStage extends RenderStage {

    private UniformBuffer cameraUniform;
    private UniformBuffer entityUniform;
    private UniformBuffer materialUniform;
    private Camera camera;
    private SceneNode node;

    public GeometryRenderStage(Camera camera, SceneNode node, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(shader, renderTarget, graphicsContext);
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
	            entityUniform.writeData(entity.readData());            
	            Model model = entity.getModel();
	            getGraphicsContext().bindVertexArray(model.getVertexArray());
	            model.getMeshIterator().forEachRemaining((Mesh mesh) -> {
	                Material material = mesh.getMaterial();
	                materialUniform.writeData(material.readData());
	
	                if(material.getDiffuseTexture() != null && material.getDiffuseTexture() instanceof Texture2D) {
	                    getShader().addTexture("diffuseTexture", material.getDiffuseTexture());
	                }
	
	                if(material.getNormalTexture() != null && material.getNormalTexture() instanceof Texture2D) {
	                    getShader().addTexture("normalTexture", material.getNormalTexture());
	                }
	
	                if(material.getSpecularTexture() != null && material.getSpecularTexture() instanceof Texture2D) {
	                    getShader().addTexture("specularTexture", material.getSpecularTexture());
	                }
	
	                if(material.getHighlightTexture() != null && material.getHighlightTexture() instanceof Texture2D) {
	                    getShader().addTexture("highlightTexture", material.getHighlightTexture());
	                }
	
	                getGraphicsContext().setPrimitive(mesh.getPrimitive());
	                getGraphicsContext().bindRasterizer(mesh.getRasterizer());
	                getGraphicsContext().bindShader(getShader());
	                getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
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
