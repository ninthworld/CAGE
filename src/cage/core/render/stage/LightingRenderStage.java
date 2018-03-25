package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
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
    private UniformBuffer cameraUniform;
    private ShaderStorageBuffer lightShaderStorage;

    public LightingRenderStage(SceneManager sceneManager, Model fxModel, Shader shader, RenderTarget renderTarget, Rasterizer rasterizer, GraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, rasterizer, graphicsContext);
        this.sceneManager = sceneManager;
    }

    @Override
    public void preRender() {
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }
        if(lightShaderStorage == null) {
            lightShaderStorage = getShader().getShaderStorageBuffer("Light");
        }

        if(getInputRenderStageCount() > 0 && getInputRenderStage(0) instanceof GeometryRenderStage) {
            GeometryRenderStage geometryRenderStage = (GeometryRenderStage)getInputRenderStage(0);
            getShader().addTexture("diffuseTexture", geometryRenderStage.getDiffuseTextureOutput());
            getShader().addTexture("specularTexture", geometryRenderStage.getSpecularTextureOutput());
            getShader().addTexture("normalTexture", geometryRenderStage.getNormalTextureOutput());
            getShader().addTexture("depthTexture", geometryRenderStage.getDepthTextureOutput());

            int capacity =  sceneManager.getLightCount() * Light.READ_SIZE;
            FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(capacity);
            Iterator<Light> it = sceneManager.getLightIterator();
            while(it.hasNext()) {
                lightBuffer.put(it.next().readData());
            }
            lightBuffer.flip();
            lightShaderStorage.writeData(lightBuffer);
            cameraUniform.writeData(geometryRenderStage.getCamera().readData());

            if(getInputRenderStageCount() > 1 && getInputRenderStage(1) instanceof ShadowRenderStage) {
                ShadowRenderStage shadowRenderStage = (ShadowRenderStage)getInputRenderStage(1);
                getShader().addTexture("shadowTexture", shadowRenderStage.getDiffuseTextureOutput());
            }
        }
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
