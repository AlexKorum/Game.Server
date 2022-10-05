package server.game.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import server.game.server.actors.Tank;
import server.game.server.ws.WebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

@Component
public class GameLoop extends ApplicationAdapter {
    private static final float frameRate = 1 / 60f;
    private final WebSocketHandler socketHandler;

    private final Json json;
    private float lastRender = 0;
    private final ObjectMap<String, Tank> tanks = new ObjectMap<>();
    private final Array<Tank> stateToSend = new Array<>();
    private final ForkJoinPool pool = ForkJoinPool.commonPool();

    public GameLoop(WebSocketHandler socketHandler, Json json) {
        this.socketHandler = socketHandler;
        this.json = json;
    }

    @Override
    public void create() {
        socketHandler.setConnectListener(session -> {
            Tank tank = new Tank();
            tank.setId(session.getId());
            tanks.put(session.getId(), tank);
            try {
                session
                        .getNativeSession()
                        .getBasicRemote()
                        .sendText(String.format("{\"class\":\"sessionKey\", \"id\":\"%s\"}", session.getId()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        socketHandler.setDisconnectListener(session -> {
            sendToEverybody(
                    String.format("{\"class\":\"evict\", \"id\":\"%s\"}", session.getId())
            );
            tanks.remove(session.getId());
        });
        socketHandler.setMessageListener((session, message) -> {
            pool.execute(() -> {
                String type = message.get("type").asText();
                switch (type) {
                    case "state":
                        Tank tank = tanks.get(session.getId());
                        tank.setUp(message.get("up").asBoolean());
                        tank.setDown(message.get("down").asBoolean());
                        tank.setLeft(message.get("left").asBoolean());
                        tank.setRight(message.get("right").asBoolean());
                        tank.setAngle((float) message.get("angle").asDouble());
                        break;
                    default:
                        throw new RuntimeException("Unchown WS object type: " + type);
                }
            });
        });
    }

    @Override
    public void render() {
        lastRender += Gdx.graphics.getDeltaTime();
        if (lastRender >= frameRate) {
            stateToSend.clear();
            for (ObjectMap.Entry<String, Tank> tankEntry : tanks) {
                Tank tank = tankEntry.value;
                tank.act(lastRender);
                stateToSend.add(tank);
            }
            lastRender = 0;
            String stateJson = json.toJson(stateToSend);
            sendToEverybody(stateJson);
        }

    }

    private void sendToEverybody(String json) {
        pool.execute(() -> {
            for (StandardWebSocketSession session : socketHandler.getSessions()) {
                try {
                    if (session.isOpen()) {
                        session.getNativeSession().getBasicRemote().sendText(json);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
