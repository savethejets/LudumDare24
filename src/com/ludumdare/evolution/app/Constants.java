package com.ludumdare.evolution.app;

public class Constants {
    public static final String MAIN_WINDOW_TITLE = "Ludum Dare 24";
    public static final String LOG = "LUDUMDARE-CODEHERD";

    //Box2d
    public static final Float BOX2D_SCALE_FACTOR = 32.0f;

    public static float convertToBox2d(float input) {
        return input / Constants.BOX2D_SCALE_FACTOR;
    }

    public static float convertFromBox2d(float input) {
        return input * Constants.BOX2D_SCALE_FACTOR;
    }
}
