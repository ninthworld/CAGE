package cage.core.graphics;

import cage.core.graphics.type.FormatType;

import java.nio.*;

public abstract class Texture {

    protected int m_width;
    protected int m_height;
    protected FormatType m_format;
    protected boolean m_mipmapping;
    protected Sampler m_sampler;

    protected Texture(int width, int height, FormatType format, boolean mipmapping) {
        m_width = width;
        m_height = height;
        m_format = format;
        m_mipmapping = mipmapping;
        m_sampler = null;
    }

    protected Texture(int width, int height, FormatType format) {
        this(width, height, format, false);
    }

    protected Texture(int width, int height, boolean mipmapping) {
        this(width, height, FormatType.RGBA_8_UNORM, mipmapping);
    }

    protected Texture(int width, int height) {
        this(width, height, FormatType.RGBA_8_UNORM, false);
    }

    public int getWidth() {
        return m_width;
    }

    public void setWidth(int width) {
        m_width = width;
    }

    public int getHeight() {
        return m_height;
    }

    public void setHeight(int height) {
        m_height = height;
    }

    public FormatType getFormat() {
        return m_format;
    }

    public void setFormat(FormatType format) {
        m_format = format;
    }

    public boolean isMipmapping() {
        return m_mipmapping;
    }

    public void setMipmapping(boolean mipmapping) {
        m_mipmapping = mipmapping;
    }

    public Sampler getSampler() {
        return m_sampler;
    }

    public void setSampler(Sampler sampler) {
        m_sampler = sampler;
    }

    public abstract void setData(ByteBuffer data);
    public abstract void setData(ShortBuffer data);
    public abstract void setData(IntBuffer data);
    public abstract void setData(FloatBuffer data);
    public abstract void setData(DoubleBuffer data);
}
