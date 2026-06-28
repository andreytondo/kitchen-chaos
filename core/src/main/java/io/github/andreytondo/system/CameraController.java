package io.github.andreytondo.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.github.andreytondo.entity.BaseActor;
import io.github.andreytondo.utils.Constants;

public class CameraController {

    private static final float MIN_ZOOM    = 0.35f;
    private static final float MAX_ZOOM    = 1.5f;
    private static final float CAMERA_LERP = 5f;

    private final OrthographicCamera camera;
    private final Vector3 tmpVec = new Vector3();

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void update(float delta, BaseActor target) {
        float targetX = target.getX() + target.getWidth()  / 2f;
        float targetY = target.getY() + target.getHeight() / 2f;

        tmpVec.set(targetX, targetY, 0);
        camera.project(tmpVec);

        float screenW   = Gdx.graphics.getWidth();
        float screenH   = Gdx.graphics.getHeight();
        float deadZoneX = screenW * 0.25f;
        float deadZoneY = screenH * 0.25f;

        if (tmpVec.x < deadZoneX || tmpVec.x > screenW - deadZoneX
         || tmpVec.y < deadZoneY || tmpVec.y > screenH - deadZoneY) {
            camera.position.x += (targetX - camera.position.x) * CAMERA_LERP * delta;
            camera.position.y += (targetY - camera.position.y) * CAMERA_LERP * delta;
        }

        clamp();
        camera.update();
    }

    public void handleZoom(float scrollAmount) {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        tmpVec.set(mouseX, mouseY, 0);
        camera.unproject(tmpVec);
        float worldX = tmpVec.x;
        float worldY = tmpVec.y;

        camera.zoom = MathUtils.clamp(camera.zoom + scrollAmount * 0.1f, MIN_ZOOM, MAX_ZOOM);
        camera.update();

        tmpVec.set(mouseX, mouseY, 0);
        camera.unproject(tmpVec);
        camera.position.x += worldX - tmpVec.x;
        camera.position.y += worldY - tmpVec.y;

        clamp();
    }

    public void resize() {
        Vector3 savedPos  = camera.position.cpy();
        float   savedZoom = camera.zoom;
        camera.setToOrtho(false, Constants.ROOM_WIDTH, Constants.ROOM_HEIGHT);
        camera.position.set(savedPos);
        camera.zoom = savedZoom;
    }

    private void clamp() {
        float halfW = camera.viewportWidth  * camera.zoom * 0.5f;
        float halfH = camera.viewportHeight * camera.zoom * 0.5f;
        camera.position.x = MathUtils.clamp(camera.position.x,
            Math.min(halfW, Constants.ROOM_WIDTH  - halfW),
            Math.max(halfW, Constants.ROOM_WIDTH  - halfW));
        camera.position.y = MathUtils.clamp(camera.position.y,
            Math.min(halfH, Constants.ROOM_HEIGHT - halfH),
            Math.max(halfH, Constants.ROOM_HEIGHT - halfH));
    }
}
