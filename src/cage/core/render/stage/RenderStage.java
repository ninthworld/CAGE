package cage.core.render.stage;

import cage.core.graphics.RenderTarget;
import cage.core.graphics.Shader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class RenderStage {

    private List<RenderStage> m_inputStages;
    private Shader m_shader;
    private RenderTarget m_renderTarget;

    protected RenderStage(Shader shader, RenderTarget renderTarget) {
        m_inputStages = new ArrayList<>();
        m_shader = shader;
        m_renderTarget = renderTarget;
    }

    public void preRender() {
    }

    public void render() {
        getInputStageIterator().forEachRemaining(RenderStage::render);
        preRender();
    }

    public Shader getShader() {
        return m_shader;
    }

    public void setShader(Shader shader) {
        m_shader = shader;
    }

    public RenderTarget getRenderTarget() {
        return m_renderTarget;
    }

    public void setRenderTarget(RenderTarget renderTarget) {
        m_renderTarget = renderTarget;
    }

    public int getInputStageCount() {
        return m_inputStages.size();
    }

    public void attachInputStage(RenderStage stage) {
        m_inputStages.add(stage);
    }

    public void detachInputStage(RenderStage stage) {
        m_inputStages.remove(stage);
    }

    public void containsInputStage(RenderStage stage) {
        m_inputStages.contains(stage);
    }

    public RenderStage getInputStage(int index) {
        return m_inputStages.get(index);
    }

    public Iterator<RenderStage> getInputStageIterator() {
        return m_inputStages.iterator();
    }
}
