package cage.core.common;

import cage.core.common.listener.MoveListener;

public interface Movable extends Listenable {
    int getX();
    int getY();
    void setPosition(int x, int y);
    void notifyMove();
    Movable getMovableParent();
    MoveListener getMovableParentListener();
    void setMovableParent(Movable parent);
    void setMovableParent(Movable parent, MoveListener listener);
    void removeMovableParent();
    boolean hasMovableParent();
}
