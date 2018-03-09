package cage.core.application;

public abstract class GameTimer {

    protected double m_startTime;
    protected double m_lastTime;

    protected GameTimer() {
        m_startTime = 0.0;
        m_startTime = getTime();
        reset();
    }

    public void reset() {
        m_lastTime = getTime();
    }

    public double getElapsed() {
        return getTime() - m_lastTime;
    }

    public abstract double getTime();
}
