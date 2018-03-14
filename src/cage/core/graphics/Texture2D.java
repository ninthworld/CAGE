package cage.core.graphics;

import cage.core.graphics.type.FormatType;

public abstract class Texture2D extends Texture {

    public Texture2D(int width, int height, FormatType format, boolean mipmapping) {
        super(width, height, format, mipmapping);
    }

    public Texture2D(int width, int height, FormatType format) {
        super(width, height, format);
    }

    public Texture2D(int width, int height, boolean mipmapping) {
        super(width, height, mipmapping);
    }

    public Texture2D(int width, int height) {
        super(width, height);
    }
}
