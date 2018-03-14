package cage.core.render.stage;

import cage.core.graphics.*;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;

public class GeometryRenderStage extends RenderStage {

    private IGraphicsContext graphicsContext;
    private UniformBuffer cameraUniform;
    private UniformBuffer entityUniform;
    private UniformBuffer materialUniform;
    private Camera camera;
    private SceneNode node;

    public GeometryRenderStage(SceneNode node, Camera camera, Shader shader, RenderTarget renderTarget, IGraphicsContext graphicsContext) {
        super(shader, renderTarget);
        this.graphicsContext = graphicsContext;
        this.node = node;
        this.camera = camera;
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

        graphicsContext.bindRenderTarget(getRenderTarget());
        graphicsContext.clear();

        renderNode(node);
    }

    private void renderNode(Node node) {
        node.getNodeIterator().forEachRemaining(this::renderNode);
        if(node instanceof SceneEntity) {
            SceneEntity entity = (SceneEntity)node;
            Model model = entity.getModel();

            entityUniform.setData(entity.getBufferData());

            graphicsContext.bindVertexArray(model.getVertexArray());
            model.getMeshIterator().forEachRemaining((Mesh mesh) -> {
                Material material = mesh.getMaterial();
                materialUniform.setData(material.getBufferData());

                if(material.getDiffuseTexture() != null) {
                    getShader().attachTexture("diffuseTexture", material.getDiffuseTexture());
                }

                if(material.getNormalTexture() != null) {
                    getShader().attachTexture("normalTexture", material.getNormalTexture());
                }

                if(material.getSpecularTexture() != null) {
                    getShader().attachTexture("specularTexture", material.getSpecularTexture());
                }

                if(material.getSpecularHighlightTexture() != null) {
                    getShader().attachTexture("highlightTexture", material.getSpecularHighlightTexture());
                }

                graphicsContext.bindShader(getShader());
                graphicsContext.drawIndexed(mesh.getIndexBuffer());
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
