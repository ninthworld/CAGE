package cage.core.model;

import animatedModel.AnimatedModel;
import animation.Animation;
import cage.core.graphics.vertexarray.VertexArray;

public class ExtModel extends Model {

	private AnimatedModel animatedModel;
	private Animation animation;
    private float animationSpeed;
    private boolean paused;
    private boolean finishAndPause;
    private float currentTime;

	public ExtModel(VertexArray vertexArray, AnimatedModel animatedModel, Animation animation) {
		super(vertexArray);
		this.animatedModel = animatedModel;
		this.animation = animation;
		this.animatedModel.doAnimation(animation);
		this.animationSpeed = 1.0f;
		this.paused = false;
		this.finishAndPause = false;
		this.currentTime = 0.0f;
	}

	public void update(float deltaTime) {
	    deltaTime *= animationSpeed;
        if(!paused) {
            if(finishAndPause) {
                if(currentTime + deltaTime >= animation.getLength()) {
                    deltaTime = animation.getLength() - currentTime + 0.001f;
                    paused = true;
                    finishAndPause = false;
                }
            }
            animatedModel.update(deltaTime);
            currentTime += deltaTime;
            currentTime %= animation.getLength();
        }
    }

	public AnimatedModel getAnimatedModel() {
		return animatedModel;
	}

    public float getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isFinishAndPause() {
        return finishAndPause;
    }

    public void togglePause() {
	    paused = !paused;
    }

    public void finishAndPause() {
	    finishAndPause = true;
    }

    public void stop() {
	    float deltaTime = animation.getLength() - currentTime + 0.001f;
        animatedModel.update(deltaTime);
        currentTime += deltaTime;
        currentTime %= animation.getLength();
	    paused = true;
    }

    public void start() {
        paused = false;
        finishAndPause = false;
    }
}
