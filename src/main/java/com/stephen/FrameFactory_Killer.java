package com.stephen;

import java.util.ArrayList;

public interface FrameFactory_Killer<S extends StatHolder<S>> {
    Frame<S> createKillerFrame(ArrayList<S> parties, int lives);
}