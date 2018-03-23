package cage.opengl.graphics.texture;

import cage.core.graphics.texture.TextureCubeMap;
import cage.core.graphics.type.FormatType;

import java.nio.*;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static cage.opengl.utils.GLUtils.checkError;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLTextureCubeMap extends TextureCubeMap implements GLTexture {

    private int textureId;

    public GLTextureCubeMap(int width, int height, FormatType format, boolean mipmapping) {
        super(width, height, format, mipmapping);        
        generate();
    }

    public GLTextureCubeMap(int width, int height, boolean mipmapping) {
        super(width, height, mipmapping);        
        generate();
    }

    public GLTextureCubeMap(int width, int height, FormatType format) {
        super(width, height, format);        
        generate();
    }

    public GLTextureCubeMap(int width, int height) {
        super(width, height);        
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
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    }

    public void initialize() {
        bind();
        for(int i=0; i<6; ++i) {
            glTexImage2D(
                    GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0,
                    getGLInternalFormatType(getFormat()),
                    getWidth(), getHeight(),
                    0,
                    getGLFormatType(getFormat()),
                    getGLDataType(getFormat()),
                    0);
            checkError("glTexImage2D");
        }

        if(isMipmapping()) {
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
            checkError("glGenerateMipmap");
        }
        else {
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, 0);
        }
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
    public void setMipmapping(boolean mipmapping) {
        super.setMipmapping(mipmapping);
        initialize();
    }
    
    @Override
    public void writeData(ByteBuffer data) {
        bind();
        glTexSubImage2D(
                getGLCubeMapFace(getDataCubeFace()),
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void writeData(ShortBuffer data) {
        bind();
        glTexSubImage2D(
                getGLCubeMapFace(getDataCubeFace()),
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void writeData(IntBuffer data) {
        bind();
        glTexSubImage2D(
                getGLCubeMapFace(getDataCubeFace()),
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void writeData(FloatBuffer data) {
        bind();
        glTexSubImage2D(
                getGLCubeMapFace(getDataCubeFace()),
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void writeData(DoubleBuffer data) {
        bind();
        glTexSubImage2D(
                getGLCubeMapFace(getDataCubeFace()),
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public int getTextureId() {
        return textureId;
    }
}
