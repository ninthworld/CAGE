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

    public TerrainRenderStage(TerrainManager node, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(node.getCamera(), node, shader, shader, shader, shader, renderTarget, graphicsContext);
    }

    @Override
    protected void preRender() {
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }
        if(entityUniform == null) {
            entityUniform = getShader().getUniformBuffer("Entity");
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
}
