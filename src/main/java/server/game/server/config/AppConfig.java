package server.game.server.config;


import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.game.server.GameLoop;
import server.game.server.actors.Tank;

@Configuration
public class AppConfig {
    @Bean
    public HeadlessApplication getAplication(GameLoop gameLoop) {
        return new HeadlessApplication(gameLoop);
    }
    @Bean
    public Json getJson(){
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.addClassTag("tank", Tank.class);
        return json;
    }
}
