package cage.core.input.action;

public interface IAction {
    void performAction(float deltaTime, IEvent event);
}
