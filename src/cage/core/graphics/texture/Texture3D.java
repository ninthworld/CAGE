package cage.core.graphics.texture;

import cage.core.graphics.type.FormatType;

public abstract class Texture3D extends Texture {

    public Texture3D(int width, int height, FormatType format, boolean mipmapping) {
        super(width, height, format, mipmapping);
    }

    public Texture3D(int width, int height, FormatType format) {
        super(width, height, format);
    }

    public Texture3D(int width, int height, boolean mipmapping) {
        super(width, height, mipmapping);
    }

    public Texture3D(int width, int height) {
        super(width, height);
    }
}
