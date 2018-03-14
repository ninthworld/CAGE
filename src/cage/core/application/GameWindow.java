package cage.core.application;

public abstract class GameWindow {

    private String title;
    private int width;
    private int height;
    private int samples;
    private boolean closed;

    protected GameWindow(String title, int width, int height, int samples) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.samples = samples;
        this.closed = false;
    }

    public abstract void initialize();
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

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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
}
