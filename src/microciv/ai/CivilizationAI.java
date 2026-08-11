package microciv.ai;

import microciv.society.Civilization;
import microciv.world.Map;
import microciv.App;

public class CivilizationAI extends Civilization {
    
    CivilizationAI(String id, String civName, String leaderName) {
        this.setId(id);
        this.civName = civName;
        this.leaderName = leaderName;
    }

    // Building behavior

    public void buildStructure() {
        
        if (this.woodResource <= this.foodResource && this.woodResource <= this.stoneResource) { // No wood
            //this.build(App.structures.get(2));
            this.structures.add(App.structures.get(2));
            
        } else if (this.foodResource <= this.woodResource && this.foodResource <= this.stoneResource) { // No food
            this.structures.add(App.structures.get(1));
        } else if (this.stoneResource <= this.woodResource && this.stoneResource <= this.foodResource) { // No stone
            this.structures.add(App.structures.get(3));
        }

    }

    public void findRequiredTiles(int x, int y, int targetTerrainId) {
        
    }

}
