package cage.core.application;

import cage.core.input.InputState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GameWindow {

    private String title;
    protected int width;
    protected int height;
    protected int fsWidth;
    protected int fsHeight;
    protected int posX;
    protected int posY;
    private int refreshRate;
    private int samples;
    private boolean mouseVisible;
    private boolean mouseCentered;
    private boolean fullscreen;
    protected boolean closed;
    private List<IListener> listeners;

    public GameWindow(String title, int width, int height, int refreshRate, int samples) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.fsWidth = width;
        this.fsHeight = height;
        this.posX = 0;
        this.posY = 0;
        this.refreshRate = refreshRate;
        this.samples = samples;
        this.mouseVisible = true;
        this.mouseCentered = false;
        this.fullscreen = false;
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
    public int getPositionX() {
        return posX;
    }

    public int getPositionY() {
        return posY;
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
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

    public void addListener(IListener listener) {
        listeners.add(listener);
    }

    public int getListenerSize() {
        return listeners.size();
    }

    public Iterator<IListener> getListenerIterator() {
        return listeners.iterator();
    }

    public boolean containsListener(IListener listener) {
        return listeners.contains(listener);
    }

    public IListener getListener(int index) {
        return listeners.get(index);
    }

    public void removeListener(IListener listener) {
        listeners.remove(listener);
    }

    public interface IWindowCloseListener extends IListener {
        void onWindowClose();
    }

    public interface IWindowResizeListener extends IListener {
        void onWindowResize(int width, int height);
    }

    public interface IWindowMoveListener extends IListener {
        void onWindowMove(int x, int y);
    }

    public interface IWindowFocusListener extends IListener {
        void onWindowFocus(boolean focused);
    }

    public interface IMouseButtonListener extends IListener {
        void onMouseButton(int button, InputState state);
    }

    public interface IMouseWheelListener extends IListener {
        void onMouseWheel(double xOffset, double yOffset);
    }

    public interface IMouseMoveListener extends IListener {
        void onMouseMove(double x, double y);
    }

    public interface IKeyboardListener extends IListener {
        void onKeyboard(int key, InputState state);
    }

    public interface IListener {
    }
}
