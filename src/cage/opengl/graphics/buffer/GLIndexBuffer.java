package cage.opengl.graphics.buffer;

import cage.core.graphics.buffer.IndexBuffer;

import java.nio.*;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL15.*;

public class GLIndexBuffer extends IndexBuffer implements GLBuffer {

    private int bufferId;

    public GLIndexBuffer() {
        super();

        int[] buffers = new int[1];
        glGenBuffers(buffers);
        this.bufferId = buffers[0];
    }

    @Override
    public void destroy() {
        if(bufferId > 0) {
            glDeleteBuffers(new int[]{ bufferId });
        }
    }

    @Override
    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
    }

    @Override
    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public int getBufferId() {
        return bufferId;
    }

    @Override
    public void writeData(ByteBuffer data) {
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void writeData(ShortBuffer data) {
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void writeData(IntBuffer data) {
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void writeData(FloatBuffer data) {
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void writeData(DoubleBuffer data) {
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }
}
