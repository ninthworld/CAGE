package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Model;

public class FXAARenderStage extends FXRenderStage {

    public FXAARenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, rasterizer, graphicsContext);
    }

    @Override
    public void preRender() {
        if(getInputRenderStageCount() >= 1) {
            getShader().addTexture("diffuseTexture", getInputRenderStage(0).getRenderTarget().getColorTexture(0));
        }
    }
}
