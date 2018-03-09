package cage.core.graphics;

import cage.core.graphics.type.FormatType;

public abstract class TextureMS extends Texture {

    protected int m_samples;

    protected TextureMS(int width, int height, int samples, FormatType format) {
        super(width, height, format);
        m_samples = samples;
    }

    protected TextureMS(int width, int height, int samples) {
        super(width, height);
        m_samples = samples;
    }

    public int getMultisampleCount() {
        return m_samples;
    }

    public void setMultisampleCount(int samples) {
        m_samples = samples;
    }

    public void setSampler(Sampler sampler) {
    }
}
