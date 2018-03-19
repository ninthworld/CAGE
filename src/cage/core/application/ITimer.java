package cage.core.application;

public interface ITimer {
    void reset();
    float getElapsedTime();
    float getCurrentTime();
}
