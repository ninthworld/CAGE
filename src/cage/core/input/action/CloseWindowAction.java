package cage.core.input.action;

import cage.core.window.Window;

public class CloseWindowAction implements IAction {

    private Window window;

    public CloseWindowAction(Window window) {
        this.window = window;
    }

    @Override
    public void performAction(float deltaTime, IEvent event) {
        window.setClosed(true);
    }
}
