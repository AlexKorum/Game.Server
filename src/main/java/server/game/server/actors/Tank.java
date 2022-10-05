package server.game.server.actors;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Tank implements Json.Serializable{
    private String id;
    private float angle;
    private float x;
    private float y;
    private float speed = 300;

    private boolean up;
    private boolean down;
    private boolean left;


    @Override
    public void write(Json json) {
        json.writeValue("x", x);
        json.writeValue("y", y);
        json.writeValue("angle", angle);
        json.writeValue("id", id);
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {

    }
    public void act(float delta){
        float stepLength = speed * delta;
        if (isUp()) y += stepLength;
        if (isDown()) y -= stepLength;
        if (isLeft()) x -= stepLength;
        if (isRight()) x += stepLength;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    private boolean right;



}
