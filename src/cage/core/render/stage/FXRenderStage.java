package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Mesh;
import cage.core.model.Model;

public class FXRenderStage extends RenderStage {

    private Model fxModel;

    public FXRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(shader, renderTarget, graphicsContext);
        this.fxModel = fxModel;
    }

    @Override
    protected void midRender() {
        super.midRender();
        getGraphicsContext().bindRenderTarget(getRenderTarget());
        getGraphicsContext().clear();

        Mesh mesh = fxModel.getMesh(0);
        getGraphicsContext().setPrimitive(mesh.getPrimitive());
        getGraphicsContext().bindRasterizer(mesh.getRasterizer());
        getGraphicsContext().bindVertexArray(fxModel.getVertexArray());
        getGraphicsContext().bindShader(getShader());
        getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
    }
    
    public Model getFXModel() {
    	return fxModel;
    }
    
    public void setFXModel(Model fxModel) {
    	this.fxModel = fxModel;
    }
}
