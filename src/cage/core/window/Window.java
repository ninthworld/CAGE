package cage.core.window;

import cage.core.common.IBufferData;
import cage.core.graphics.config.LayoutConfig;
import cage.core.window.listener.IWindowListener;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Window implements IBufferData {

    public static final int BUFFER_DATA_SIZE = 4;
    public static final LayoutConfig BUFFER_LAYOUT = new LayoutConfig().float2().float2();

    private String title;

    protected int width;
    protected int height;
    protected int fsWidth;
    protected int fsHeight;
    protected int wWidth;
    protected int wHeight;

    protected int posX;
    protected int posY;
    protected int wPosX;
    protected int wPosY;

    private int refreshRate;
    private int samples;
    private boolean vsync;
    private boolean mouseVisible;
    private boolean mouseCentered;
    private boolean fullscreen;
    protected boolean maximized;
    protected boolean closed;
    private List<IWindowListener> listeners;

    public Window(String title, int width, int height, boolean vsync, int refreshRate, int samples) {
        this.title = title;

        this.width = width;
        this.height = height;
        this.fsWidth = width;
        this.fsHeight = height;
        this.wWidth = width;
        this.wHeight = height;

        this.posX = 0;
        this.posY = 0;
        this.wPosX = 0;
        this.wPosY = 0;

        this.refreshRate = refreshRate;
        this.samples = samples;
        this.vsync = vsync;
        this.mouseVisible = true;
        this.mouseCentered = false;
        this.fullscreen = false;
        this.maximized = false;
        this.closed = false;
        this.listeners = new ArrayList<>();
    }

    public abstract void update();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if(isFullscreen()) {
            this.fsWidth = width;
            this.fsHeight = height;
        }
        else {
            this.wWidth = width;
            this.wHeight = height;
        }
    }

    public int getFullscreenWidth() {
        return fsWidth;
    }

    public int getFullscreenHeight() {
        return fsHeight;
    }

    public void setFullscreenSize(int width, int height) {
        this.fsWidth = width;
        this.fsHeight = height;
    }

    public int getWindowedWidth() {
        return wWidth;
    }

    public int getWindowedHeight() {
        return wHeight;
    }

    public void setWindowedSize(int width, int height) {
        this.wWidth = width;
        this.wHeight = height;
    }

    public int getPositionX() {
        return posX;
    }

    public int getPositionY() {
        return posY;
    }

    public int getWindowedPositionX() {
        return wPosX;
    }

    public int getWindowedPositionY() {
        return wPosY;
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
        if(!isFullscreen()) {
            this.wPosX = x;
            this.wPosY = y;
        }
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

    public void addListener(IWindowListener listener) {
        listeners.add(listener);
    }

    public int getListenerSize() {
        return listeners.size();
    }

    public Iterator<IWindowListener> getListenerIterator() {
        return listeners.iterator();
    }

    public boolean containsListener(IWindowListener listener) {
        return listeners.contains(listener);
    }

    public IWindowListener getListener(int index) {
        return listeners.get(index);
    }

    public void removeListener(IWindowListener listener) {
        listeners.remove(listener);
    }

    @Override
    public FloatBuffer getBufferData() {
        float width = getWidth();
        float height = getHeight();

        FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
        buffer.put(0, width);
        buffer.put(1, height);
        buffer.put(2, 1.0f / width);
        buffer.put(3, 1.0f / height);
        buffer.rewind();
        return buffer;
    }
}
