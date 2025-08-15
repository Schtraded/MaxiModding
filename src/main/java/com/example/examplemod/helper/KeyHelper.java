package com.example.examplemod.helper;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

public class KeyHelper {
    public static void nullifyKeyPressedExceptF11() {
        while (Keyboard.next()) {
            int key = Keyboard.getEventKey();

            if (Keyboard.getEventKeyState()) { // true if key pressed, false if released
                if (key == Keyboard.KEY_F11 || key == Keyboard.KEY_ESCAPE) {
                    return;
                }
                Keyboard.destroy();
                try {
                    Keyboard.create();
                } catch (LWJGLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
