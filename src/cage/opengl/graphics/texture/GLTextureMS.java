package cage.opengl.graphics.texture;

import cage.core.graphics.texture.TextureMS;
import cage.core.graphics.type.FormatType;

import java.nio.*;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

public class GLTextureMS extends TextureMS implements GLTexture {

    private int textureId;

    public GLTextureMS(int width, int height, int samples, FormatType format) {
        super(width, height, samples, format);
        generate();
    }

    public GLTextureMS(int width, int height, int samples) {
        super(width, height, samples);
        generate();
    }

    private void generate() {
        int[] textures = new int[1];
        glGenTextures(textures);
        textureId = textures[0];
        initialize();
    }

    @Override
    public void destroy() {
        if(textureId > 0) {
            glDeleteTextures(new int[]{ textureId });
        }
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, textureId);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
    }

    public void initialize() {
        bind();
        glTexImage2DMultisample(
                GL_TEXTURE_2D_MULTISAMPLE,
                getMultisampleCount(),
                getGLInternalFormatType(getFormat()),
                getWidth(), getHeight(), false);
        checkError("glTexImage2DMultisample");
        unbind();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        initialize();
    }

    @Override
    public void setFormat(FormatType format) {
        super.setFormat(format);
        initialize();
    }

    @Override
    public void setMultisampleCount(int samples) {
        super.setMultisampleCount(samples);
        initialize();
    }

    @Override
    public void writeData(ByteBuffer data) {
    }

    @Override
    public void writeData(ShortBuffer data) {
    }

    @Override
    public void writeData(IntBuffer data) {
    }

    @Override
    public void writeData(FloatBuffer data) {
    }

    @Override
    public void writeData(DoubleBuffer data) {
    }

    @Override
    public int getTextureId() {
        return textureId;
    }
}
