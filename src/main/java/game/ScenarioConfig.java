package game;

public class ScenarioConfig {
    private String biome;
    private int width;
    private int height;
    private String userNation;
    private String aiNation;
    private String aiDifficulty;
    private long seed;

    public ScenarioConfig() {}

    public ScenarioConfig(String biome, int width, int height, String userNation, String aiNation, String aiDifficulty, long seed) {
        this.biome = biome;
        this.width = width;
        this.height = height;
        this.userNation = userNation;
        this.aiNation = aiNation;
        this.aiDifficulty = aiDifficulty;
        this.seed = seed;
    }

    public String getBiome() {
        return biome;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUserNation() {
        return userNation;
    }

    public String getAiNation() {
        return aiNation;
    }

    public String getAiDifficulty() {
        return aiDifficulty;
    }

    public long getSeed() {
        return seed;
    }
}
