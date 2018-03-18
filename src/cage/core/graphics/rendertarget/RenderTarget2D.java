package cage.core.graphics.rendertarget;

import cage.core.graphics.texture.Texture2D;

public abstract class RenderTarget2D extends RenderTarget<Texture2D> {

    public RenderTarget2D(int width, int height) {
        super(width, height);
    }
}
