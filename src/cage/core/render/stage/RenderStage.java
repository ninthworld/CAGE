package cage.core.render.stage;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class RenderStage {

    private IGraphicsContext graphicsContext;
    private Rasterizer rasterizer;
    private List<RenderStage> inputStages;
    private Shader shader;
    private RenderTarget renderTarget;

    public RenderStage(Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, IGraphicsContext graphicsContext) {
        this.inputStages = new ArrayList<>();
        this.shader = shader;
        this.renderTarget = renderTarget;
        this.rasterizer = rasterizer;
        this.graphicsContext = graphicsContext;
    }

    public void preRender() {
    }

    public void render() {
        getInputStageIterator().forEachRemaining(RenderStage::render);
        preRender();
        graphicsContext.bindRasterizer(rasterizer);
    }

    public IGraphicsContext getGraphicsContext() {
    	return graphicsContext;
    }
    
    public void setGraphicsContext(IGraphicsContext graphicsContext) {
    	this.graphicsContext = graphicsContext;
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

    public Rasterizer getRasterizer() {
        return rasterizer;
    }

    public void setRasterizer(Rasterizer rasterizer) {
        this.rasterizer = rasterizer;
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
