package cage.core.render.stage;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.RenderTarget;
import cage.core.graphics.Shader;
import cage.core.model.Mesh;
import cage.core.model.Model;

public class FXRenderStage extends RenderStage {

    protected IGraphicsContext m_graphicsContext;
    protected Model m_fxModel;

    public FXRenderStage(Shader shader, RenderTarget renderTarget, Model fxModel, IGraphicsContext graphicsContext) {
        super(shader, renderTarget);
        m_graphicsContext = graphicsContext;
        m_fxModel = fxModel;
    }

    @Override
    public void render() {
        super.render();
        m_graphicsContext.bindRenderTarget(getRenderTarget());
        m_graphicsContext.clear();
        m_graphicsContext.bindVertexArray(m_fxModel.getVertexArray());
        m_graphicsContext.bindShader(getShader());
        m_fxModel.getMeshIterator().forEachRemaining((Mesh mesh) -> m_graphicsContext.drawIndexed(mesh.getIndexBuffer()));
    }
}
