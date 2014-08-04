package com.daviancorp.framework;

public abstract class Screen {
    protected final GameFramework game;

    public Screen(GameFramework game) {
        this.game = game;
    }

    public abstract void update(float deltaTime);

    public abstract void paint(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
    
    public abstract void backButton();
}
