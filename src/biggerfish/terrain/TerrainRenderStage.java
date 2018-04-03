package biggerfish.terrain;

import biggerfish.quadtree.QuadTreeNode;
import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.render.stage.GeometryRenderStage;
import cage.core.render.stage.RenderStage;
import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;

import java.util.Iterator;

public class TerrainRenderStage extends GeometryRenderStage {

    private UniformBuffer cameraUniform;
    private UniformBuffer entityUniform;
    private UniformBuffer materialUniform;
    private Texture heightmapTexture;
    private Texture normalmapTexture;

    public TerrainRenderStage(Texture heightmapTexture, Texture normalmapTexture, TerrainManager node, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(node.getCamera(), node, shader, renderTarget, graphicsContext);
        this.heightmapTexture = heightmapTexture;
        this.normalmapTexture = normalmapTexture;
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

        getShader().addTexture("heightmapTexture", heightmapTexture);
        getShader().addTexture("normalmapTexture", normalmapTexture);

        Material material = ((TerrainManager)getSceneNode()).getModel().getMesh(0).getMaterial();
        materialUniform.writeData(material.readData());

        if (material.getDiffuseTexture() != null && material.getDiffuseTexture() instanceof Texture2D) {
            getShader().addTexture("diffuseTexture", material.getDiffuseTexture());
        }

        if (material.getNormalTexture() != null && material.getNormalTexture() instanceof Texture2D) {
            getShader().addTexture("normalTexture", material.getNormalTexture());
        }

        if (material.getSpecularTexture() != null && material.getSpecularTexture() instanceof Texture2D) {
            getShader().addTexture("specularTexture", material.getSpecularTexture());
        }

        if (material.getHighlightTexture() != null && material.getHighlightTexture() instanceof Texture2D) {
            getShader().addTexture("highlightTexture", material.getHighlightTexture());
        }

        cameraUniform.writeData(getCamera().readData());
    }

    @Override
    protected void midRender() {

        getGraphicsContext().bindRenderTarget(getRenderTarget());

        renderNode(getSceneNode());
    }

    private void renderNode(Node node) {
        if(!node.isEnabled()) {
            return;
        }
        node.getNodeIterator().forEachRemaining(this::renderNode);
        if(node instanceof QuadTreeNode) {
            QuadTreeNode entity = (QuadTreeNode)node;
            if(entity.isLeaf() && getCamera().getFrustum().inFrustum(entity.getWorldBounds())) {
                Model model = entity.getModel();
                Mesh mesh = model.getMesh(entity.getMeshIndex());
                entityUniform.writeData(entity.readData());
                getGraphicsContext().bindVertexArray(model.getVertexArray());
                getGraphicsContext().setPrimitive(mesh.getPrimitive());
                getGraphicsContext().bindRasterizer(mesh.getRasterizer());
                getGraphicsContext().bindShader(getShader());
                getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
            }
        }
    }

    public Texture getHeightMapTexture() {
        return heightmapTexture;
    }

    public void setHeightMapTexture(Texture heightTexture) {
        this.heightmapTexture = heightTexture;
    }

    public Texture getNormalmapTexture() {
        return normalmapTexture;
    }

    public void setNormalmapTexture(Texture normalmapTexture) {
        this.normalmapTexture = normalmapTexture;
    }
}
