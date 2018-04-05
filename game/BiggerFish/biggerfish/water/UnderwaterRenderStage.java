package biggerfish.water;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Model;
import cage.core.render.stage.FXRenderStage;
import cage.core.render.stage.GeometryRenderStage;
import cage.core.render.stage.LightingRenderStage;

public class UnderwaterRenderStage extends FXRenderStage {

    public UnderwaterRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, graphicsContext);
    }

    @Override
    protected void preRender() {
        if(getInputRenderStageCount() > 0 && getInputRenderStage(0) instanceof LightingRenderStage) {
            LightingRenderStage lightingRenderStage = (LightingRenderStage) getInputRenderStage(0);
            getShader().addTexture("diffuseTexture", lightingRenderStage.getRenderTarget().getColorTexture(0));
            if(lightingRenderStage.getInputRenderStageCount() > 0 && lightingRenderStage.getInputRenderStage(0) instanceof GeometryRenderStage) {
                GeometryRenderStage geometryRenderStage = (GeometryRenderStage) lightingRenderStage.getInputRenderStage(0);
                getShader().addTexture("normalTexture", geometryRenderStage.getNormalTextureOutput());
                getShader().addTexture("depthTexture", geometryRenderStage.getDepthTextureOutput());
            }
        }
    }
}
