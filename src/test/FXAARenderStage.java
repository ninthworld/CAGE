package test;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.RenderTarget;
import cage.core.graphics.Shader;
import cage.core.model.Model;
import cage.core.render.stage.FXRenderStage;

public class FXAARenderStage extends FXRenderStage {

    public FXAARenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, IGraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, graphicsContext);
    }

    @Override
    public void preRender() {
        if(getInputStageCount() >= 1) {
            getShader().attachTexture("diffuseTexture", getInputStage(0).getRenderTarget().getColorTexture(0));
        }
    }
}
