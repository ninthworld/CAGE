package cage.core.application;

public abstract class GameTimer {

    protected double startTime;
    protected double lastTime;

    public GameTimer() {
        this.startTime = 0.0;
        this.startTime = getTime();
        reset();
    }

    public void reset() {
        this.lastTime = getTime();
    }

    public double getElapsed() {
        return getTime() - this.lastTime;
    }

    public abstract double getTime();
}
