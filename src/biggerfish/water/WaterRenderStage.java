package biggerfish.water;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.render.stage.GeometryRenderStage;
import cage.core.render.stage.LightingRenderStage;

public class WaterRenderStage extends GeometryRenderStage {

    private UniformBuffer cameraUniform;
    private UniformBuffer entityUniform;

    public WaterRenderStage(WaterManager node, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
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
        if(getInputRenderStageCount() > 0 && getInputRenderStage(0) instanceof LightingRenderStage) {
            LightingRenderStage lightingRenderStage = (LightingRenderStage) getInputRenderStage(0);
            getShader().addTexture("refractTexture", lightingRenderStage.getRenderTarget().getColorTexture(0));

            if(lightingRenderStage.getSkyboxTexture() != null) {
                getShader().addTexture("skyboxTexture", lightingRenderStage.getSkyboxTexture());
            }
            if(lightingRenderStage.getSkydomeTexture() != null) {
                getShader().addTexture("skydomeTexture", lightingRenderStage.getSkydomeTexture());
            }
        }
    }

    @Override
    protected void midRender() {
        getGraphicsContext().bindRenderTarget(getRenderTarget());
        getGraphicsContext().clear();

        WaterManager entity = (WaterManager)getSceneNode();
        Model model = entity.getModel();
        Mesh mesh = model.getMesh(0);
        entityUniform.writeData(entity.readData());
        getGraphicsContext().bindVertexArray(model.getVertexArray());
        getGraphicsContext().setPrimitive(mesh.getPrimitive());
        getGraphicsContext().bindRasterizer(mesh.getRasterizer());
        getGraphicsContext().bindShader(getShader());
        getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
    }
}
