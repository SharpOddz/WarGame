package map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class EdgeFeature {
    private boolean[] rivers = new boolean[6];
    private boolean[] roads = new boolean[6];

    @JsonCreator
    public EdgeFeature(@JsonProperty("rivers") boolean[] rivers, @JsonProperty("roads") boolean[] roads) {
        if (rivers != null) this.rivers = Arrays.copyOf(rivers, 6);
        if (roads != null) this.roads = Arrays.copyOf(roads, 6);
    }

    public EdgeFeature() {}

    public boolean hasRiver(int direction) {
        return rivers[direction];
    }

    public void setRiver(int direction, boolean value) {
        rivers[direction] = value;
    }

    public boolean hasRoad(int direction) {
        return roads[direction];
    }

    public void setRoad(int direction, boolean value) {
        roads[direction] = value;
    }

    public boolean[] getRivers() {
        return rivers;
    }

    public boolean[] getRoads() {
        return roads;
    }
}
