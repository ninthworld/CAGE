package cage.core.scene.light;

public interface ShadowCastableLight {
    boolean isCastShadow();
    void setCastShadow(boolean castShadow);
}
