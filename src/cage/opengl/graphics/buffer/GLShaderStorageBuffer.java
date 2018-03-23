package cage.opengl.graphics.buffer;

import cage.core.graphics.buffer.ShaderStorageBuffer;

import java.nio.*;

import static cage.opengl.utils.GLUtils.checkError;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class GLShaderStorageBuffer extends ShaderStorageBuffer implements GLBuffer {

    private int bufferId;

    public GLShaderStorageBuffer() {
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
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
    }

    @Override
    public void unbind() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    @Override
    public int getBufferId() {
        return bufferId;
    }

    @Override
    public void setData(ByteBuffer data) {
        bind();
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(ShortBuffer data) {
        bind();
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(IntBuffer data) {
        bind();
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(FloatBuffer data) {
        bind();
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(DoubleBuffer data) {
        bind();
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }

    @Override
    public void setData(LongBuffer data) {
        bind();
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        checkError("glBufferData");
        unbind();
    }
}

