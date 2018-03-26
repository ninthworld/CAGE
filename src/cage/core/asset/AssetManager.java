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
import cage.core.gui.graphics.GUIFont;
import cage.core.gui.graphics.GUIImage;
import cage.core.gui.GUIManager;
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

import cage.core.graphics.config.LayoutConfig;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;

import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private AssetProperties assetProperties;
    private GraphicsDevice graphicsDevice;
    private GUIManager guiManager;
    private Map<String, Model> models;
    private Map<String, Texture> textures;
    private Map<String, GUIFont> fonts;
    private Map<String, GUIImage> images;
    private Shader defaultGeometryShader;
    private Shader defaultSimpleGeometryShader;
    private Shader defaultLightingShader;
    private Shader defaultShadowShader;
    private Shader defaultFXAAShader;
    private Shader defaultSSAOShader;
    private Texture defaultNoiseTexture;
    private GUIFont defaultFont;

    public AssetManager(Path assetProperties, GraphicsDevice graphicsDevice, GUIManager guiManager) {
        this.assetProperties = new AssetProperties(assetProperties);
        this.graphicsDevice = graphicsDevice;
        this.guiManager = guiManager;

        this.models = new HashMap<>();
        this.textures = new HashMap<>();
        this.fonts = new HashMap<>();
        this.images = new HashMap<>();

        this.assetProperties.setDefault("assets.shaders.default.geometry.vertex", "geometry/material.vs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.fragment", "geometry/material.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.simple.vertex", "geometry/simple.vs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.simple.fragment", "geometry/simple.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.vertex", "fx/fx.vs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.lighting.fragment", "fx/lighting.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.shadow.fragment", "fx/shadow.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.fxaa.fragment", "fx/fxaa.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.ssao.fragment", "fx/ssao.fs.glsl");
        this.assetProperties.setDefault("assets.fonts.default", "arial.ttf");
        this.assetProperties.setDefault("assets.fonts.default.name", "Arial");
        this.assetProperties.setDefault("assets.textures.default.noise", "noise.bmp");

        this.defaultGeometryShader = loadShader("default.geometry");
        this.defaultSimpleGeometryShader = loadShader("default.geometry.simple");
        this.defaultLightingShader = loadShader("default.fx", "default.fx.lighting");
        this.defaultShadowShader = loadShader("default.fx", "default.fx.shadow");
        this.defaultFXAAShader = loadShader("default.fx", "default.fx.fxaa");
        this.defaultSSAOShader = loadShader("default.fx", "default.fx.ssao");
        this.defaultNoiseTexture = loadTexture("default.noise");
    }

    public void initialize() {
        this.defaultFont = loadFont("default");
    }

    public Shader getDefaultGeometryShader() {
        return defaultGeometryShader;
    }

    public Shader getDefaultSimpleGeometryShader() {
        return defaultSimpleGeometryShader;
    }

    public Shader getDefaultLightingShader() {
        return defaultLightingShader;
    }

    public Shader getDefaultShadowShader() {
        return defaultShadowShader;
    }

    public Shader getDefaultFXAAShader() {
        return defaultFXAAShader;
    }

    public Shader getDefaultSSAOShader() {
        return defaultSSAOShader;
    }

    public Texture getDefaultNoiseTexture() {
        return defaultNoiseTexture;
    }

    public GUIFont getDefaultFont() {
        return defaultFont;
    }

    public GUIFont loadFont(String configKey) {
        return loadFontFile(assetProperties.getValue("assets.fonts." + configKey + ".name"), assetProperties.getValuePath("assets.fonts." + configKey).toString());
    }

    public GUIFont loadFontFile(String name, String file) {
        String concat = file.concat(name);
        if(fonts.containsKey(concat)) {
            return fonts.get(concat);
        }
        else {
            try {
                Path path = assetProperties.getFontsPath().resolve(file);
                byte[] data = Files.readAllBytes(path);
                ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
                buffer.put(data);
                buffer.flip();
                GUIFont font = guiManager.createFont(name, buffer);
                fonts.put(concat, font);
                return font;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public GUIImage loadImage(String configKey) {
        return loadImageFile(assetProperties.getValuePath("assets.images." + configKey).toString());
    }

    public GUIImage loadImageFile(String file) {
        if(images.containsKey(file)) {
            return images.get(file);
        }
        else {
            ImageData img = getImageData(assetProperties.getImagesPath().resolve(file).toString());
            if(img == null) {
                return null;
            }
            GUIImage image = guiManager.createImage(img.width, img.height, (ByteBuffer)img.data);
            images.put(file, image);
            return image;
        }
    }

    public Model loadOBJModel(String configKey) {
        return loadOBJModelFile(assetProperties.getValuePath("assets.models." + configKey).toString());
    }

    public Model loadOBJModelFile(String file) {
        if(models.containsKey(file)) {
            return models.get(file);
        }
        else {
            try {
                Build builder = new Build();
                new Parse(builder, assetProperties.getModelsPath().resolve(file).toString());

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

                IntBuffer indices = BufferUtils.createIntBuffer(builder.faces.size() * 3);
                for (Face face : builder.faces) {
                    for (FaceVertex vertex : face.vertices) {
                        indices.put(indexMap.get(vertex));
                    }
                }
                indices.flip();

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

                VertexBuffer vertexBuffer = graphicsDevice.createVertexBuffer();
                vertexBuffer.setLayout(new LayoutConfig().float3().float2().float3().float3());
                vertexBuffer.setUnitCount(index);
                vertexBuffer.writeData(vertices);

                IndexBuffer indexBuffer = graphicsDevice.createIndexBuffer();
                indexBuffer.setUnitCount(builder.faces.size() * 3);
                indexBuffer.writeData(indices);

                VertexArray vertexArray = graphicsDevice.createVertexArray();
                vertexArray.addVertexBuffer(vertexBuffer);

                Material material = new Material();
                for (com.owens.oobjloader.builder.Material mtl : builder.materialLib.values()) {
                    material.setDiffuse((float) mtl.kd.rx, (float) mtl.kd.gy, (float) mtl.kd.bz);
                    material.setSpecular((float) mtl.ks.rx, (float) mtl.ks.gy, (float) mtl.ks.bz);
                    material.setShininess((float)mtl.nsExponent);

                    if(mtl.mapKdFilename != null) {
                        material.setDiffuse(loadTextureFile(mtl.mapKdFilename));
                    }

                    if(mtl.mapKsFilename != null) {
                        material.setSpecular(loadTextureFile(mtl.mapKsFilename));
                    }

                    if(mtl.mapNsFilename != null) {
                        material.setHighlight(loadTextureFile(mtl.mapNsFilename));
                    }
                }

                Model model = new Model(vertexArray);
                model.addMesh(new Mesh(indexBuffer, material));

                models.put(file, model);
                return model;
            } catch (IOException e) {
                System.err.println("Failed to load model '" + file + "'");
                e.printStackTrace();
            }
        }
        return null;
    }

    public Shader loadShader(String configKey) {
        return loadShader(configKey, configKey, configKey);
    }

    public Shader loadShader(String configKeyVertex, String configKeyFragment) {
        return loadShader(configKeyVertex, "", configKeyFragment);
    }

    public Shader loadShader(String configKeyVertex, String configKeyGeometry, String configKeyFragment) {
        return loadShaderFile(
                assetProperties.getValuePath("assets.shaders." + configKeyVertex + ".vertex").toString(),
                assetProperties.getValuePath("assets.shaders." + configKeyGeometry + ".geometry").toString(),
                assetProperties.getValuePath("assets.shaders." + configKeyFragment + ".fragment").toString());
    }

    public Shader loadShaderFile(String vertexFile, String fragmentFile) {
        return loadShaderFile(vertexFile, "", fragmentFile);
    }

    public Shader loadShaderFile(String vertexFile, String geometryFile, String fragmentFile) {
        Shader shader = graphicsDevice.createShader();
        try {
            String line;

            BufferedReader vertexReader = Files.newBufferedReader(assetProperties.getShadersPath().resolve(vertexFile));
            StringBuilder vertexShaderSrc = new StringBuilder();
            while ((line = vertexReader.readLine()) != null) {
                vertexShaderSrc.append(line).append("\n");
            }
            shader.setVertexShaderSource(vertexShaderSrc.toString());

            BufferedReader fragmentReader = Files.newBufferedReader(assetProperties.getShadersPath().resolve(fragmentFile));
            StringBuilder fragmentShaderSrc = new StringBuilder();
            while ((line = fragmentReader.readLine()) != null) {
                fragmentShaderSrc.append(line).append("\n");
            }
            shader.setFragmentShaderSource(fragmentShaderSrc.toString());

            if(!geometryFile.isEmpty()) {
                BufferedReader geometryReader = Files.newBufferedReader(assetProperties.getShadersPath().resolve(geometryFile));
                StringBuilder geometryShaderSrc = new StringBuilder();
                while ((line = geometryReader.readLine()) != null) {
                    geometryShaderSrc.append(line).append("\n");
                }
                shader.setGeometryShaderSrc(geometryShaderSrc.toString());
            }

            shader.compile();
        } catch(IOException e) {
            System.err.println("Failed to load shader");
            e.printStackTrace();
        }
        return shader;
    }

    public Texture2D loadTexture(String configKey) {
        return loadTextureFile(assetProperties.getValuePath("assets.textures." + configKey).toString());
    }

    public Texture2D loadTextureFile(String file) {
        Texture tex;
        if(textures.containsKey(file) && (tex = textures.get(file)) instanceof Texture2D) {
            return (Texture2D)tex;
        }
        else {
            ImageData img = getImageData(assetProperties.getTexturesPath().resolve(file).toString());
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

    public TextureCubeMap loadCubeMap(String configKey) {
        return loadCubeMapFile(
                assetProperties.getValuePath("assets.textures." + configKey + ".right").toString(),
                assetProperties.getValuePath("assets.textures." + configKey + ".left").toString(),
                assetProperties.getValuePath("assets.textures." + configKey + ".top").toString(),
                assetProperties.getValuePath("assets.textures." + configKey + ".bottom").toString(),
                assetProperties.getValuePath("assets.textures." + configKey + ".back").toString(),
                assetProperties.getValuePath("assets.textures." + configKey + ".front").toString());
    }

    public TextureCubeMap loadCubeMapFile(String rightFile, String leftFile, String topFile, String bottomFile, String backFile, String frontFile) {
        String[] files = new String[] {
            rightFile, leftFile, topFile, bottomFile, backFile, frontFile
        };

        String concat = Arrays.toString(files);
        Texture tex;
        if(textures.containsKey(concat) && (tex = textures.get(concat)) instanceof TextureCubeMap) {
            return (TextureCubeMap)tex;
        }
        else {
            ImageData[] imgs = new ImageData[6];
            for(int i=0; i<imgs.length; ++i) {
                imgs[i] = getImageData(assetProperties.getTexturesPath().resolve(files[i]).toString());
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

    private ImageData getImageData(String file) {
        // TODO: Add support for 16-bit PNG
        ByteBuffer data;
        int width, height;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            data = STBImage.stbi_load(file, w, h, comp, 4);
            if(data == null) {
                System.err.println("Failed to load texture '" + file + "'\n" + STBImage.stbi_failure_reason());
                return null;
            }
            width = w.get();
            height = h.get();
        }

        ImageData img = new ImageData();
        img.width = width;
        img.height = height;
        img.data = data;
        return img;
    }

    private class ImageData {
        public int width;
        public int height;
        public Buffer data;
    }

    static {
        Logger.getLogger(Parse.class.getName()).setLevel(Level.SEVERE);
        Logger.getLogger(Build.class.getName()).setLevel(Level.SEVERE);
    }
}
