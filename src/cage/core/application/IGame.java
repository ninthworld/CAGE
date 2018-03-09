package cage.core.application;

public interface IGame {

    void initialize(GameEngine engine);
    void destroy(GameEngine engine);

    void update(GameEngine engine, double deltaTime);
    void render(GameEngine engine);
}
