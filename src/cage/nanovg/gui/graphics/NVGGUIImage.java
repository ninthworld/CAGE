package cage.nanovg.gui.graphics;

import cage.core.gui.graphics.GUIImage;
import org.lwjgl.nanovg.NVGPaint;

import java.nio.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class NVGGUIImage extends GUIImage {

    private long context;
    private int id;
    private NVGPaint paint;

    public NVGGUIImage(long context, int width, int height, ByteBuffer data) {
        super(width, height);
        this.context = context;
        this.id = nvgCreateImageRGBA(context, width, height, NVG_IMAGE_REPEATX | NVG_IMAGE_REPEATY, data);
        this.paint = NVGPaint.calloc();
        notifyUpdate();
    }

    public int getId() {
        return id;
    }

    public NVGPaint getPaint() {
        return paint;
    }

    @Override
    public void notifyUpdate() {
        nvgImagePattern(context, getBounds().x, getBounds().y, getBounds().width, getBounds().height, getAngle(), id, getAlpha(), paint);
    }

    @Override
    public void destroy() {
        paint.free();
        nvgDeleteImage(context, id);
    }
}
