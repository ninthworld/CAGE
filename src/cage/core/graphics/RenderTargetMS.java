package cage.core.graphics;

public abstract class RenderTargetMS extends RenderTarget<TextureMS> {

    protected int m_samples;

    protected RenderTargetMS(int width, int height, int samples) {
        super(width, height);
        m_samples = samples;
    }

    public int getMultisampleCount() {
        return m_samples;
    }

    public void setMultisampleCount(int samples) {
        m_samples = samples;
        m_colorTextures.forEach((Integer i, TextureMS t) -> {
        	t.setMultisampleCount(m_samples);
        });
        if(hasDepthTexture()) {
            m_depthTexture.setMultisampleCount(m_samples);
        }
    }
}
