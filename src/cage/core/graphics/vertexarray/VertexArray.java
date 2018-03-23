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

    public void addVertexBuffer(VertexBuffer buffer) {
        vertexBuffers.add(buffer);
        attributeCount += buffer.getLayout().getLayoutStack().size();
    }

    public void removeVertexBuffer(VertexBuffer buffer) {
        attributeCount -= buffer.getLayout().getLayoutStack().size();
        vertexBuffers.remove(buffer);
    }

    public void removeVertexBuffer(int index) {
        attributeCount -= vertexBuffers.remove(index).getLayout().getLayoutStack().size();
    }

    public void removeAllVertexBuffers() {
        vertexBuffers.forEach(this::removeVertexBuffer);
    }

    public int getVertexBufferCount() {
        return vertexBuffers.size();
    }

    public boolean containsVertexBuffer(VertexBuffer buffer) {
        return vertexBuffers.contains(buffer);
    }

    public VertexBuffer getVertexBuffer(int index) {
        return vertexBuffers.get(index);
    }

    public Iterator<VertexBuffer> getVertexBufferIterator() {
        return vertexBuffers.iterator();
    }

}
