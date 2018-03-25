package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Mesh;
import cage.core.model.Model;

public class FXRenderStage extends RenderStage {

    private Model fxModel;

    public FXRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        super(shader, renderTarget, rasterizer, graphicsContext);
        this.fxModel = fxModel;
    }

    @Override
    public void render() {
        super.render();
        getGraphicsContext().bindRenderTarget(getRenderTarget());
        getGraphicsContext().clear();
        getGraphicsContext().bindVertexArray(fxModel.getVertexArray());
        getGraphicsContext().bindShader(getShader());
        getGraphicsContext().drawIndexed(fxModel.getMesh(0).getIndexBuffer());
        getGraphicsContext().unbindShader(getShader());
        getGraphicsContext().unbindVertexArray(fxModel.getVertexArray());
    }
    
    public Model getFXModel() {
    	return fxModel;
    }
    
    public void setFXModel(Model fxModel) {
    	this.fxModel = fxModel;
    }
}
