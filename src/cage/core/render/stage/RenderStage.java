package cage.core.render.stage;

import cage.core.common.Movable;
import cage.core.common.Sizable;
import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.window.listener.IResizeWindowListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class RenderStage implements Sizable, Movable {

    private GraphicsContext graphicsContext;
    private Rasterizer rasterizer;
    private List<RenderStage> inputStages;
    private Shader shader;
    private RenderTarget renderTarget;
    private int width;
    private int height;
    private int posX;
    private int posY;
    private boolean rendered;

    public RenderStage(Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        this.inputStages = new ArrayList<>();
        this.shader = shader;
        this.renderTarget = renderTarget;
        this.rasterizer = rasterizer;
        this.graphicsContext = graphicsContext;
        this.width = renderTarget.getWidth();
        this.height = renderTarget.getHeight();
        this.posX = 0;
        this.posY = 0;
        if(this.renderTarget.containsResizeListener()) {
            //this.renderTarget.getWindow().addListener((IResizeWindowListener) (width, height) -> outputDimensions.setSize(width, height));
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

    @Override
    public int getX() {
        return posX;
    }

    @Override
    public int getY() {
        return posY;
    }

    @Override
    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
