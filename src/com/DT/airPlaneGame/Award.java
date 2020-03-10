package com.DT.airPlaneGame;

/**
 *this is an interface which will be implemented by subclasses
 *
 */
public interface Award {
    public static final int DOUBLE_FIRE = 0;
    public static final int LIFE = 1;

    /**
     * get the type of Award
     * @return award type in int value
     */
    public abstract int getType();

}
