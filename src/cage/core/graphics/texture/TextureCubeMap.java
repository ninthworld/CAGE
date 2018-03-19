package cage.core.graphics.texture;

import cage.core.graphics.type.CubeFaceType;
import cage.core.graphics.type.FormatType;

public abstract class TextureCubeMap extends Texture {

    private CubeFaceType cubeFace;

    public TextureCubeMap(int width, int height, FormatType format, boolean mipmapping) {
        super(width, height, format, mipmapping);
        this.cubeFace = CubeFaceType.RIGHT;
    }

    public TextureCubeMap(int width, int height, FormatType format) {
        super(width, height, format);
        this.cubeFace = CubeFaceType.RIGHT;
    }

    public TextureCubeMap(int width, int height, boolean mipmapping) {
        super(width, height, mipmapping);
        this.cubeFace = CubeFaceType.RIGHT;
    }

    public TextureCubeMap(int width, int height) {
        super(width, height);
        this.cubeFace = CubeFaceType.RIGHT;
    }

    public CubeFaceType getDataCubeFace() {
        return cubeFace;
    }

    public void setDataCubeFace(CubeFaceType face) {
        this.cubeFace = face;
    }
}
