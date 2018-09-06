package cage.core.window;

import cage.core.common.Movable;
import cage.core.common.Readable;
import cage.core.common.Sizable;
import cage.core.common.listener.Listener;
import cage.core.common.listener.MoveListener;
import cage.core.common.listener.ResizeListener;
import cage.core.graphics.config.LayoutConfig;
import cage.core.window.listener.WindowListener;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Window implements Sizable, Movable, Readable {

    public static final LayoutConfig READ_LAYOUT = new LayoutConfig().float2().float2();
    public static final int READ_SIZE = READ_LAYOUT.getUnitSize() / 4;

    protected int posX;
    protected int posY;
    private Movable movableParent;
    private MoveListener moveListener;
    protected int width;
    protected int height;
    private Sizable sizableParent;
    private ResizeListener resizeListener;
    private List<Listener> listeners;

    protected int windowedPosX;
    protected int windowedPosY;
    protected int fullscreenWidth;
    protected int fullscreenHeight;
    protected int windowedWidth;
    protected int windowedHeight;

    private String title;
    private int refreshRate;
    private int samples;
    private boolean vsync;
    private boolean mouseVisible;
    private boolean mouseCentered;
    private boolean fullscreen;
    protected boolean maximized;
    protected boolean closed;
    private FloatBuffer buffer;

    public Window(String title, int width, int height, boolean vsync, int refreshRate, int samples) {
        this.title = title;
        this.refreshRate = refreshRate;
        this.samples = samples;
        this.vsync = vsync;
        this.mouseVisible = true;
        this.mouseCentered = false;
        this.fullscreen = false;
        this.maximized = false;
        this.closed = false;
        this.listeners = new ArrayList<>();
        this.buffer = BufferUtils.createFloatBuffer(READ_SIZE);

        this.windowedPosX = 0;
        this.windowedPosY = 0;
        this.fullscreenWidth = width;
        this.fullscreenHeight = height;
        this.windowedWidth = width;
        this.windowedHeight = height;

        this.posX = 0;
        this.posY = 0;
        this.movableParent = null;
        this.moveListener = null;
        this.width = width;
        this.height = height;
        this.sizableParent = null;
        this.resizeListener = null;
        this.listeners = new ArrayList<>();
    }

    public abstract void update();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFullscreenWidth() {
        return fullscreenWidth;
    }

    public int getFullscreenHeight() {
        return fullscreenHeight;
    }

    public void setFullscreenSize(int width, int height) {
        this.fullscreenWidth = width;
        this.fullscreenHeight = height;
    }

    public int getWindowedWidth() {
        return windowedWidth;
    }

    public int getWindowedHeight() {
        return windowedHeight;
    }

    public void setWindowedSize(int width, int height) {
        this.windowedWidth = width;
        this.windowedHeight = height;
    }

    public int getWindowedPositionX() {
        return windowedPosX;
    }

    public int getWindowedPositionY() {
        return windowedPosY;
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    public int getMultisampleCount() {
        return samples;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isMouseVisible() {
        return mouseVisible;
    }

    public void setMouseVisible(boolean mouseVisible) {
        this.mouseVisible = mouseVisible;
    }

    public boolean isMouseCentered() {
        return mouseCentered;
    }

    public void setMouseCentered(boolean mouseCentered) {
        this.mouseCentered = mouseCentered;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    public Vector2f getMousePosition() {
        return new Vector2f();
    }

    @Override
    public FloatBuffer readData() {
        float width = getWidth();
        float height = getHeight();
        buffer.put(0, width);
        buffer.put(1, height);
        buffer.put(2, 1.0f / width);
        buffer.put(3, 1.0f / height);
        buffer.rewind();
        return buffer;
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
        if(!isFullscreen()) {
            this.windowedPosX = x;
            this.windowedPosY = y;
        }
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
        if(isFullscreen()) {
            this.fullscreenWidth = width;
            this.fullscreenHeight = height;
        }
        else {
            this.windowedWidth = width;
            this.windowedHeight = height;
        }
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
