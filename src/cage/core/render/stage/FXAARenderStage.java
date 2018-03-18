package cage.core.render.stage;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Model;

public class FXAARenderStage extends FXRenderStage {

    public FXAARenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, IGraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, rasterizer, graphicsContext);
    }

    @Override
    public void preRender() {
        if(getInputStageCount() >= 1) {
            getShader().attachTexture("diffuseTexture", getInputStage(0).getRenderTarget().getColorTexture(0));
        }
    }
}
