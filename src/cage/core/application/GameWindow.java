package cage.core.application;

public abstract class GameWindow {

    protected String m_title;
    protected int m_width;
    protected int m_height;
    protected int m_samples;
    protected boolean m_closed;

    protected GameWindow(String title, int width, int height, int samples) {
        m_title = title;
        m_width = width;
        m_height = height;
        m_samples = samples;
        m_closed = false;
    }

    public abstract void initialize();
    public abstract void update();

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public int getWidth() {
        return m_width;
    }

    public void setWidth(int width) {
        this.m_width = width;
    }

    public int getHeight() {
        return m_height;
    }

    public void setHeight(int height) {
        m_height = height;
    }

    public int getMultisampleCount() {
        return m_samples;
    }

    public boolean isClosed() {
        return m_closed;
    }

    public void setClosed(boolean closed) {
        m_closed = closed;
    }
}
