package cage.opengl.graphics;

import cage.core.graphics.UniformBuffer;

import java.nio.*;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.*;

public class GLUniformBuffer extends UniformBuffer implements IGLBuffer {

    private int m_bufferId;

    public GLUniformBuffer() {
        super();

        int[] buffers = new int[1];
        glGenBuffers(buffers);
        m_bufferId = buffers[0];
    }

    @Override
    public void destroy() {
        if(m_bufferId > 0) {
            glDeleteBuffers(new int[]{ m_bufferId });
        }
    }

    @Override
    public void bind() {
        glBindBuffer(GL_UNIFORM_BUFFER, m_bufferId);
    }

    @Override
    public void unbind() {
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    @Override
    public int getBufferId() {
        return m_bufferId;
    }

    @Override
    public void setData(ByteBuffer data) {
        bind();
        glBufferData(GL_UNIFORM_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(ShortBuffer data) {
        bind();
        glBufferData(GL_UNIFORM_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(IntBuffer data) {
        bind();
        glBufferData(GL_UNIFORM_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(FloatBuffer data) {
        bind();
        glBufferData(GL_UNIFORM_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(DoubleBuffer data) {
        bind();
        glBufferData(GL_UNIFORM_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(LongBuffer data) {
        bind();
        glBufferData(GL_UNIFORM_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }
}

