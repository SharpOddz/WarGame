package units;

public enum NationType {
    NATO,
    PACT,
    NON_ALIGNED,
    REGIONAL_POWER;

    public static NationType fromString(String value) {
        return switch (value.toLowerCase()) {
            case "pact" -> PACT;
            case "non-aligned" -> NON_ALIGNED;
            case "regional power" -> REGIONAL_POWER;
            default -> NATO;
        };
    }
}
