package cage.core.gui.graphics;

import cage.core.common.Destroyable;

import java.awt.*;

public abstract class GUIImage implements Destroyable {

    private int width;
    private int height;
    private Rectangle bounds;
    private float angle;
    private float alpha;

    public GUIImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(0, 0, width, height);
        this.angle = 0.0f;
        this.alpha = 1.0f;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
        notifyUpdate();
    }

    public void setBounds(int startX, int startY, int endX, int endY) {
        setBounds(new Rectangle(startX, startY, endX, endY));
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        notifyUpdate();
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        notifyUpdate();
    }

    public void notifyUpdate() {
    }
}
