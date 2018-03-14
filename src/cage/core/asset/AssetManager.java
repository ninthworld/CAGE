package cage.core.asset;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

import cage.core.graphics.IGraphicsDevice;
import cage.core.graphics.IndexBuffer;
import cage.core.graphics.Shader;
import cage.core.graphics.Texture;
import cage.core.graphics.VertexArray;
import cage.core.graphics.VertexBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.type.FormatType;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class AssetManager {

    public static final String ASSETS_ROOT_DIR = "assets/";

    private IGraphicsDevice graphicsDevice;
    private Path assetsDir;
    private Map<String, Texture> textures;
    private Shader defaultGeometryShader;
    private Shader defaultLightingShader;

    public AssetManager(IGraphicsDevice graphicsDevice) {
        this.graphicsDevice = graphicsDevice;
        this.assetsDir = Paths.get(ASSETS_ROOT_DIR);
        this.textures = new HashMap<>();
        this.defaultGeometryShader = loadShader("geometry/defaultGeometry.vs.glsl", "geometry/defaultGeometry.fs.glsl");
        this.defaultLightingShader = loadShader("fx/defaultFX.vs.glsl", "fx/defaultLighting.fs.glsl");
    }

    public Model loadOBJModel(String file) {
    	try {
        	Build builder = new Build();
        	Parse obj = new Parse(builder, assetsDir.resolve("models/").resolve(file).toString());    	
        	
        	HashMap<FaceVertex, Integer> indexMap = new HashMap<>();
        	ArrayList<FaceVertex> faceVertices = new ArrayList<>();
        	ArrayList<Vector3f> faceTangents = new ArrayList();
        	int index = 0;
        	for(Face face : builder.faces) {        		
        		FaceVertex f1 = face.vertices.get(0);
        		FaceVertex f2 = face.vertices.get(1);
        		FaceVertex f3 = face.vertices.get(2);
        		
        		Vector3f v1 = new Vector3f(f1.v.x, f1.v.y, f1.v.z);
        		Vector3f v2 = new Vector3f(f2.v.x, f2.v.y, f2.v.z);
        		Vector3f v3 = new Vector3f(f3.v.x, f3.v.y, f3.v.z);
        		
        		Vector2f w1 = new Vector2f(f1.t.u, f1.t.v);
        		Vector2f w2 = new Vector2f(f2.t.u, f2.t.v);
        		Vector2f w3 = new Vector2f(f3.t.u, f3.t.v);
        		
        		Vector3f s1 = v2.sub(v1);
        		Vector3f s2 = v3.sub(v1);
        		
        		Vector2f t1 = w2.sub(w1);
        		Vector2f t2 = w3.sub(w1);

                float r = 1.0f / (t1.x * t2.y - t2.x * t1.y);
                Vector3f tangent = new Vector3f(
                		t2.y * s1.x - t1.y * s2.x,
                		t2.y * s1.y - t1.y * s2.y,
                		t2.y * s1.z - t1.y * s2.z).mul(r);
        		
        		for(FaceVertex vertex : face.vertices) {
        			if(!indexMap.containsKey(vertex)) {
        				indexMap.put(vertex, index++);
        				faceVertices.add(vertex);
        				faceTangents.add(tangent);
        			}
        		}
        	}

        	FloatBuffer vertices = BufferUtils.createFloatBuffer(index * 3 * 2 * 3 * 3);
        	for(int i = 0; i < faceVertices.size(); ++i) {
        		FaceVertex vertex = faceVertices.get(i);
        		Vector3f tangent = faceTangents.get(i);
        		vertices.put(vertex.v.x).put(vertex.v.y).put(vertex.v.z);
        		vertices.put(vertex.t.u).put(vertex.t.v);
        		vertices.put(vertex.n.x).put(vertex.n.y).put(vertex.n.z);
        		vertices.put(tangent.x).put(tangent.y).put(tangent.z);
        	}
        	vertices.flip();
        	
        	IntBuffer indices = BufferUtils.createIntBuffer(builder.faces.size() * 3);
        	for(Face face : builder.faces) {
        		for(FaceVertex vertex : face.vertices) {
        			indices.put(indexMap.get(vertex));
        		}
        	}
        	indices.flip();

            VertexBuffer vertexBuffer = graphicsDevice.createVertexBuffer();
            vertexBuffer.setLayout(new LayoutConfig().float3().float2().float3().float3());
            vertexBuffer.setUnitCount(index);
            vertexBuffer.setData(vertices);
            
        	IndexBuffer indexBuffer = graphicsDevice.createIndexBuffer();
        	indexBuffer.setUnitCount(builder.faces.size() * 3);
        	indexBuffer.setData(indices);

        	VertexArray vertexArray = graphicsDevice.createVertexArray();
            vertexArray.attachVertexBuffer(vertexBuffer);

            // TODO: Temp Code
            Material material = new Material();
            material.setDiffuse(new Vector3f(1.0f, 1.0f, 1.0f));

            Mesh mesh = new Mesh(indexBuffer, material);

            Model model = new Model(vertexArray);
            model.attachMesh(mesh);
            
            return model;
    	} catch(FileNotFoundException e) {
    		e.printStackTrace();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
        return null;
    }

    public Shader loadShader(String vertexFile, String fragmentFile) {
        Shader shader = graphicsDevice.createShader();

        try {
            String line;

            BufferedReader vertexReader = Files.newBufferedReader(assetsDir.resolve("shaders/").resolve(vertexFile));
            StringBuilder vertexShaderSrc = new StringBuilder();
            while ((line = vertexReader.readLine()) != null) {
                vertexShaderSrc.append(line).append("\n");
            }

            BufferedReader fragmentReader = Files.newBufferedReader(assetsDir.resolve("shaders/").resolve(fragmentFile));
            StringBuilder fragmentShaderSrc = new StringBuilder();
            while ((line = fragmentReader.readLine()) != null) {
                fragmentShaderSrc.append(line).append("\n");
            }

            shader.setVertexShaderSource(vertexShaderSrc.toString());
            shader.setFragmentShaderSource(fragmentShaderSrc.toString());
            shader.compile();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return shader;
    }

    public Texture loadTexture(String file) {
        if(textures.containsKey(file)) {
            return textures.get(file);
        }
        else {
            return null;
        }
    }

    public Shader getDefaultGeometryShader() {
        return defaultGeometryShader;
    }

    public Shader getDefaultLightingShader() {
        return defaultLightingShader;
    }
}
