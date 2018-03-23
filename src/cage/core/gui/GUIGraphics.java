package cage.core.gui;

import cage.core.common.IDestroyable;

public interface GUIGraphics extends IDestroyable {
    void beginPath();
    void closePath();
    void save();
    void restore();

    void fill();

    void rect(float x, float y, float w, float h);

    void setFill(float r, float g, float b, float a);


//    void arc(float cx, float cy, float r, float a0, float a1, int dir);
//    void arcTo(float x1, float y1, float x2, float y2, float radius);
//    void beginPath();
//    void closePath();
//    void bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y);
//    void circle(float cx, float cy, float radius);
//    void ellipse(float cx, float cy, float rx, float ry);
//    void fill();
//    void setFill(); // color or gradient
//    void setFontFace(String face);
//    void setFontSize(int size);
//    void setFontBlur(float blur);
//    void setLineCap(int cap);
//    void createLinearGradient();
//    void createBoxGradient();
//    void createFont();
//    void createImage();
}
