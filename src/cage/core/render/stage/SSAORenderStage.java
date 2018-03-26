package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.model.Model;

public class SSAORenderStage extends FXRenderStage {

    private UniformBuffer cameraUniform;
    private Texture noiseTexture;

    public SSAORenderStage(Texture noiseTexture, Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, rasterizer, graphicsContext);
        this.noiseTexture = noiseTexture;
    }

    @Override
    protected void preRender() {
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }
        if(noiseTexture != null) {
            getShader().addTexture("noiseTexture", noiseTexture);
        }
        if(getInputRenderStageCount() > 0 && getInputRenderStage(0) instanceof GeometryRenderStage) {
            GeometryRenderStage geometryRenderStage = (GeometryRenderStage)getInputRenderStage(0);
            getShader().addTexture("normalTexture", geometryRenderStage.getNormalTextureOutput());
            getShader().addTexture("depthTexture", geometryRenderStage.getDepthTextureOutput());
            cameraUniform.writeData(geometryRenderStage.getCamera().readData());
        }
    }

    public Texture getDiffuseTextureOutput() {
        return getRenderTarget().getColorTexture(0);
    }
}
