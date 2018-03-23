package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.model.Model;
import cage.core.scene.SceneManager;
import cage.core.scene.light.Light;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Iterator;

public class LightingRenderStage extends FXRenderStage {

    private SceneManager sceneManager;

    public LightingRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, rasterizer, graphicsContext);
        this.sceneManager = null;
    }

    @Override
    public void preRender() {
        if(getInputRenderStageCount() > 0 && getInputRenderStage(0) instanceof GeometryRenderStage) {
            GeometryRenderStage renderStage = (GeometryRenderStage)getInputRenderStage(0);
            getShader().addTexture("diffuseTexture", renderStage.getDiffuseTextureOutput());
            getShader().addTexture("specularTexture", renderStage.getSpecularTextureOutput());
            getShader().addTexture("normalTexture", renderStage.getNormalTextureOutput());
            getShader().addTexture("depthTexture", renderStage.getDepthTextureOutput());

            int capacity =  sceneManager.getLightCount() * Light.READ_SIZE;
            FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(capacity);
            Iterator<Light> it = sceneManager.getLightIterator();
            while(it.hasNext()) {
                lightBuffer.put(it.next().readData());
            }
            lightBuffer.flip();
            getShader().getShaderStorageBuffer("Light").writeData(lightBuffer);
            getShader().getUniformBuffer("Camera").writeData(renderStage.getCamera().readData());
        }
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
