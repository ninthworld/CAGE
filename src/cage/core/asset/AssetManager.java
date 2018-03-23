package cage.core.asset;

import cage.core.graphics.*;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.texture.TextureCubeMap;
import cage.core.graphics.type.CubeFaceType;
import cage.core.graphics.vertexarray.VertexArray;
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

import cage.core.graphics.config.LayoutConfig;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class AssetManager {

    static {
        Logger.getLogger(Parse.class.getName()).setLevel(Level.SEVERE);
        Logger.getLogger(Build.class.getName()).setLevel(Level.SEVERE);
    }

    public static final String ASSETS_ROOT_DIR = "assets/";

    private GraphicsDevice graphicsDevice;
    private Path assetsDir;
    private Map<String, Model> models;
    private Map<String, Texture> textures;
    private Shader defaultGeometryShader;
    private Shader defaultLightingShader;

    public AssetManager(GraphicsDevice graphicsDevice) {
        this.graphicsDevice = graphicsDevice;
        this.assetsDir = Paths.get(ASSETS_ROOT_DIR);
        this.models = new HashMap<>();
        this.textures = new HashMap<>();
        this.defaultGeometryShader = loadShader("geometry/material.vs.glsl", "geometry/material.fs.glsl");
        this.defaultLightingShader = loadShader("fx/fx.vs.glsl", "fx/lighting.fs.glsl");
    }

    public Model loadOBJModel(String file) {
        if(models.containsKey(file)) {
            return models.get(file);
        }
        else {
            try {
                Build builder = new Build();
                new Parse(builder, assetsDir.resolve("models/").resolve(file).toString());

                HashMap<FaceVertex, Integer> indexMap = new HashMap<>();
                ArrayList<FaceVertex> faceVertices = new ArrayList<>();
                ArrayList<Vector3f> faceTangents = new ArrayList();
                int index = 0;
                for (Face face : builder.faces) {
                    Vector3f tangent = new Vector3f();
                    if(face.vertices.get(0).t != null) {
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
                        tangent = new Vector3f(
                                t2.y * s1.x - t1.y * s2.x,
                                t2.y * s1.y - t1.y * s2.y,
                                t2.y * s1.z - t1.y * s2.z).mul(r);
                    }
                    for (FaceVertex vertex : face.vertices) {
                        if (!indexMap.containsKey(vertex)) {
                            indexMap.put(vertex, index++);
                            faceVertices.add(vertex);
                            faceTangents.add(tangent);
                        }
                    }
                }

                FloatBuffer vertices = BufferUtils.createFloatBuffer(index * 3 * 2 * 3 * 3);
                for (int i = 0; i < faceVertices.size(); ++i) {
                    FaceVertex vertex = faceVertices.get(i);
                    Vector3f tangent = faceTangents.get(i);
                    vertices.put(vertex.v.x).put(vertex.v.y).put(vertex.v.z);
                    if(vertex.t != null) {
                        vertices.put(vertex.t.u).put(vertex.t.v);
                    }
                    else {
                        vertices.put(0.0f).put(0.0f);
                    }
                    if(vertex.n != null) {
                        vertices.put(vertex.n.x).put(vertex.n.y).put(vertex.n.z);
                    }
                    else {
                        vertices.put(0.0f).put(0.0f).put(0.0f);
                    }
                    vertices.put(tangent.x).put(tangent.y).put(tangent.z);
                }
                vertices.flip();

                IntBuffer indices = BufferUtils.createIntBuffer(builder.faces.size() * 3);
                for (Face face : builder.faces) {
                    for (FaceVertex vertex : face.vertices) {
                        indices.put(indexMap.get(vertex));
                    }
                }
                indices.flip();

                VertexBuffer vertexBuffer = graphicsDevice.createVertexBuffer();
                vertexBuffer.setLayout(new LayoutConfig().float3().float2().float3().float3());
                vertexBuffer.setUnitCount(index);
                vertexBuffer.writeData(vertices);

                IndexBuffer indexBuffer = graphicsDevice.createIndexBuffer();
                indexBuffer.setUnitCount(builder.faces.size() * 3);
                indexBuffer.writeData(indices);

                VertexArray vertexArray = graphicsDevice.createVertexArray();
                vertexArray.attachVertexBuffer(vertexBuffer);

                Material material = new Material();
                for (com.owens.oobjloader.builder.Material mtl : builder.materialLib.values()) {
                    material.setDiffuse((float) mtl.kd.rx, (float) mtl.kd.gy, (float) mtl.kd.bz);
                    material.setSpecular((float) mtl.ks.rx, (float) mtl.ks.gy, (float) mtl.ks.bz);
                    material.setShininess((float)mtl.nsExponent);

                    if(mtl.mapKdFilename != null) {
                        material.setDiffuse(loadTexture(mtl.mapKdFilename));
                    }

                    if(mtl.mapKsFilename != null) {
                        material.setSpecular(loadTexture(mtl.mapKsFilename));
                    }

                    if(mtl.mapNsFilename != null) {
                        material.setHighlight(loadTexture(mtl.mapNsFilename));
                    }
                }

                Model model = new Model(vertexArray);
                model.attachMesh(new Mesh(indexBuffer, material));

                models.put(file, model);
                return model;
            } catch (IOException e) {
                System.err.println("Failed to load model '" + file + "'");
                e.printStackTrace();
            }
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
            System.err.println("Failed to load shader");
            e.printStackTrace();
        }
        return shader;
    }

    public Texture2D loadTexture(String file) {
        Texture tex;
        if(textures.containsKey(file) && (tex = textures.get(file)) instanceof Texture2D) {
            return (Texture2D)tex;
        }
        else {
            Image img = loadImage(file);
            if(img == null) {
                return null;
            }
            Texture2D texture = graphicsDevice.createTexture2D(img.width, img.height);
            if(img.data instanceof ByteBuffer) {
                texture.writeData((ByteBuffer)img.data);
            }
            else if(img.data instanceof ShortBuffer) {
                texture.writeData((ShortBuffer)img.data);
            }
            textures.put(file, texture);
            return texture;
        }
    }

    public TextureCubeMap loadCubeMap(String rightFile, String leftFile, String topFile, String bottomFile, String backFile, String frontFile) {
        String[] files = new String[] {
            rightFile, leftFile, topFile, bottomFile, backFile, frontFile
        };

        String concat = Arrays.toString(files);
        Texture tex;
        if(textures.containsKey(concat) && (tex = textures.get(concat)) instanceof TextureCubeMap) {
            return (TextureCubeMap)tex;
        }
        else {
            Image[] imgs = new Image[6];
            for(int i=0; i<imgs.length; ++i) {
                imgs[i] = loadImage(files[i]);
                if(imgs[i] == null) {
                    return null;
                }
            }
            TextureCubeMap texture = graphicsDevice.createTextureCubeMap(imgs[0].width, imgs[0].height);
            for(int i=0; i<imgs.length; ++i) {
                texture.setDataCubeFace(CubeFaceType.values()[i]);
                if(imgs[i].data instanceof ByteBuffer) {
                    texture.writeData((ByteBuffer)imgs[i].data);
                }
                else if(imgs[i].data instanceof ShortBuffer) {
                    texture.writeData((ShortBuffer)imgs[i].data);
                }
            }
            textures.put(concat, texture);
            return texture;
        }
    }

    public Shader getDefaultGeometryShader() {
        return defaultGeometryShader;
    }

    public Shader getDefaultLightingShader() {
        return defaultLightingShader;
    }

    private Image loadImage(String file) {
        // TODO: Add support for 16-bit PNG
        ByteBuffer data;
        int width, height;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            data = STBImage.stbi_load(assetsDir.resolve("textures/").resolve(file).toString(), w, h, comp, 4);
            if(data == null) {
                System.err.println("Failed to load texture '" + file + "'\n" + STBImage.stbi_failure_reason());
                return null;
            }
            width = w.get();
            height = h.get();
        }

        Image img = new Image();
        img.width = width;
        img.height = height;
        img.data = data;
        return img;
    }

    private class Image {
        public int width;
        public int height;
        public Buffer data;
    }
}
