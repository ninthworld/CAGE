package cage.core.application;

import cage.core.engine.Engine;

public interface Game {
    void initialize(Engine engine);
    void destroy(Engine engine);
    void update(Engine engine, float deltaTime);
    void render(Engine engine);
}
