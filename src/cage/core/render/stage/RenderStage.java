package cage.core.render.stage;

import cage.core.common.Movable;
import cage.core.common.Sizable;
import cage.core.common.listener.Listener;
import cage.core.common.listener.MoveListener;
import cage.core.common.listener.ResizeListener;
import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class RenderStage implements Sizable, Movable {

    private int posX;
    private int posY;
    private Movable movableParent;
    private MoveListener moveListener;
    private int width;
    private int height;
    private Sizable sizableParent;
    private ResizeListener resizeListener;
    private List<Listener> listeners;

    private GraphicsContext graphicsContext;
    private List<RenderStage> inputStages;
    private Rasterizer rasterizer;
    private Shader shader;
    private RenderTarget renderTarget;
    private boolean rendered;

    public RenderStage(Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        this.inputStages = new ArrayList<>();
        this.rasterizer = rasterizer;
        this.shader = shader;
        this.renderTarget = renderTarget;
        this.rendered = false;

        this.posX = 0;
        this.posY = 0;
        this.movableParent = null;
        this.moveListener = null;
        this.width = renderTarget.getWidth();
        this.height = renderTarget.getHeight();
        this.sizableParent = null;
        this.resizeListener = null;
        this.listeners = new ArrayList<>();

        this.renderTarget.setSizableParent(this);
    }

    public void preRender() {
    }

    public void render() {
        if(rendered) {
            return;
        }
        rendered = true;
        inputStages.forEach(RenderStage::render);
        preRender();
        graphicsContext.bindRasterizer(rasterizer);
    }

    public void postRender() {
        rendered = false;
        inputStages.forEach(RenderStage::postRender);
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
        this.renderTarget.setSizableParent(this);
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

    public void addInputRenderStage(RenderStage renderStage) {
        inputStages.add(renderStage);
    }

    public void removeInputRenderStage(RenderStage renderStage) {
        inputStages.remove(renderStage);
    }

    public void removeInputRenderStage(int index) {
        inputStages.remove(index);
    }

    public void removeAllInputRenderStages() {
        inputStages.forEach(this::removeInputRenderStage);
    }

    public int getInputRenderStageCount() {
        return inputStages.size();
    }

    public boolean containsInputRenderStage(RenderStage renderStage) {
        return inputStages.contains(renderStage);
    }

    public RenderStage getInputRenderStage(int index) {
        return inputStages.get(index);
    }

    public Iterator<RenderStage> getInputRenderStageIterator() {
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
        notifyMove();
    }

    @Override
    public void notifyMove() {
        for(Listener listener : listeners) {
            if(listener instanceof MoveListener) {
                ((MoveListener) listener).onMove(posX, posY);
            }
        }
    }

    @Override
    public Movable getMovableParent() {
        return movableParent;
    }

    @Override
    public MoveListener getMovableParentListener() {
        return moveListener;
    }

    @Override
    public void setMovableParent(Movable parent) {
        setMovableParent(parent, this::setPosition);
    }

    @Override
    public void setMovableParent(Movable parent, MoveListener listener) {
        if(hasMovableParent()) {
            removeMovableParent();
        }
        this.movableParent = parent;
        this.moveListener = listener;
        this.movableParent.addListener(this.moveListener);
    }

    @Override
    public void removeMovableParent() {
        this.movableParent.removeListener(moveListener);
        this.movableParent = null;
        this.moveListener = null;
    }

    @Override
    public boolean hasMovableParent() {
        return movableParent != null;
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
        notifyResize();
    }

    @Override
    public void notifyResize() {
        for(Listener listener : listeners) {
            if(listener instanceof ResizeListener) {
                ((ResizeListener) listener).onResize(width, height);
            }
        }
    }

    @Override
    public Sizable getSizableParent() {
        return sizableParent;
    }

    @Override
    public ResizeListener getSizableParentListener() {
        return resizeListener;
    }

    @Override
    public void setSizableParent(Sizable parent) {
        setSizableParent(parent, this::setSize);
    }

    @Override
    public void setSizableParent(Sizable parent, ResizeListener listener) {
        if(hasSizableParent()) {
            removeSizableParent();
        }
        this.sizableParent = parent;
        this.resizeListener = listener;
        this.sizableParent.addListener(this.resizeListener);
    }

    @Override
    public void removeSizableParent() {
        this.sizableParent.removeListener(resizeListener);
        this.sizableParent = null;
        this.resizeListener = null;
    }

    @Override
    public boolean hasSizableParent() {
        return sizableParent != null;
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeListener(int index) {
        this.listeners.remove(index);
    }

    @Override
    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public int getListenerCount() {
        return listeners.size();
    }

    @Override
    public boolean containsListener(Listener listener) {
        return listeners.contains(listener);
    }

    @Override
    public Listener getListener(int index) {
        return listeners.get(index);
    }

    @Override
    public Iterator<Listener> getListenerIterator() {
        return listeners.iterator();
    }
}
