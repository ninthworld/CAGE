package cage.core.graphics.rendertarget;

import cage.core.graphics.texture.TextureMS;

import java.util.Map;

public abstract class RenderTargetMS extends RenderTarget<TextureMS> {

    private int samples;

    public RenderTargetMS(int width, int height, int samples) {
        super(width, height);
        this.samples = samples;
    }

    public int getMultisampleCount() {
        return samples;
    }

    public void setMultisampleCount(int samples) {
        this.samples = samples;
        getColorTextureIterator().forEachRemaining((Map.Entry<Integer, TextureMS> entry) -> {
        	entry.getValue().setMultisampleCount(samples);
        });
        if(containsDepthTexture()) {
            getDepthTexture().setMultisampleCount(samples);
        }
    }
}