package cage.core.input.action;

public interface IInputAction {
    void performAction(float deltaTime, IInputEvent event);
}
