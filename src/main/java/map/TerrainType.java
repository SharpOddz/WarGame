package map;

public enum TerrainType {
    PLAIN,
    HILL,
    MOUNTAIN,
    FOREST,
    JUNGLE,
    LAKE,
    OCEAN,
    TOWN,
    CITY;

    public static TerrainType fromBiome(String biome, double value) {
        return switch (biome.toLowerCase()) {
            case "desert" -> value > 0.65 ? HILL : PLAIN;
            case "arctic" -> value > 0.7 ? MOUNTAIN : value > 0.5 ? LAKE : PLAIN;
            case "tropical" -> value > 0.7 ? JUNGLE : value > 0.5 ? FOREST : PLAIN;
            case "islands" -> value < 0.35 ? OCEAN : value < 0.45 ? LAKE : PLAIN;
            case "steppe" -> value > 0.7 ? HILL : PLAIN;
            default -> value > 0.75 ? FOREST : value > 0.6 ? HILL : PLAIN;
        };
    }
}
