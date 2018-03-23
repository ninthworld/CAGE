package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.window.listener.IResizeWindowListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class RenderStage {

    private GraphicsContext graphicsContext;
    private Rasterizer rasterizer;
    private List<RenderStage> inputStages;
    private Shader shader;
    private RenderTarget renderTarget;
    private Rectangle outputDimensions;
    private boolean rendered;

    public RenderStage(Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        this.inputStages = new ArrayList<>();
        this.shader = shader;
        this.renderTarget = renderTarget;
        this.rasterizer = rasterizer;
        this.graphicsContext = graphicsContext;
        this.outputDimensions = new Rectangle(0, 0, this.renderTarget.getWidth(), this.renderTarget.getHeight());
        if(this.renderTarget.containsResizeListener()) {
            this.renderTarget.getWindow().addListener((IResizeWindowListener) (width, height) -> outputDimensions.setSize(width, height));
        }
        this.rendered = false;
    }

    public void preRender() {
    }

    public void render() {
        if(rendered) {
            return;
        }
        rendered = true;
        getInputStageIterator().forEachRemaining(RenderStage::render);
        preRender();
        graphicsContext.bindRasterizer(rasterizer);
    }

    public void postRender() {
        rendered = false;
        getInputStageIterator().forEachRemaining(RenderStage::postRender);
    }

    public GraphicsContext getGraphicsContext() {
    	return graphicsContext;
    }
    
    public void setGraphicsContext(GraphicsContext graphicsContext) {
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

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
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

    public void detachAllInputStages() {
        inputStages.forEach(this::detachInputStage);
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

    public Rectangle getOutputDimensions() {
        return outputDimensions;
    }

    public void setOutputDimensions(Rectangle outputDimensions) {
        this.outputDimensions = outputDimensions;
    }

    public void setOutputDimensions(int x, int y, int width, int height) {
        setOutputDimensions(new Rectangle(x, y, width, height));
    }
}
