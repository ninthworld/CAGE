package cage.core.render.stage;

import cage.core.graphics.RenderTarget;
import cage.core.graphics.Shader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class RenderStage {

    private List<RenderStage> inputStages;
    private Shader shader;
    private RenderTarget renderTarget;

    public RenderStage(Shader shader, RenderTarget renderTarget) {
        this.inputStages = new ArrayList<>();
        this.shader = shader;
        this.renderTarget = renderTarget;
    }

    public void preRender() {
    }

    public void render() {
        getInputStageIterator().forEachRemaining(RenderStage::render);
        preRender();
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public RenderTarget getRenderTarget() {
        return renderTarget;
    }

    public void setRenderTarget(RenderTarget renderTarget) {
        this.renderTarget = renderTarget;
    }

    public int getInputStageCount() {
        return inputStages.size();
    }

    public void attachInputStage(RenderStage stage) {
        inputStages.add(stage);
    }

    public void detachInputStage(RenderStage stage) {
        inputStages.remove(stage);
    }

    public void containsInputStage(RenderStage stage) {
        inputStages.contains(stage);
    }

    public RenderStage getInputStage(int index) {
        return inputStages.get(index);
    }

    public Iterator<RenderStage> getInputStageIterator() {
        return inputStages.iterator();
    }
}
