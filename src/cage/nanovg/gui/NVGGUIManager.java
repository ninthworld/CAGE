package cage.nanovg.gui;

import cage.core.common.Destroyable;
import cage.core.gui.graphics.GUIFont;
import cage.core.gui.graphics.GUIImage;
import cage.core.gui.GUIManager;
import cage.core.window.Window;
import cage.nanovg.gui.graphics.NVGGUIFont;
import cage.nanovg.gui.graphics.NVGGUIGraphics;
import cage.nanovg.gui.graphics.NVGGUIImage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class NVGGUIManager extends GUIManager {

    private Window window;
    private long context;
    private List<Destroyable> guiObjects;

    public NVGGUIManager() {
        super(new NVGGUIGraphics());
        this.context = 0;
        this.guiObjects = new ArrayList<>();
    }

    public void initialize(Window window) {
        this.window = window;
        this.context = nvgCreate((window.getMultisampleCount() > 1 ? NVG_ANTIALIAS : 0) | NVG_STENCIL_STROKES);
        if(context == 0) {
            System.err.println("Failed to create NanoVG context");
        }
        ((NVGGUIGraphics)getGraphics()).initialize(context);
    }

    @Override
    public GUIImage createImage(int width, int height, ByteBuffer data) {
        GUIImage image = new NVGGUIImage(context, width, height, data);
        guiObjects.add(image);
        return image;
    }

    @Override
    public GUIFont createFont(String name, ByteBuffer data) {
        GUIFont font = new NVGGUIFont(context, name, data);
        guiObjects.add(font);
        return font;
    }

    @Override
    public void render() {
        nvgBeginFrame(context, window.getWidth(), window.getHeight(), 1);
        super.render();
        nvgEndFrame(context);
    }

    @Override
    public void destroy() {
        super.destroy();
        for(Destroyable obj : guiObjects) {
            obj.destroy();
        }
        nvgDelete(context);
    }
}
