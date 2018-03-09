package test;

import java.awt.Color;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import cage.core.application.GameEngine;
import cage.core.graphics.*;
import cage.core.graphics.type.CullType;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.scene.SceneEntity;
import cage.core.scene.camera.PerspectiveCamera;
import cage.core.scene.light.AttenuationType;
import cage.core.scene.light.PointLight;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import cage.core.application.IGame;
import cage.core.graphics.config.LayoutConfig;
import cage.opengl.GLBootstrap;

public class MyGame implements IGame {

    private PerspectiveCamera camera;

    private UniformBuffer cameraUniform;
    private UniformBuffer modelUniform;
    private UniformBuffer materialUniform;
    private UniformBuffer lightUniform;

    private SceneEntity entity;

    private RenderTarget geometryScene;
    private RenderTarget lightingScene;

    private Model quadModel;
    private Shader lightingShader;

    public MyGame(GameEngine engine) {
    }

    @Override
    public void initialize(GameEngine engine) {
    	engine.getGraphicsContext().setClearColor(Color.decode("#6495ed"));
    	engine.getGraphicsDevice().getDefaultRasterizer().setCullType(CullType.NONE);

    	/*
    	Possible Configuration

        Camera camera = engine.getCameraManager().createProjectionCamera();
        Light light = engine.getLightManager().createPointLight();

        Model cubeModel = engine.getAssetManager().loadModel("cube.obj");

        SceneEntity cubeEntity = engine.getSceneManager().createSceneEntity();
        cubeEntity.setModel(cubeModel);

        RenderLayerMS sceneLayer = engine.getRenderManager().createRenderLayerMS();
        sceneLayer.setOutputSemantic(new SemanticConfig().color().normal().position())

        RenderLayerMS skyboxLayer = engine.getRenderManager().createRenderLayerMS();

        RenderLayer shadowLayer = engine.getRenderManager().createRenderLayer();
        shadowLayer.addInputLayer(sceneLayer);

        RenderLayerFX ssaoLayer = engine.getRenderManager().createRenderLayerFX();
        ssaoLayer.addInputLayer(sceneLayer);

        RenderLayerFX blurSSAOLayer = engine.getRenderManager().createRenderLayerFX();
        blurLayer.addInputLayer(ssaoLayer);

        RenderLayerFX lightingLayer = engine.getRenderManager().createFXRenderLayer();
        lightingLayer.addInputLayer(blurSSAOLayer);

        engine.getRenderManager().setOutputLayer(lightingLayer);
        */

    	camera = new PerspectiveCamera(null);
    	camera.setAspectRatio((float)engine.getWindow().getWidth() / (float)engine.getWindow().getHeight());
    	camera.setLocalPosition(new Vector3f(0.0f, 0.0f, 4.0f));
/*
    	float[] positions = new float[] {
    	        // Top
    	        -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                // Bottom
                1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                // Front
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                // Back
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                // Right
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                // Left
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f
        };
        VertexBuffer positionsBuffer = engine.getGraphicsDevice().createVertexBuffer();
        positionsBuffer.setLayout(new LayoutConfig().float3());
        positionsBuffer.setUnitCount(positions.length / 3);
        positionsBuffer.setData((FloatBuffer)BufferUtils.createFloatBuffer(positions.length).put(positions).rewind());

    	float[] texCoords = new float[] {
                // Top
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                // Bottom
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 1.0f,
                // Front
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                // Back
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                // Right
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                // Left
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f
        };
        VertexBuffer texCoordBuffer = engine.getGraphicsDevice().createVertexBuffer();
        texCoordBuffer.setLayout(new LayoutConfig().float2());
        texCoordBuffer.setUnitCount(texCoords.length / 2);
        texCoordBuffer.setData((FloatBuffer)BufferUtils.createFloatBuffer(texCoords.length).put(texCoords).rewind());

    	float[] normals = new float[] {
    	        // Top
    	        0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                // Bottom
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                // Front
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                // Back
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                // Right
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                // Left
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f
        };
        VertexBuffer normalBuffer = engine.getGraphicsDevice().createVertexBuffer();
        normalBuffer.setLayout(new LayoutConfig().float3());
        normalBuffer.setUnitCount(normals.length / 3);
        normalBuffer.setData((FloatBuffer)BufferUtils.createFloatBuffer(normals.length).put(normals).rewind());

    	float[] tangents = new float[] {
    	        // Top
    	        1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                // Bottom
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                // Front
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                // Back
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                // Right
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                // Left
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        };
        VertexBuffer tangentBuffer = engine.getGraphicsDevice().createVertexBuffer();
        tangentBuffer.setLayout(new LayoutConfig().float3());
        tangentBuffer.setUnitCount(tangents.length / 3);
        tangentBuffer.setData((FloatBuffer)BufferUtils.createFloatBuffer(tangents.length).put(tangents).rewind());

    	int[] indices = new int[] {
    	        // Top
                0, 1, 2, 2, 3, 0,
                // Bottom
                4, 5, 6, 6, 7, 4,
                // Front
                8, 9, 10, 10, 11, 8,
                // Back
                12, 13, 14, 14, 15, 12,
                // Right
                16, 17, 18, 18, 19, 16,
                // Left
                20, 21, 22, 22, 23, 20
        };
    	IndexBuffer indexBuffer = engine.getGraphicsDevice().createIndexBuffer();
    	indexBuffer.setUnitCount(indices.length);
    	indexBuffer.setData((IntBuffer)BufferUtils.createIntBuffer(indices.length).put(indices).rewind());

    	VertexArray vertexArray = engine.getGraphicsDevice().createVertexArray();
    	vertexArray.attachVertexBuffer(positionsBuffer);
        vertexArray.attachVertexBuffer(texCoordBuffer);
        vertexArray.attachVertexBuffer(normalBuffer);
        vertexArray.attachVertexBuffer(tangentBuffer);

        Material material = new Material();
        material.setAmbientColor(new Vector3f(0.0f, 1.0f, 0.0f));
        material.setDiffuse(new Vector3f(0.0f, 0.0f, 1.0f));

        Mesh mesh = new Mesh(indexBuffer, material);

        Model model = new Model(vertexArray);
        model.addMesh(mesh);
*/
    	
    	Model sphereModel = engine.getAssetLoader().loadOBJModel("sphere/sphere.obj");
    	
        entity = new SceneEntity(null);
        entity.setModel(sphereModel);

        float[] quadPositions = new float[] {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                1.0f, 1.0f,
                -1.0f, 1.0f
        };
        VertexBuffer quadVertexBuffer = engine.getGraphicsDevice().createVertexBuffer();
        quadVertexBuffer.setLayout(new LayoutConfig().float2());
        quadVertexBuffer.setUnitCount(quadPositions.length / 2);
        quadVertexBuffer.setData((FloatBuffer)BufferUtils.createFloatBuffer(quadPositions.length).put(quadPositions).rewind());

        int[] quadIndices = new int[] {
                0, 1, 2, 2, 3, 0
        };
        IndexBuffer quadIndexBuffer = engine.getGraphicsDevice().createIndexBuffer();
        quadIndexBuffer.setUnitCount(quadIndices.length);
        quadIndexBuffer.setData((IntBuffer)BufferUtils.createIntBuffer(quadIndices.length).put(quadIndices).rewind());

        VertexArray quadVertexArray = engine.getGraphicsDevice().createVertexArray();
        quadVertexArray.attachVertexBuffer(quadVertexBuffer);

        quadModel = new Model(quadVertexArray);
        quadModel.addMesh(new Mesh(quadIndexBuffer, new Material()));

        cameraUniform = engine.getGraphicsDevice().createUniformBuffer();
        cameraUniform.setLayout(new LayoutConfig().float4x4().float4x4().float4x4().float4x4());
        cameraUniform.setData(camera.getBufferData());

        modelUniform = engine.getGraphicsDevice().createUniformBuffer();
        modelUniform.setLayout(new LayoutConfig().float4x4());

        materialUniform = engine.getGraphicsDevice().createUniformBuffer();
        materialUniform.setLayout(new LayoutConfig().float4x4());

        lightUniform = engine.getGraphicsDevice().createUniformBuffer();
        LayoutConfig lightConfig = new LayoutConfig();
        for(int i=0; i<1; ++i) lightConfig.float4x4().float4();
        lightUniform.setLayout(lightConfig);
        
        /*
        PointLight light = new PointLight(null);
        light.setLocalPosition(new Vector3f(0.0f, 0.0f, 6.0f));
        light.setRange(16.0f);
		light.update();
        
        lightUniform.setData(light.getBufferData());
        */
        
        engine.getAssetLoader().getDefaultGeometryShader().attachUniformBuffer(0, cameraUniform);
        engine.getAssetLoader().getDefaultGeometryShader().attachUniformBuffer(1, modelUniform);
        engine.getAssetLoader().getDefaultGeometryShader().attachUniformBuffer(2, materialUniform);

        geometryScene = engine.getGraphicsDevice().createRenderTarget(engine.getWindow().getWidth(), engine.getWindow().getHeight());
        geometryScene.attachColorTexture(1, engine.getGraphicsDevice().createTexture(engine.getWindow().getWidth(), engine.getWindow().getHeight()));
        geometryScene.attachColorTexture(2, engine.getGraphicsDevice().createTexture(engine.getWindow().getWidth(), engine.getWindow().getHeight()));
        geometryScene.attachColorTexture(3, engine.getGraphicsDevice().createTexture(engine.getWindow().getWidth(), engine.getWindow().getHeight()));

        lightingScene = engine.getGraphicsDevice().createRenderTarget(engine.getWindow().getWidth(), engine.getWindow().getHeight());

        lightingShader = engine.getAssetLoader().loadShader("fx/defaultFX.vs.glsl", "fx/defaultLighting.fs.glsl");
        lightingShader.attachUniformBuffer(5, lightUniform);
        lightingShader.attachUniformBuffer(6, cameraUniform);
        lightingShader.attachTexture("ambientTexture", geometryScene.getColorTexture(0));
        lightingShader.attachTexture("diffuseTexture", geometryScene.getColorTexture(1));
        lightingShader.attachTexture("specularTexture", geometryScene.getColorTexture(2));
        lightingShader.attachTexture("normalTexture", geometryScene.getColorTexture(3));
        lightingShader.attachTexture("depthTexture", geometryScene.getDepthTexture());
    }

    @Override
    public void destroy(GameEngine engine) {
    }

    float angle = 0.0f;
    
    @Override
    public void update(GameEngine engine, double deltaTime) {
		engine.getWindow().setTitle("FPS: " + engine.getFPS());

		//entity.setLocalRotation(entity.getLocalRotation().add(new Vector3f(0.0f, 0.01f, 0.0f)));

		// TODO: Temp code
        PointLight light = new PointLight(null);
        light.setLocalPosition(
        		new Vector3f(
        				(float)Math.sin(angle) * 4.0f,
        				(float)Math.sin(angle * 4.0f) * 2.0f, 
        				(float)Math.cos(angle) * 4.0f));
        light.setRange(16.0f);
        light.setAmbientColor(new Vector3f(0.1f, 0.1f, 0.1f));
        light.setDiffuseColor(new Vector3f(1.0f, 1.0f, 1.0f));
		light.update();
		
		PointLight light2 = new PointLight(null);
        light2.setLocalPosition(
        		new Vector3f(
        				(float)Math.sin(angle) * -4.0f,
        				(float)Math.sin(angle * 4.0f) * -2.0f, 
        				(float)Math.cos(angle) * -4.0f));
        light2.setRange(16.0f);
        light2.setAmbientColor(new Vector3f(0.1f, 0.1f, 0.1f));
        light2.setDiffuseColor(new Vector3f(1.0f, 1.0f, 1.0f));
		light2.update();
		
		FloatBuffer lightBuffer = BufferUtils.createFloatBuffer(40);
		lightBuffer.put(light.getBufferData());
		lightBuffer.position(20);
		lightBuffer.put(light2.getBufferData());
		lightBuffer.flip();
        lightUniform.setData(lightBuffer);
		
        angle += 0.01f;
        
		entity.update();
		camera.update(); // TODO: Replace with SceneGraph update() from engine
        cameraUniform.setData(camera.getBufferData());
    }

    @Override
    public void render(GameEngine engine) {
        modelUniform.setData(entity.getBufferData());

        engine.getGraphicsContext().bindRenderTarget(geometryScene);
        engine.getGraphicsContext().clear();

        engine.getGraphicsContext().bindVertexArray(entity.getModel().getVertexArray());
        entity.getModel().getMeshes().forEach((Mesh mesh) -> {
            materialUniform.setData(mesh.getMaterial().getBufferData());
            engine.getGraphicsContext().bindShader(engine.getAssetLoader().getDefaultGeometryShader());
            engine.getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
        });
        engine.getGraphicsContext().unbindShader(engine.getAssetLoader().getDefaultGeometryShader());
        engine.getGraphicsContext().unbindVertexArray(entity.getModel().getVertexArray());

        engine.getGraphicsContext().bindRenderTarget(lightingScene);
        engine.getGraphicsContext().clear();

        engine.getGraphicsContext().bindVertexArray(quadModel.getVertexArray());
        engine.getGraphicsContext().bindShader(lightingShader);
        engine.getGraphicsContext().drawIndexed(quadModel.getMeshes().get(0).getIndexBuffer());
        engine.getGraphicsContext().unbindShader(lightingShader);
        engine.getGraphicsContext().unbindVertexArray(quadModel.getVertexArray());

        engine.getGraphicsContext().bindBackBuffer();
        engine.getGraphicsContext().clear();

        engine.getGraphicsContext().resolveToBackBuffer(lightingScene);
    }

    public static void main(String[] args) {
        GLBootstrap bootstrap = new GLBootstrap("Test Game", 1600, 900);
        bootstrap.run(MyGame::new);
    }
}
