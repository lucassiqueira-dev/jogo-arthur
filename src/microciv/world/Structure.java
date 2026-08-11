package microciv.world;
public class Structure {
    
    private String ownerId;

    public String name;
    public String colorCode;
    
    public int minimumRequiredTerrainId = 1;
    public int maximumRequiredTerrainId = 3;

    public int id;

    public int developmentLevel = 1; // The development level of the structure, starting at 1.

    public int woodCost = 0;
    public int foodCost = 0;
    public int stoneCost = 0;

    public int woodProduction = 0;
    public int foodProduction = 0;
    public int stoneProduction = 0;

    public int turnsToBuild = 1;

    public String buildingAnouncement;

    public boolean canBuild(int terrainTileId){
        if (this.minimumRequiredTerrainId >= terrainTileId && terrainTileId <= maximumRequiredTerrainId){
            return true;
        }
        return false;
    }

    // Getters and Setters
    public String getOwnerId() {
        return ownerId;
    }
    
    // Later change into a conquered structure, so that the owner can be changed. For now, it will be set once and never changed.
    public void setOwnerId(String ownerId) {
        if(this.ownerId != null) {
            throw new IllegalArgumentException("This structure already has an owner.");
        }
        this.ownerId = ownerId;
    }

}
