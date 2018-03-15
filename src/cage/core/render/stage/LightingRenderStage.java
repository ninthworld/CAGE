package cage.core.render.stage;

import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.RenderTarget;
import cage.core.graphics.Shader;
import cage.core.model.Model;
import cage.core.scene.SceneManager;
import cage.core.scene.light.Light;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Iterator;

public class LightingRenderStage extends FXRenderStage {

    public static final int MAX_LIGHTS = 3;

    private SceneManager sceneManager;

    public LightingRenderStage(Model fxModel, Shader shader, RenderTarget renderTarget, IGraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, graphicsContext);
        this.sceneManager = null;
    }
    
    public SceneManager getSceneManager() {
    	return sceneManager;
    }
    
    public void setSceneManager(SceneManager sceneManager) {
    	this.sceneManager = sceneManager;
    }

    @Override
    public void preRender() {
        if(getInputStageCount() == 1 && getInputStage(0) instanceof GeometryRenderStage) {
            GeometryRenderStage renderStage = (GeometryRenderStage)getInputStage(0);
            getShader().attachTexture("diffuseTexture", renderStage.getDiffuseTextureOutput());
            getShader().attachTexture("specularTexture", renderStage.getSpecularTextureOutput());
            getShader().attachTexture("normalTexture", renderStage.getNormalTextureOutput());
            getShader().attachTexture("depthTexture", renderStage.getDepthTextureOutput());

            int capacity = MAX_LIGHTS * Light.BUFFER_DATA_SIZE;
            FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(capacity);
            Iterator<Light> it = this.sceneManager.getLightIterator();
            while(it.hasNext()) {
                Light light = it.next();
                lightBuffer.put(light.getBufferData());
                int pos = lightBuffer.position() + Light.BUFFER_DATA_SIZE;
                if(pos < capacity) {
                    lightBuffer.position(pos);
                }
                else {
                    break;
                }
            }
            lightBuffer.flip();
            getShader().getUniformBuffer("Light").setData(lightBuffer);
            getShader().getUniformBuffer("Camera").setData(renderStage.getCamera().getBufferData());
        }
    }
}
