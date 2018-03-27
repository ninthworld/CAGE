package cage.core.render.stage;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.TextureCubeMap;
import cage.core.model.Model;
import cage.core.render.RenderManager;
import cage.core.scene.SceneManager;
import cage.core.scene.light.Light;
import cage.core.scene.light.ShadowCastableLight;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.Iterator;

import static org.lwjgl.system.MemoryStack.stackPush;

public class LightingRenderStage extends FXRenderStage {

    private SceneManager sceneManager;
    private UniformBuffer cameraUniform;
    private ShaderStorageBuffer lightShaderStorage;
    private UniformBuffer skyboxUniform;
    private TextureCubeMap skyboxTexture;
    private Texture skydomeTexture;
    private Vector3f skyColor;
    private Vector3f sunPosition;
    private boolean useSkybox;
    private boolean useSkydome;
    private boolean useAtmosphere;

    public LightingRenderStage(SceneManager sceneManager, Model fxModel, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
        super(fxModel, shader, renderTarget, graphicsContext);
        this.sceneManager = sceneManager;
        this.skyboxTexture = null;
        this.skyColor = new Vector3f(0.0f, 0.0f, 0.0f);
        this.sunPosition = new Vector3f(0.0f, 1.0f, 0.0f);
        this.useSkybox = false;
        this.useSkydome = false;
        this.useAtmosphere = false;
    }

    @Override
    protected void preRender() {
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }
        if(lightShaderStorage == null) {
            lightShaderStorage = getShader().getShaderStorageBuffer("Light");
        }
        if(skyboxUniform == null) {
            skyboxUniform = getShader().getUniformBuffer("Skybox");
        }

        if(skyboxTexture != null) {
            getShader().addTexture("skyboxTexture", skyboxTexture);
        }

        if(skydomeTexture != null) {
            getShader().addTexture("skydomeTexture", skydomeTexture);
        }

        try(MemoryStack stack = stackPush()) {
            FloatBuffer skyboxBuffer = stack.callocFloat(RenderManager.SKYBOX_READ_SIZE);
            skyColor.get(0, skyboxBuffer);
            sunPosition.get(4, skyboxBuffer);
            skyboxBuffer.put(8, (useSkybox ? 1.0f : 0.0f));
            skyboxBuffer.put(9, (useSkydome ? 1.0f : 0.0f));
            skyboxBuffer.put(10, (useAtmosphere ? 1.0f : 0.0f));
            skyboxBuffer.rewind();
            skyboxUniform.writeData(skyboxBuffer);
        }

        try(MemoryStack stack = stackPush()) {
            FloatBuffer lightBuffer = stack.callocFloat(sceneManager.getLightCount() * Light.READ_SIZE);
            Iterator<Light> it = sceneManager.getLightIterator();
            while(it.hasNext()) {
                lightBuffer.put(it.next().readData());
            }
            lightBuffer.flip();
            lightShaderStorage.writeData(lightBuffer);
        }

        if(getInputRenderStageCount() > 0 && getInputRenderStage(0) instanceof GeometryRenderStage) {
            GeometryRenderStage geometryRenderStage = (GeometryRenderStage)getInputRenderStage(0);
            getShader().addTexture("diffuseTexture", geometryRenderStage.getDiffuseTextureOutput());
            getShader().addTexture("specularTexture", geometryRenderStage.getSpecularTextureOutput());
            getShader().addTexture("normalTexture", geometryRenderStage.getNormalTextureOutput());
            getShader().addTexture("depthTexture", geometryRenderStage.getDepthTextureOutput());
            cameraUniform.writeData(geometryRenderStage.getCamera().readData());
        }
        if(getInputRenderStageCount() > 1 && getInputRenderStage(1) instanceof ShadowRenderStage) {
            ShadowRenderStage shadowRenderStage = (ShadowRenderStage)getInputRenderStage(1);
            getShader().addTexture("shadowTexture", shadowRenderStage.getDiffuseTextureOutput());
        }
        if(getInputRenderStageCount() > 2 && getInputRenderStage(2) instanceof SSAORenderStage) {
            SSAORenderStage ssaoRenderStage = (SSAORenderStage)getInputRenderStage(2);
            getShader().addTexture("ssaoTexture", ssaoRenderStage.getDiffuseTextureOutput());
        }
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public TextureCubeMap getSkyboxTexture() {
        return skyboxTexture;
    }

    public void setSkyboxTexture(TextureCubeMap skyboxTexture) {
        this.skyboxTexture = skyboxTexture;
    }

    public Texture getSkydomeTexture() {
        return skydomeTexture;
    }

    public void setSkydomeTexture(Texture skydomeTexture) {
        this.skydomeTexture = skydomeTexture;
    }

    public Vector3fc getSkyColor() {
        return skyColor;
    }

    public void setSkyColor(Vector3fc skyColor) {
        this.skyColor = new Vector3f(skyColor);
    }

    public Vector3fc getSunPosition() {
        return sunPosition;
    }

    public void setSunPosition(Vector3fc sunPosition) {
        this.sunPosition = new Vector3f(sunPosition);
    }

    public boolean isUseSkybox() {
        return useSkybox;
    }

    public void setUseSkybox(boolean useSkybox) {
        this.useSkybox = useSkybox;
    }

    public boolean isUseAtmosphere() {
        return useAtmosphere;
    }

    public void setUseAtmosphere(boolean useAtmosphere) {
        this.useAtmosphere = useAtmosphere;
    }

    public boolean isUseSkydome() {
        return useSkydome;
    }

    public void setUseSkydome(boolean useSkydome) {
        this.useSkydome = useSkydome;
    }
}
