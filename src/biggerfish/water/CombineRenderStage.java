package biggerfish.water;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Model;
import cage.core.render.stage.FXRenderStage;

public class CombineRenderStage extends FXRenderStage {

    public CombineRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, graphicsContext);
    }

    @Override
    protected void preRender() {
        if(getInputRenderStageCount() >= 2) {
            getShader().addTexture("colorTexture1", getInputRenderStage(0).getRenderTarget().getColorTexture(0));
            getShader().addTexture("colorTexture2", getInputRenderStage(1).getRenderTarget().getColorTexture(0));
            getShader().addTexture("depthTexture1", getInputRenderStage(0).getRenderTarget().getDepthTexture());
            getShader().addTexture("depthTexture2", getInputRenderStage(1).getInputRenderStage(0).getRenderTarget().getDepthTexture());
        }
    }
}
