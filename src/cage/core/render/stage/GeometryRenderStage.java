package cage.core.render.stage;

import cage.core.graphics.*;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
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

    public GeometryRenderStage(Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        super(shader, renderTarget, rasterizer, graphicsContext);
        this.node = null;
        this.camera = null;
    }

    @Override
    public void preRender() {
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }

        if(entityUniform == null) {
            entityUniform = getShader().getUniformBuffer("Entity");
        }

        if(materialUniform == null) {
            materialUniform = getShader().getUniformBuffer("Material");
        }

        cameraUniform.setData(camera.getBufferData());
    }

    @Override
    public void render() {
        super.render();

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
            Model model = entity.getModel();

            entityUniform.setData(entity.getBufferData());

            getGraphicsContext().bindVertexArray(model.getVertexArray());
            model.getMeshIterator().forEachRemaining((Mesh mesh) -> {
                Material material = mesh.getMaterial();
                materialUniform.setData(material.getBufferData());

                if(material.getDiffuseTexture() != null && material.getDiffuseTexture() instanceof Texture2D) {
                    getShader().attachTexture("diffuseTexture", material.getDiffuseTexture());
                }

                if(material.getNormalTexture() != null && material.getNormalTexture() instanceof Texture2D) {
                    getShader().attachTexture("normalTexture", material.getNormalTexture());
                }

                if(material.getSpecularTexture() != null && material.getSpecularTexture() instanceof Texture2D) {
                    getShader().attachTexture("specularTexture", material.getSpecularTexture());
                }

                if(material.getHighlightTexture() != null && material.getHighlightTexture() instanceof Texture2D) {
                    getShader().attachTexture("highlightTexture", material.getHighlightTexture());
                }

                getGraphicsContext().bindShader(getShader());
                getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
            });
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
