package cage.nanovg.gui;

import cage.core.gui.GUIManager;
import cage.core.window.Window;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class NVGGUIManager extends GUIManager {

    private Window window;
    private long context;

    public NVGGUIManager() {
        super(new NVGGUIGraphics());
        this.context = 0;
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
    public void render() {
        nvgBeginFrame(context, window.getWidth(), window.getHeight(), 1);
        super.render();
        nvgEndFrame(context);
    }

    @Override
    public void destroy() {
        super.destroy();
        nvgDelete(context);
    }
}
