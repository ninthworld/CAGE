package cage.core.graphics.texture;

import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.FormatType;

public abstract class TextureMS extends Texture {

    private int samples;

    public TextureMS(int width, int height, int samples, FormatType format) {
        super(width, height, format);
        this.samples = samples;
    }

    public TextureMS(int width, int height, int samples) {
        super(width, height);
        this.samples = samples;
    }

    public int getMultisampleCount() {
        return samples;
    }

    public void setMultisampleCount(int samples) {
        this.samples = samples;
    }

    public void setSampler(Sampler sampler) {
    }
}
