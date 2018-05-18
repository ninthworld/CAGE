package cage.core.gui.graphics;

import cage.core.common.Destroyable;

public interface GUIGraphics extends Destroyable {
    void save();
    void restore();

    void beginPath();
    void closePath();
    void moveTo(float x, float y);
    void lineTo(float x, float y);
    void arc(float cx, float cy, float r, float a0, float a1, int dir);
    void arcTo(float x1, float y1, float x2, float y2, float radius);
    void bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y);
    void circle(float cx, float cy, float radius);
    void ellipse(float cx, float cy, float rx, float ry);
    void rect(float x, float y, float w, float h);
    void roundedRect(float x, float y, float w, float h, float radius);

    void fill();
    void stroke();

    void rotate(float angle);
    void scale(float x, float y);
    void translate(float x, float y);
    void skewX(float angle);
    void skewY(float angle);

    void setFill(float r, float g, float b, float a);
    void setFill(GUIImage image);
    void setStroke(float r, float g, float b, float a);
    void setStroke(GUIImage image);

    void setStrokeWidth(float size);
    void setLineCap(int cap);
    void setLineJoin(int join);

    void setFont(GUIFont font);
    void setFont(String name);
    void setFontSize(int size);
    void setFontBlur(float blur);

    void drawText(float x, float y, String str);
    void drawTextBox(float x, float y, float breakWidth, String str);

    void setTextAlign(TextAlign...align);
    void setTextLetterSpacing(float spacing);
    void setTextLineHeight(float height);

}
