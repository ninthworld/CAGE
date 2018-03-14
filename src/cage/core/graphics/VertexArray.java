package cage.core.graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class VertexArray {

    protected List<VertexBuffer> vertexBuffers;
    protected int attributeCount;

    public VertexArray() {
        this.vertexBuffers = new ArrayList<>();
        this.attributeCount = 0;
    }

    public int getAttributeCount() {
        return attributeCount;
    }

    public int getVertexBufferCount() {
        return vertexBuffers.size();
    }

    public abstract void attachVertexBuffer(VertexBuffer buffer);

    public abstract void detachVertexBuffer(VertexBuffer buffer);

    public Iterator<VertexBuffer> getVertexBufferIterator() {
        return vertexBuffers.iterator();
    }

    public boolean containsVertexBuffer(VertexBuffer buffer) {
        return vertexBuffers.contains(buffer);
    }

    public VertexBuffer getVertexBuffer(int index) {
        return vertexBuffers.get(index);
    }
}
