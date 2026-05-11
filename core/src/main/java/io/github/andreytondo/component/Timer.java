package io.github.andreytondo.component;

public class Timer {
    private final float duration;
    private float elapsed;
    private boolean running;

    public Timer(float duration) {
        this.duration = duration;
    }

    public void start() {
        elapsed = 0;
        running = true;
    }

    public void update(float delta) {
        if (running) {
            elapsed += delta;
            if (elapsed >= duration) {
                elapsed = duration;
                running = false;
            }
        }
    }

    public boolean isRunning() { return running; }
    public float getProgress() { return duration > 0 ? elapsed / duration : 1f; }
    public void reset() { elapsed = 0; running = false; }
}
