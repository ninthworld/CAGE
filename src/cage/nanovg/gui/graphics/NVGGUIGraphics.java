package cage.nanovg.gui.graphics;

import cage.core.gui.graphics.GUIFont;
import cage.core.gui.graphics.GUIGraphics;
import cage.core.gui.graphics.GUIImage;
import cage.core.gui.graphics.TextAlign;
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
    public void save() {
        nvgSave(context);
    }

    @Override
    public void restore() {
        nvgRestore(context);
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
    public void moveTo(float x, float y) {
        nvgMoveTo(context, x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        nvgLineTo(context, x, y);
    }

    @Override
    public void arc(float cx, float cy, float radius, float a0, float a1, int dir) {
        nvgArc(context, cx, cy, radius, a0, a1, dir);
    }

    @Override
    public void arcTo(float x1, float y1, float x2, float y2, float radius) {
        nvgArcTo(context, x1, y1, x2, y2, radius);
    }

    @Override
    public void bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
        nvgBezierTo(context, c1x, c1y, c2x, c2y, x, y);
    }

    @Override
    public void circle(float cx, float cy, float radius) {
        nvgCircle(context, cx, cy, radius);
    }

    @Override
    public void ellipse(float cx, float cy, float rx, float ry) {
        nvgEllipse(context, cx, cy, rx, ry);
    }

    @Override
    public void rect(float x, float y, float w, float h) {
        nvgRect(context, x, y, w, h);
    }

    @Override
    public void roundedRect(float x, float y, float w, float h, float radius) {
        nvgRoundedRect(context, x, y, w, h, radius);
    }

    @Override
    public void fill() {
        nvgFill(context);
    }

    @Override
    public void stroke() {
        nvgStroke(context);
    }

    @Override
    public void rotate(float angle) {
        nvgRotate(context, angle);
    }

    @Override
    public void scale(float x, float y) {
        nvgScale(context, x, y);
    }

    @Override
    public void translate(float x, float y) {
        nvgTranslate(context, x, y);
    }

    @Override
    public void skewX(float angle) {
        nvgSkewX(context, angle);
    }

    @Override
    public void skewY(float angle) {
        nvgSkewY(context, angle);
    }

    @Override
    public void setFill(float r, float g, float b, float a) {
        fillColor.r(r);
        fillColor.g(g);
        fillColor.b(b);
        fillColor.a(a);
        nvgFillColor(context, fillColor);
    }

    @Override
    public void setFill(GUIImage image) {
        if(image instanceof NVGGUIImage) {
            nvgFillPaint(context, ((NVGGUIImage)image).getPaint());
        }
    }

    @Override
    public void setStroke(float r, float g, float b, float a) {
        strokeColor.r(r);
        strokeColor.g(g);
        strokeColor.b(b);
        strokeColor.a(a);
        nvgStrokeColor(context, strokeColor);
    }

    @Override
    public void setStroke(GUIImage image) {
        if(image instanceof NVGGUIImage) {
            nvgStrokePaint(context, ((NVGGUIImage)image).getPaint());
        }
    }

    @Override
    public void setStrokeWidth(float size) {
        nvgStrokeWidth(context, size);
    }

    @Override
    public void setLineCap(int cap) {
        nvgLineCap(context, cap);
    }

    @Override
    public void setLineJoin(int join) {
        nvgLineJoin(context, join);
    }

    @Override
    public void setFont(GUIFont font) {
        if(font instanceof NVGGUIFont) {
            nvgFontFaceId(context, ((NVGGUIFont)font).getId());
        }
    }

    @Override
    public void setFont(String name) {
        nvgFontFace(context, name);
    }

    @Override
    public void setFontSize(int size) {
        nvgFontSize(context, size);
    }

    @Override
    public void setFontBlur(float blur) {
        nvgFontBlur(context, blur);
    }

    @Override
    public void drawText(float x, float y, String str) {
        nvgText(context, x, y, str);
    }

    @Override
    public void drawTextBox(float x, float y, float breakWidth, String str) {
        nvgTextBox(context, x, y, breakWidth, str);
    }

    @Override
    public void setTextAlign(TextAlign...align) {
        int a = 0;
        for(TextAlign ta : align) {
            switch (ta) {
                case LEFT: a |= NVG_ALIGN_LEFT; break;
                case CENTER: a |= NVG_ALIGN_CENTER; break;
                case RIGHT: a |= NVG_ALIGN_RIGHT; break;
                case TOP: a |= NVG_ALIGN_TOP; break;
                case MIDDLE: a |= NVG_ALIGN_MIDDLE; break;
                case BOTTOM: a |= NVG_ALIGN_BOTTOM; break;
                case BASELINE: a |= NVG_ALIGN_BASELINE; break;
            }
        }
        nvgTextAlign(context, a);
    }

    @Override
    public void setTextLetterSpacing(float spacing) {
        nvgTextLetterSpacing(context, spacing);
    }

    @Override
    public void setTextLineHeight(float height) {
        nvgTextLineHeight(context, height);
    }
}
