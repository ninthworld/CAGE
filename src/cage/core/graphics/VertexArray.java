package cage.core.graphics;

import java.util.ArrayList;
import java.util.Iterator;
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

    public abstract void attachVertexBuffer(VertexBuffer buffer);

    public abstract void detachVertexBuffer(VertexBuffer buffer);

    public Iterator<VertexBuffer> getVertexBufferIterator() {
        return m_vertexBuffers.iterator();
    }

    public boolean containsVertexBuffer(VertexBuffer buffer) {
        return m_vertexBuffers.contains(buffer);
    }

    public VertexBuffer getVertexBuffer(int index) {
        return m_vertexBuffers.get(index);
    }
}
