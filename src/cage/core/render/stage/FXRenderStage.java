package cage.core.render.stage;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.RenderTarget;
import cage.core.graphics.Shader;
import cage.core.model.Mesh;
import cage.core.model.Model;

public class FXRenderStage extends RenderStage {

    private Model fxModel;

    public FXRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, IGraphicsContext graphicsContext) {
        super(shader, renderTarget, graphicsContext);
        this.fxModel = fxModel;
    }

    @Override
    public void render() {
        super.render();
        getGraphicsContext().bindRenderTarget(getRenderTarget());
        getGraphicsContext().clear();
        getGraphicsContext().bindVertexArray(fxModel.getVertexArray());
        getGraphicsContext().bindShader(getShader());
        fxModel.getMeshIterator().forEachRemaining((Mesh mesh) -> getGraphicsContext().drawIndexed(mesh.getIndexBuffer()));
    }
    
    public Model getFXModel() {
    	return fxModel;
    }
    
    public void setFXModel(Model fxModel) {
    	this.fxModel = fxModel;
    }
}
