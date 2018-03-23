package cage.core.graphics.texture;

import cage.core.common.Destroyable;
import cage.core.common.Sizable;
import cage.core.common.Writable;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.FormatType;

import java.nio.*;

public abstract class Texture implements Destroyable, Sizable, Writable {

    private int width;
    private int height;
    private FormatType format;
    private boolean mipmapping;
    private Sampler sampler;

    public Texture(int width, int height, FormatType format, boolean mipmapping) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.mipmapping = mipmapping;
        this.sampler = null;
    }

    public Texture(int width, int height, FormatType format) {
        this(width, height, format, false);
    }

    public Texture(int width, int height, boolean mipmapping) {
        this(width, height, FormatType.RGBA_8_UNORM, mipmapping);
    }

    public Texture(int width, int height) {
        this(width, height, FormatType.RGBA_8_UNORM, false);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public FormatType getFormat() {
        return format;
    }

    public void setFormat(FormatType format) {
        this.format = format;
    }

    public boolean isMipmapping() {
        return mipmapping;
    }

    public void setMipmapping(boolean mipmapping) {
        this.mipmapping = mipmapping;
    }

    public Sampler getSampler() {
        return sampler;
    }

    public void setSampler(Sampler sampler) {
        this.sampler = sampler;
    }
}
