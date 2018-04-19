package biggerfish.terrain;

import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture2D;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.render.stage.RenderStage;
import cage.core.scene.camera.Camera;

public class EnvironmentRenderStage extends RenderStage {

    private UniformBuffer cameraUniform;
    private UniformBuffer materialUniform;
    private Camera camera;
	private Model model;
	
	public EnvironmentRenderStage(Camera camera, Model model, Shader shader, RenderTarget renderTarget, GraphicsContext graphicsContext) {
		super(shader, renderTarget, graphicsContext);
		this.camera = camera;
		this.model = model;
	}

    @Override
    protected void preRender() {
        if(cameraUniform == null) {
            cameraUniform = getShader().getUniformBuffer("Camera");
        }
        if(materialUniform == null) {
            materialUniform = getShader().getUniformBuffer("Material");
        }
        
        cameraUniform.writeData(camera.readData());
    }
    
    @Override
    protected void midRender() {
        getGraphicsContext().bindRenderTarget(getRenderTarget());

        Mesh mesh = model.getMesh(0);
        Material material = mesh.getMaterial();
        materialUniform.writeData(material.readData());
        
        if(material.getDiffuseTexture() != null && material.getDiffuseTexture() instanceof Texture2D) {
        	getShader().addTexture("diffuseTexture", material.getDiffuseTexture());
        }

        if(mesh.getBlender() != null) {
            getGraphicsContext().bindBlender(mesh.getBlender());
        }
        getGraphicsContext().bindVertexArray(model.getVertexArray());
        getGraphicsContext().setPrimitive(mesh.getPrimitive());
        getGraphicsContext().bindRasterizer(mesh.getRasterizer());
        getGraphicsContext().bindShader(getShader());
        getGraphicsContext().drawIndexedInstanced(1024, mesh.getIndexBuffer());
        if(mesh.getBlender() != null) {
            getGraphicsContext().unbindBlender(mesh.getBlender());
        }
    }
}
