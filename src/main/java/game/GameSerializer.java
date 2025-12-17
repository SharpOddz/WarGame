package game;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class GameSerializer {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void saveToFile(GameState state, File file) throws IOException {
        mapper.writeValue(file, state);
    }

    public static GameState loadFromFile(File file) throws IOException {
        GameState state = mapper.readValue(file, GameState.class);
        if (!"1.0".equals(state.getVersion())) {
            // future compatibility hook
        }
        return state;
    }
}
