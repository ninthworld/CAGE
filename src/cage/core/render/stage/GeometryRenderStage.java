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

    protected IGraphicsContext m_graphicsContext;
    protected UniformBuffer m_cameraUniform;
    protected UniformBuffer m_entityUniform;
    protected UniformBuffer m_materialUniform;
    protected Camera m_camera;
    protected SceneNode m_node;

    public GeometryRenderStage(SceneNode node, Camera camera, Shader shader, RenderTarget renderTarget, IGraphicsContext graphicsContext) {
        super(shader, renderTarget);
        m_graphicsContext = graphicsContext;
        m_node = node;
        m_camera = camera;
    }

    @Override
    public void preRender() {
        if(m_cameraUniform == null) {
            m_cameraUniform = getShader().getUniformBuffer("Camera");
        }

        if(m_entityUniform == null) {
            m_entityUniform = getShader().getUniformBuffer("Entity");
        }

        if(m_materialUniform == null) {
            m_materialUniform = getShader().getUniformBuffer("Material");
        }

        m_cameraUniform.setData(m_camera.getBufferData());
    }

    @Override
    public void render() {
        super.render();

        m_graphicsContext.bindRenderTarget(getRenderTarget());
        m_graphicsContext.clear();

        renderNode(m_node);
    }

    private void renderNode(Node node) {
        node.getNodeIterator().forEachRemaining(this::renderNode);
        if(node instanceof SceneEntity) {
            SceneEntity entity = (SceneEntity)node;
            Model model = entity.getModel();

            m_entityUniform.setData(entity.getBufferData());

            m_graphicsContext.bindVertexArray(model.getVertexArray());
            model.getMeshIterator().forEachRemaining((Mesh mesh) -> {
                Material material = mesh.getMaterial();
                m_materialUniform.setData(material.getBufferData());

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

                m_graphicsContext.bindShader(getShader());
                m_graphicsContext.drawIndexed(mesh.getIndexBuffer());
            });
        }
    }

    public Camera getCamera() {
         return m_camera;
    }

    public void setCamera(Camera camera) {
        m_camera = camera;
    }

    public SceneNode getSceneNode() {
        return m_node;
    }

    public void setSceneNode(SceneNode node) {
        m_node = node;
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
