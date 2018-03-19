package cage.core.render.stage;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.TextureCubeMap;
import cage.core.model.Model;
import cage.core.scene.SceneManager;
import cage.core.scene.light.Light;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Iterator;

public class LightingRenderStage extends FXRenderStage {

    private SceneManager sceneManager;

    public LightingRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, IGraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, rasterizer, graphicsContext);
        this.sceneManager = null;
    }

    @Override
    public void preRender() {
        if(getInputStageCount() > 0 && getInputStage(0) instanceof GeometryRenderStage) {
            GeometryRenderStage renderStage = (GeometryRenderStage)getInputStage(0);
            getShader().attachTexture("diffuseTexture", renderStage.getDiffuseTextureOutput());
            getShader().attachTexture("specularTexture", renderStage.getSpecularTextureOutput());
            getShader().attachTexture("normalTexture", renderStage.getNormalTextureOutput());
            getShader().attachTexture("depthTexture", renderStage.getDepthTextureOutput());

            int capacity =  sceneManager.getLightCount() * Light.BUFFER_DATA_SIZE;
            FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(capacity);
            Iterator<Light> it = sceneManager.getLightIterator();
            while(it.hasNext()) {
                lightBuffer.put(it.next().getBufferData());
            }
            lightBuffer.flip();
            getShader().getShaderStorageBuffer("Light").setData(lightBuffer);
            getShader().getUniformBuffer("Camera").setData(renderStage.getCamera().getBufferData());
        }
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
