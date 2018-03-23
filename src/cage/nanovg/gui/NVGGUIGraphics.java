package cage.nanovg.gui;

import cage.core.gui.GUIGraphics;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

public class NVGGUIGraphics implements GUIGraphics {

    private long context;
    private NVGColor fillColor;
    private NVGColor strokeColor;

    public NVGGUIGraphics() {
    }

    public void initialize(long context) {
        this.context = context;
        this.fillColor = NVGColor.calloc();
        this.strokeColor = NVGColor.calloc();
    }

    @Override
    public void destroy() {
        strokeColor.free();
        fillColor.free();
    }

    @Override
    public void beginPath() {
        nvgBeginPath(context);
    }

    @Override
    public void closePath() {
        nvgClosePath(context);
    }

    @Override
    public void save() {
        nvgSave(context);
    }

    @Override
    public void restore() {
        nvgRestore(context);
    }

    @Override
    public void fill() {
        nvgFill(context);
    }

    @Override
    public void rect(float x, float y, float w, float h) {
        nvgRect(context, x, y, w, h);
    }

    @Override
    public void setFill(float r, float g, float b, float a) {
        fillColor.r(r);
        fillColor.g(g);
        fillColor.b(b);
        fillColor.a(a);
        nvgFillColor(context, fillColor);
    }
}
