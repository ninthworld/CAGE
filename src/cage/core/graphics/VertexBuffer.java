package cage.core.graphics;

import cage.core.graphics.config.LayoutConfig;

public abstract class VertexBuffer extends Buffer {

    protected LayoutConfig m_layout;

    protected VertexBuffer() {
        m_layout = new LayoutConfig();
    }

    public LayoutConfig getLayout() {
        return m_layout;
    }

    public void setLayout(LayoutConfig layout) {
        m_layout = layout;
    }
}
