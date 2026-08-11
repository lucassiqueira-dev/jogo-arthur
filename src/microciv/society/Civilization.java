package microciv.society;
import java.util.ArrayList;

import microciv.world.Structure;

public class Civilization {
    
    // This is for each player and AI on the game, on the first version there will only be the player.

    public int turnsTaken = 0;

    private String id;

    public String civName;
    public String leaderName;

    public int foodResource;
    public int woodResource;
    public int stoneResource;

    public ArrayList<Structure> structures = new ArrayList<>();

    private Culture culture;

    public String getId() {
        return this.id;
    }

    public void setCulture(Culture culture) { // Make some checks later
        this.culture = culture;
    }

    public Culture getCulture() {
        return this.culture;
    }

    // Once set, a civilization's ID cannot be changed.
    public void setId(String id) {
        if (this.id != null) {
            throw new IllegalArgumentException("This civilization already has an ID.");
        }
        this.id = id;
    }

    public void produceResources() {
        for (Structure structure : structures) {
            this.foodResource += structure.foodProduction;
            this.woodResource += structure.woodProduction;
            this.stoneResource += structure.stoneProduction;
        }
    }

}