package cage.core.graphics.vertexarray;

import cage.core.common.Destroyable;
import cage.core.graphics.buffer.VertexBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class VertexArray implements Destroyable {

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

    public void detachAllVertexBuffers() {
        vertexBuffers.forEach(this::detachVertexBuffer);
    }

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
