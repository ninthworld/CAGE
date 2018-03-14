package cage.core.render.stage;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.RenderTarget;
import cage.core.graphics.Shader;
import cage.core.model.Mesh;
import cage.core.model.Model;

public class FXRenderStage extends RenderStage {

    private IGraphicsContext graphicsContext;
    private Model fxModel;

    public FXRenderStage(Shader shader, RenderTarget renderTarget, Model fxModel, IGraphicsContext graphicsContext) {
        super(shader, renderTarget);
        this.graphicsContext = graphicsContext;
        this.fxModel = fxModel;
    }

    @Override
    public void render() {
        super.render();
        graphicsContext.bindRenderTarget(getRenderTarget());
        graphicsContext.clear();
        graphicsContext.bindVertexArray(fxModel.getVertexArray());
        graphicsContext.bindShader(getShader());
        fxModel.getMeshIterator().forEachRemaining((Mesh mesh) -> graphicsContext.drawIndexed(mesh.getIndexBuffer()));
    }
}
