package cage.core.common;

import cage.core.common.listener.Listener;

import java.util.Iterator;

public interface Listenable {
    void addListener(Listener listener);
    void removeListener(Listener listener);
    void removeListener(int index);
    void removeAllListeners();
    int getListenerCount();
    boolean containsListener(Listener listener);
    Listener getListener(int index);
    Iterator<Listener> getListenerIterator();
}
