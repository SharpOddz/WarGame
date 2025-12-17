package units;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Unit {
    private UUID id;
    private String name;
    private UnitDomain domain;
    private UnitType type;
    private NationType nation;
    private int strength;
    private int movement;
    private int attack;
    private int defense;
    private int range;
    private int col;
    private int row;
    private boolean entrenched;
    private boolean disrupted;

    @JsonCreator
    public Unit(@JsonProperty("id") UUID id,
                @JsonProperty("name") String name,
                @JsonProperty("domain") UnitDomain domain,
                @JsonProperty("type") UnitType type,
                @JsonProperty("nation") NationType nation,
                @JsonProperty("strength") int strength,
                @JsonProperty("movement") int movement,
                @JsonProperty("attack") int attack,
                @JsonProperty("defense") int defense,
                @JsonProperty("range") int range,
                @JsonProperty("col") int col,
                @JsonProperty("row") int row,
                @JsonProperty("entrenched") boolean entrenched,
                @JsonProperty("disrupted") boolean disrupted) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name == null ? "Unit" : name;
        this.domain = domain == null ? UnitDomain.GROUND : domain;
        this.type = type == null ? UnitType.INFANTRY : type;
        this.nation = nation == null ? NationType.NATO : nation;
        this.strength = strength;
        this.movement = movement;
        this.attack = attack;
        this.defense = defense;
        this.range = range;
        this.col = col;
        this.row = row;
        this.entrenched = entrenched;
        this.disrupted = disrupted;
    }

    public static Unit basic(UnitType type, NationType nation, int col, int row) {
        return new Unit(UUID.randomUUID(), type.name(), UnitDomain.GROUND, type, nation, 100, 6, 10, 10, 1, col, row, false, false);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public UnitDomain getDomain() { return domain; }
    public UnitType getType() { return type; }
    public NationType getNation() { return nation; }
    public int getStrength() { return strength; }
    public int getMovement() { return movement; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getRange() { return range; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public boolean isEntrenched() { return entrenched; }
    public boolean isDisrupted() { return disrupted; }

    public void setPosition(int col, int row) {
        this.col = col;
        this.row = row;
    }
}
