package cage.core.common;

import cage.core.common.listener.ResizeListener;

public interface Sizable extends Listenable {
    int getWidth();
    int getHeight();
    void setSize(int width, int height);
    void notifyResize();
    Sizable getSizableParent();
    ResizeListener getSizableParentListener();
    void setSizableParent(Sizable parent);
    void setSizableParent(Sizable parent, ResizeListener listener);
    void removeSizableParent();
    boolean hasSizableParent();
}
