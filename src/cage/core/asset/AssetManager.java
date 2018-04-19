package cage.core.asset;

import animatedModel.AnimatedModel;
import animatedModel.Joint;
import animation.Animation;
import cage.core.graphics.*;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.texture.TextureCubeMap;
import cage.core.graphics.type.CubeFaceType;
import cage.core.graphics.type.FormatType;
import cage.core.graphics.vertexarray.VertexArray;
import cage.core.gui.graphics.GUIFont;
import cage.core.gui.graphics.GUIImage;
import cage.core.gui.GUIManager;
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

import cage.core.graphics.config.LayoutConfig;
import cage.core.model.ExtModel;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import colladaLoader.ColladaLoader;
import dataStructures.*;

import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loaders.AnimatedModelLoader;
import loaders.AnimationLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class AssetManager {

    private AssetProperties assetProperties;
    private GraphicsDevice graphicsDevice;
    private GUIManager guiManager;
    private Map<String, Model> models;
    private Map<String, ExtModel> extModels;
    private Map<String, Texture> textures;
    private Map<String, GUIFont> fonts;
    private Map<String, GUIImage> images;
    private Shader defaultGeometryShader;
    private Shader defaultAnimatedGeometryShader;
    private Shader defaultSimpleGeometryShader;
    private Shader defaultLightingShader;
    private Shader defaultShadowShader;
    private Shader defaultFXAAShader;
    private Texture defaultNoiseTexture;
    private GUIFont defaultFont;

    public AssetManager(Path assetProperties, GraphicsDevice graphicsDevice, GUIManager guiManager) {
        this.assetProperties = new AssetProperties(assetProperties);
        this.graphicsDevice = graphicsDevice;
        this.guiManager = guiManager;

        this.models = new HashMap<>();
        this.extModels = new HashMap<>();
        this.textures = new HashMap<>();
        this.fonts = new HashMap<>();
        this.images = new HashMap<>();

        this.assetProperties.setDefault("assets.shaders.default.geometry.vertex", "geometry/material.vs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.geometry", "geometry/material.gs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.fragment", "geometry/material.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.animated.vertex", "geometry/animated.material.vs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.simple.vertex", "geometry/simple.vs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.geometry.simple.fragment", "geometry/simple.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.vertex", "fx/fx.vs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.lighting.fragment", "fx/lighting.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.fx.fxaa.fragment", "fx/fxaa.fs.glsl");
        this.assetProperties.setDefault("assets.shaders.default.shadow.fragment", "shadow/shadow.fs.glsl");
        this.assetProperties.setDefault("assets.fonts.default", "arial.ttf");
        this.assetProperties.setDefault("assets.fonts.default.name", "Arial");
        this.assetProperties.setDefault("assets.textures.default.noise", "noise.bmp");

        this.defaultGeometryShader = loadShader("default.geometry");
        this.defaultAnimatedGeometryShader = loadShader("default.geometry.animated", "default.geometry", "default.geometry");
        this.defaultSimpleGeometryShader = loadShader("default.geometry.simple");
        this.defaultLightingShader = loadShader("default.fx", "default.fx.lighting");
        this.defaultShadowShader = loadShader("default.fx", "default.shadow");
        this.defaultFXAAShader = loadShader("default.fx", "default.fx.fxaa");
        this.defaultNoiseTexture = loadTexture("default.noise");
    }

    public void initialize() {
        this.defaultFont = loadFont("default");
    }

    public Shader getDefaultGeometryShader() {
        return defaultGeometryShader;
    }

    public Shader getDefaultAnimatedGeometryShader() {
        return defaultAnimatedGeometryShader;
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
            ImageData img = getImageData(assetProperties.getImagesPath().resolve(file).toString(), FormatType.RGBA_8_UNORM);
            if(img == null) {
                return null;
            }
            GUIImage image = guiManager.createImage(img.width, img.height, (ByteBuffer)img.data);
            images.put(file, image);
            return image;
        }
    }

    public ExtModel loadColladaModel(String configKey) {
    	return loadColladaModelFile(assetProperties.getValuePath("assets.models." + configKey).toString());
    }

    public ExtModel loadColladaModelFile(String file) {
    	if(extModels.containsKey(file)) {
    		return extModels.get(file);
    	}
    	else {
    	    Path path = assetProperties.getModelsPath().resolve(file);
    		AnimatedModelData modelData = ColladaLoader.loadColladaModel(path, 3);
    		SkeletonData skeletonData = modelData.getJointsData();
    		Joint headJoint = AnimatedModelLoader.createJoints(skeletonData.headJoint);
    		AnimatedModel animatedModel = new AnimatedModel(headJoint, skeletonData.jointCount);
    		Animation animation = AnimationLoader.loadAnimation(path);

            MeshData mesh = modelData.getMeshData();
            IntBuffer indices = BufferUtils.createIntBuffer(mesh.getIndices().length);
            indices.put(mesh.getIndices());
            indices.flip();

            FloatBuffer vertices = BufferUtils.createFloatBuffer(mesh.getVertexCount() * 3 * 2 * 3 * 3);
            for(int i=0; i<mesh.getVertexCount(); ++i) {
                vertices.put(mesh.getVertices()[(i * 3) + 0]);        
                vertices.put(mesh.getVertices()[(i * 3) + 1]); 
                vertices.put(mesh.getVertices()[(i * 3) + 2]);
                
                vertices.put(mesh.getTextureCoords()[(i * 2) + 0]);        
                vertices.put(mesh.getTextureCoords()[(i * 2) + 1]); 

                vertices.put(mesh.getNormals()[(i * 3) + 0]);        
                vertices.put(mesh.getNormals()[(i * 3) + 1]); 
                vertices.put(mesh.getNormals()[(i * 3) + 2]);

                vertices.put(mesh.getVertexWeights()[(i * 3) + 0]);        
                vertices.put(mesh.getVertexWeights()[(i * 3) + 1]); 
                vertices.put(mesh.getVertexWeights()[(i * 3) + 2]);
            }
            vertices.flip();
            
            FloatBuffer joints = BufferUtils.createFloatBuffer(mesh.getJointIds().length);
            for(int id : mesh.getJointIds()) {
                joints.put(id);
            }
            joints.flip();

            VertexBuffer vertexBuffer = graphicsDevice.createVertexBuffer();
            vertexBuffer.setLayout(new LayoutConfig().float3().float2().float3().float3());
            vertexBuffer.setUnitCount(mesh.getVertexCount());
            vertexBuffer.writeData(vertices);

            VertexBuffer jointBuffer = graphicsDevice.createVertexBuffer();
            jointBuffer.setLayout(new LayoutConfig().float3());
            jointBuffer.setUnitCount(mesh.getJointIds().length);
            jointBuffer.writeData(joints);

            IndexBuffer indexBuffer = graphicsDevice.createIndexBuffer();
            indexBuffer.setUnitCount(mesh.getIndices().length);
            indexBuffer.writeData(indices);

            VertexArray vertexArray = graphicsDevice.createVertexArray();
            vertexArray.addVertexBuffer(vertexBuffer);
            vertexArray.addVertexBuffer(jointBuffer);

            Material material = new Material();
            ExtModel model = new ExtModel(vertexArray, animatedModel, animation);
            model.addMesh(new Mesh(indexBuffer, material, graphicsDevice.getDefaultRasterizer()));

            extModels.put(file, model);
            return model;
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
                int index = 0;
                for (Face face : builder.faces) {
                    for (FaceVertex vertex : face.vertices) {
                        if (!indexMap.containsKey(vertex)) {
                            indexMap.put(vertex, index++);
                            faceVertices.add(vertex);
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

                FloatBuffer vertices = BufferUtils.createFloatBuffer(index * 3 * 2 * 3);
                for (int i = 0; i < faceVertices.size(); ++i) {
                    FaceVertex vertex = faceVertices.get(i);
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
                }
                vertices.flip();

                VertexBuffer vertexBuffer = graphicsDevice.createVertexBuffer();
                vertexBuffer.setLayout(new LayoutConfig().float3().float2().float3());
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
                model.addMesh(new Mesh(indexBuffer, material, graphicsDevice.getDefaultRasterizer()));

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
        shader.setVertexShaderSource(getShaderSource(assetProperties.getShadersPath().resolve(vertexFile).toString()));
        shader.setFragmentShaderSource(getShaderSource(assetProperties.getShadersPath().resolve(fragmentFile).toString()));
        if(!geometryFile.isEmpty()) {
            shader.setGeometryShaderSrc(getShaderSource(assetProperties.getShadersPath().resolve(geometryFile).toString()));
        }
        shader.compile();
        return shader;
    }

    public Texture2D loadTexture(String configKey) {
        return loadTexture(configKey, FormatType.RGBA_8_UNORM);
    }

    public Texture2D loadTexture(String configKey, FormatType format) {
        return loadTextureFile(assetProperties.getValuePath("assets.textures." + configKey).toString(), format);
    }

    public Texture2D loadTextureFile(String file) {
        return loadTextureFile(file, FormatType.RGBA_8_UNORM);
    }

    public Texture2D loadTextureFile(String file, FormatType format) {
        Texture tex;
        if(textures.containsKey(file) && (tex = textures.get(file)) instanceof Texture2D) {
            return (Texture2D)tex;
        }
        else {
            ImageData img = getImageData(assetProperties.getTexturesPath().resolve(file).toString(), format);
            if(img == null) {
                return null;
            }
            Texture2D texture = graphicsDevice.createTexture2D(img.width, img.height, format);
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
                imgs[i] = getImageData(assetProperties.getTexturesPath().resolve(files[i]).toString(), FormatType.RGBA_8_UNORM);
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

    private static final Pattern includePattern = Pattern.compile("^[ ]*#[ ]*include[ ]+[\\\"<](.*)[\\\">].*");
    private String getShaderSource(String file) {
        String src = "";
        try {
            Path path = Paths.get(file);
            BufferedReader reader = Files.newBufferedReader(path);
            StringBuilder shaderSrc = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher includeMatcher = includePattern.matcher(line);
                if(includeMatcher.matches()) {
                    shaderSrc.append(getShaderSource(path.getParent().resolve(includeMatcher.group(1)).toString()));
                }
                else {
                    shaderSrc.append(line).append("\n");
                }
            }
            src = shaderSrc.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return src;
    }

    private ImageData getImageData(String file, FormatType format) {
        Buffer data;
        int width, height;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            if(format.getBits() == 16) {
                data = STBImage.stbi_load_16(file, w, h, comp, format.getChannels());
            }
            else {
                data = STBImage.stbi_load(file, w, h, comp, format.getChannels());
            }
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
