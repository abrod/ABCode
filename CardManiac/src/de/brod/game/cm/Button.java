package de.brod.game.cm;

import de.brod.opengl.OpenGLButton;

public abstract class Button extends OpenGLButton {

    public Button(float x1, float y1, float x2, float y2) {
        super(x1, y1, x2, y2);
        setUp(true);
    }

    @Override
    protected void organize() {
        // make nothing
    }

}
