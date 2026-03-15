package com.bankingsystem.util;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class AutoLogoutManager {
    private static final Duration TIMEOUT = Duration.minutes(5);
    private static PauseTransition timer;

    private AutoLogoutManager() {
    }

    public static void start(Runnable onTimeout) {
        stop();
        timer = new PauseTransition(TIMEOUT);
        timer.setOnFinished(event -> onTimeout.run());
        timer.playFromStart();
    }

    public static void reset() {
        if (timer != null) {
            timer.playFromStart();
        }
    }

    public static void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}