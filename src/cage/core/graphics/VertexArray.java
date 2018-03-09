package cage.core.graphics;

import java.util.ArrayList;
import java.util.List;

public abstract class VertexArray {

    protected List<VertexBuffer> m_vertexBuffers;
    protected int m_attributeCount;

    protected VertexArray() {
        m_vertexBuffers = new ArrayList<>();
        m_attributeCount = 0;
    }

    public int getAttributeCount() {
        return m_attributeCount;
    }

    public int getVertexBufferCount() {
        return m_vertexBuffers.size();
    }

    public VertexBuffer getVertexBuffer(int index) {
        return m_vertexBuffers.get(index);
    }

    public void removeVertexBuffer(int index) {
        m_vertexBuffers.remove(index);
    }

    public abstract void attachVertexBuffer(VertexBuffer buffer);
}
