package cage.core.graphics.texture;

import cage.core.common.Destroyable;
import cage.core.common.Sizable;
import cage.core.common.Writable;
import cage.core.common.listener.Listener;
import cage.core.common.listener.ResizeListener;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.FormatType;

import java.nio.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Texture implements Destroyable, Sizable, Writable {

    private int width;
    private int height;
    private Sizable sizableParent;
    private ResizeListener resizeListener;
    private List<Listener> listeners;

    private FormatType format;
    private boolean mipmapping;
    private Sampler sampler;

    public Texture(int width, int height, FormatType format, boolean mipmapping) {
        this.format = format;
        this.mipmapping = mipmapping;
        this.sampler = null;

        this.width = width;
        this.height = height;
        this.sizableParent = null;
        this.resizeListener = null;
        this.listeners = new ArrayList<>();
    }

    public Texture(int width, int height, FormatType format) {
        this(width, height, format, false);
    }

    public Texture(int width, int height, boolean mipmapping) {
        this(width, height, FormatType.RGBA_8_UNORM, mipmapping);
    }

    public Texture(int width, int height) {
        this(width, height, FormatType.RGBA_8_UNORM, false);
    }

    public FormatType getFormat() {
        return format;
    }

    public void setFormat(FormatType format) {
        this.format = format;
    }

    public boolean isMipmapping() {
        return mipmapping;
    }

    public void setMipmapping(boolean mipmapping) {
        this.mipmapping = mipmapping;
    }

    public Sampler getSampler() {
        return sampler;
    }

    public void setSampler(Sampler sampler) {
        this.sampler = sampler;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        notifyResize();
    }

    @Override
    public void notifyResize() {
        for(Listener listener : listeners) {
            if(listener instanceof ResizeListener) {
                ((ResizeListener) listener).onResize(width, height);
            }
        }
    }

    @Override
    public Sizable getSizableParent() {
        return sizableParent;
    }

    @Override
    public ResizeListener getSizableParentListener() {
        return resizeListener;
    }

    @Override
    public void setSizableParent(Sizable parent) {
        setSizableParent(parent, this::setSize);
    }

    @Override
    public void setSizableParent(Sizable parent, ResizeListener listener) {
        if(hasSizableParent()) {
            removeSizableParent();
        }
        this.sizableParent = parent;
        this.resizeListener = listener;
        this.sizableParent.addListener(this.resizeListener);
    }

    @Override
    public void removeSizableParent() {
        this.sizableParent.removeListener(resizeListener);
        this.sizableParent = null;
        this.resizeListener = null;
    }

    @Override
    public boolean hasSizableParent() {
        return sizableParent != null;
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeListener(int index) {
        this.listeners.remove(index);
    }

    @Override
    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public int getListenerCount() {
        return listeners.size();
    }

    @Override
    public boolean containsListener(Listener listener) {
        return listeners.contains(listener);
    }

    @Override
    public Listener getListener(int index) {
        return listeners.get(index);
    }

    @Override
    public Iterator<Listener> getListenerIterator() {
        return listeners.iterator();
    }
}
