package cage.opengl.graphics;

import cage.core.graphics.VertexBuffer;

import java.nio.*;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL15.*;

public class GLVertexBuffer extends VertexBuffer implements IGLBuffer {

    private int bufferId;

    public GLVertexBuffer() {
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
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    }

    @Override
    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public int getBufferId() {
        return bufferId;
    }

    @Override
    public void setData(ByteBuffer data) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(ShortBuffer data) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(IntBuffer data) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(FloatBuffer data) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(DoubleBuffer data) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(LongBuffer data) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        checkError("glBufferData");
        unbind();
    }
}
