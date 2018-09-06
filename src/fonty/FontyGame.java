package fonty;

import cage.core.application.Game;
import cage.core.engine.Engine;
import cage.core.graphics.GraphicsContext;
import cage.core.graphics.buffer.Buffer;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.rendertarget.RenderTarget2D;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.type.*;
import cage.core.input.action.InputAction;
import cage.core.input.action.InputEvent;
import cage.core.input.component.Key;
import cage.core.input.type.InputActionType;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.render.RenderManager;
import cage.core.render.stage.FXRenderStage;
import cage.glfw.GLFWBootstrap;
import cage.opengl.graphics.shader.GLShader;
import cage.opengl.graphics.texture.GLTexture;
import cage.opengl.graphics.type.GLTypeUtils;
import cage.opengl.utils.GLUtils;
import com.sun.prism.impl.BufferUtil;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FontyGame implements Game {

    private Shader sdfShader;

    private Vector2f offset = new Vector2f();
    private float scale = 1.0f;
    private int size = 64 * 16;
    private boolean renderSDF = true;

    public FontyGame(Engine engine, String[] args) {
    }

    @Override
    public void initialize(Engine engine) {

        long time = System.currentTimeMillis();
        Texture2D fontTexture = engine.getAssetManager().loadTextureFile("../../out.png");
//        Texture2D fontTexture = createFontSDF_CS("roboto/RobotoCondensed-Regular.ttf", 64, engine);
//        System.out.printf("Done in %d ms", (int)(System.currentTimeMillis() - time));
//        saveTexture(fontTexture, "out.png", engine);

        UniformBuffer transformBuffer = engine.getGraphicsDevice().createUniformBuffer();
        transformBuffer.setLayout(new LayoutConfig().float2().float2());

        sdfShader = engine.getAssetManager().loadShaderFile("fonty/sdf.vs.glsl", "fonty/sdf.fs.glsl");
        sdfShader.addUniformBuffer("Window", engine.getRenderManager().getDefaultWindowUniformBuffer());
        sdfShader.addUniformBuffer("Transform", transformBuffer);

        engine.getGraphicsDevice().getDefaultBlender().setBlend(BlendType.SRC_ALPHA, BlendType.INV_SRC_ALPHA, BlendOpType.ADD);
        engine.getRenderManager().getDefaultFXModel().getMesh(0).getRasterizer().setCullType(CullType.NONE);

        FXRenderStage sdfRenderStage = engine.getRenderManager().createFXRenderStage(sdfShader, (fxModel, shader, renderTarget, graphicsContext) -> new FXRenderStage(fxModel, shader, renderTarget, graphicsContext) {
            @Override
            public void midRender() {
                getGraphicsContext().bindRenderTarget(getRenderTarget());
                getGraphicsContext().clear();

                engine.getGraphicsContext().bindBlender(engine.getGraphicsDevice().getDefaultBlender());

                Mesh mesh = fxModel.getMesh(0);
                getGraphicsContext().setPrimitive(mesh.getPrimitive());
                getGraphicsContext().bindRasterizer(mesh.getRasterizer());
                getGraphicsContext().bindVertexArray(fxModel.getVertexArray());

                Matrix4f transform = new Matrix4f().identity();
                transform.translate(8.0f, 8.0f, 0.0f);
                transform.scale(1.0f);
                try(MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer buffer = stack.mallocFloat(4);
                    buffer.put(offset.x * scale).put(offset.y * scale);
                    buffer.put((float)size * scale).put((renderSDF ? 1.0f : 0.0f));
                    buffer.rewind();
                    transformBuffer.writeData(buffer);
                }
                sdfShader.addTexture("u_texture", fontTexture);

                getGraphicsContext().bindShader(getShader());
                getGraphicsContext().drawIndexed(mesh.getIndexBuffer());
            }
        });
        engine.getRenderManager().addOutputRenderStage(sdfRenderStage);

        float speed = 128.0f;
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.LEFT, InputActionType.REPEAT, (deltaTime, event) -> offset.x += deltaTime * speed);
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.RIGHT, InputActionType.REPEAT, (deltaTime, event) -> offset.x -= deltaTime * speed);
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.UP, InputActionType.REPEAT, (deltaTime, event) -> offset.y += deltaTime * speed);
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.DOWN, InputActionType.REPEAT, (deltaTime, event) -> offset.y -= deltaTime * speed);
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.PAGEUP, InputActionType.PRESS, (deltaTime, event) -> scale *= 1.2f);
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.PAGEDOWN, InputActionType.PRESS, (deltaTime, event) -> scale /= 1.2f);
        engine.getInputManager().addAction(engine.getInputManager().getKeyboardController(), Key.DELETE, InputActionType.PRESS, (deltaTime, event) -> renderSDF = !renderSDF);
    }

    private Texture2D createFontSDF(String fontFile, int glyphSize, Engine engine) {
        TrueTypeFont font = null;
        try {
            RandomAccessFile file = new RandomAccessFile("assets/fonts/" + fontFile, "r");
            FileChannel in = file.getChannel();
            ByteBuffer buffer = BufferUtils.createByteBuffer((int)in.size());
            in.read(buffer);
            buffer.flip();
            font = new TrueTypeFont(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SDF sdf = new SDF(engine.getGraphicsDevice(), font);
        return sdf.generateFontSDF(glyphSize);
    }

    private Texture2D createFontSDF_CS(String fontFile, int glyphSize, Engine engine) {
        final int MAX_GLYPH = 256;

        TrueTypeFont font = null;
        try {
            RandomAccessFile file = new RandomAccessFile("assets/fonts/" + fontFile, "r");
            FileChannel in = file.getChannel();
            ByteBuffer buffer = BufferUtils.createByteBuffer((int)in.size());
            in.read(buffer);
            buffer.flip();
            font = new TrueTypeFont(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Shader computeShader = engine.getAssetManager().loadShaderFile("compute/GenerateFontSDF.cs.glsl");

        ShaderStorageBuffer glyphInfo = engine.getGraphicsDevice().createShaderStorageBuffer();
        ShaderStorageBuffer glyphData = engine.getGraphicsDevice().createShaderStorageBuffer();

        int[] numPoints = new int[MAX_GLYPH];
        int[] numContours = new int[MAX_GLYPH];
        int[] offsets = new int[MAX_GLYPH];
        int[] min = new int[MAX_GLYPH * 2];
        int[] max = new int[MAX_GLYPH * 2];

        TrueTypeFont.Glyph[] glyphs = new TrueTypeFont.Glyph[MAX_GLYPH];
        int off = 0;
        for(int i=0; i<MAX_GLYPH; ++i) {
            glyphs[i] = font.readGlyph(i);
            TrueTypeFont.Glyph glyph = glyphs[i];
            if(glyph == null || glyph.contourEnds == null || glyph.points == null) continue;
            numPoints[i] = glyph.points.length;
            numContours[i] = glyph.contourEnds.length;
            offsets[i] = off;
            off += numContours[i] + numPoints[i] * 3;
            min[i * 2] = glyph.xMin;
            min[i * 2 + 1] = glyph.yMin;
            max[i * 2] = glyph.xMax;
            max[i * 2 + 1] = glyph.yMax;
        }

        IntBuffer glyphInfoBuffer = BufferUtils.createIntBuffer(MAX_GLYPH * 7);
        glyphInfoBuffer.put(numPoints).put(numContours).put(offsets).put(min).put(max);
        glyphInfoBuffer.rewind();
        glyphInfo.writeData(glyphInfoBuffer);

        FloatBuffer glyphDataBuffer = BufferUtils.createFloatBuffer(off);
        for(int i=0; i<MAX_GLYPH; ++i) {
            TrueTypeFont.Glyph glyph = glyphs[i];
            if(glyph == null || glyph.contourEnds == null || glyph.points == null) continue;
            for(int j=0; j<glyph.contourEnds.length; ++j) {
                glyphDataBuffer.put(glyph.contourEnds[j]);
            }
            for(int j=0; j<glyph.points.length; ++j) {
                glyphDataBuffer.put(glyph.points[j].x);
                glyphDataBuffer.put(glyph.points[j].y);
                glyphDataBuffer.put(glyph.points[j].onCurve ? 1 : 0);
            }
        }
        glyphDataBuffer.rewind();
        glyphData.writeData(glyphDataBuffer);

        computeShader.addShaderStorageBuffer(1, glyphInfo);
        computeShader.addShaderStorageBuffer(2, glyphData);

//        RenderTarget renderTarget = engine.getGraphicsDevice().createRenderTarget2D((int)Math.sqrt(MAX_GLYPH) * GLYPH_SIZE, (int)Math.sqrt(MAX_GLYPH) * GLYPH_SIZE);
        Texture2D texture = engine.getGraphicsDevice().createTexture2D((int)Math.sqrt(MAX_GLYPH) * glyphSize, (int)Math.sqrt(MAX_GLYPH) * glyphSize); //renderTarget.getColorTexture(0);
        computeShader.addTexture(0, texture, ImageType.WRITE_ONLY);

        engine.getGraphicsContext().bindShader(computeShader);
        engine.getGraphicsContext().computeDispatch(32, 32, 1);
        engine.getGraphicsContext().computeMemoryBarrier();

        return texture;
    }

    private void saveTexture(Texture2D texture, String outputFile, Engine engine) {
        RenderTarget2D renderTarget = engine.getGraphicsDevice().createRenderTarget2D();
        renderTarget.addColorTexture(0, texture);
        engine.getGraphicsContext().bindRenderTarget(renderTarget);
        ByteBuffer buffer = engine.getGraphicsContext().getPixels(new Rectangle(0, 0, texture.getWidth(), texture.getHeight()));

        BufferedImage image = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int x=0; x<texture.getWidth(); ++x) {
            for(int y=0; y<texture.getHeight(); ++y) {
                int i = (x + (texture.getWidth() * y)) * 4;
                int r = buffer.get(i) & 0xff;
                int g = buffer.get(i + 1) & 0xff;
                int b = buffer.get(i + 2) & 0xff;
                image.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        try {
            ImageIO.write(image, outputFile.substring(outputFile.lastIndexOf('.') + 1, outputFile.length()), new File(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(Engine engine) {
    }

    @Override
    public void update(Engine engine, float deltaTime) {

    }

    @Override
    public void render(Engine engine) {

    }

    public static void main(String[] args) {
        new GLFWBootstrap("Fonty", 1600, 900).run(engine -> new FontyGame(engine, args));
    }
}
