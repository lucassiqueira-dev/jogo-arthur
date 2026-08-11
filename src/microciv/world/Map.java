package microciv.world;

import java.util.Random;

import microciv.society.Civilization;
import microciv.ui.MenuManager;

public class Map {
    
    public int size; // The size of the map (size x size)
    public Tile[][] map; // 2D array of tiles

    public float waterChance = 5f;
    public float originalWaterChance = waterChance;

    public float mountainChance = 7.5f;

    public float forestChance = 29.5f;
    public float originalForestChance = forestChance;

    public int centralizationFactorTownHall = 2;

    public int startingPopulationFactor = 50; // Only on town hall

    /*
    This will generate what each map tile actually is, everything starts
    as water as default;
    Each border tile should be a water tile, it should look organic.
    Land should always generate on the center tiles which is where the start is.
    Forests are generated lastly and appear on adjacent tiles to hills.
    Hills are formed in small clusters and cannot be in contact with water.
    */

    public Map(int size) {

        this.size = size;

        this.map = new Tile[size][size];

    }

    public void generateIslandMap() {
        // Generates land/water
        for (int i = 1; i < size-1; i++){ // Y
            //from 1 to size-2, border tiles are water

            for (int j = 1; j < size-1; j++){ // X
                
                // Makes the edges more jagged.
                waterChance = originalWaterChance;
                if (i == 1 || i == size-2) waterChance *= 10;
                if (j == 1 || j == size-2) waterChance *= 10;

                if (Math.random() * 100 > waterChance){
                    this.map[j][i] = new Tile(1); // Land tile
                    continue;
                } else {
                    this.map[j][i] = new Tile(0); // Water tile
                }
            }
        }

        

        // Generates hills

        /*
            The mountain map laters servers for forest generating, based off the "elevation".
        */

        Random rand = new Random();

        int[][] mountainMap = new int[this.size][this.size];

        for (int i = 3; i < this.size-3; i++){ // Y
            //from 3 to 17 (3 to inside) can actually be mountain

            for (int j = 3; j < this.size-3; j++){ // X
                                
                if (rand.nextInt(101) < mountainChance){
                    
                    // Check four surrounding tiles
                    if (
                        this.map[j-1][i].terrainId != 0 &&
                        this.map[j][i-1].terrainId != 0 &&
                        this.map[j+1][i].terrainId != 0 &&
                        this.map[j][i+1].terrainId != 0
                    ) {
                        this.map[j][i].terrainId = 3;
                        mountainMap[j][i] = 3;
                    }
                    continue;
                }
            }
        }

        // Forest generation

        for (int i = 2; i < this.size-2; i++){ // Y
            //from 2 to 18 (2 to inside)

            for (int j = 2; j < this.size-2; j++){ // X
                
                forestChance = originalForestChance;

                if (
                        this.map[j-1][i].terrainId == 3 ||
                        this.map[j][i-1].terrainId == 3 ||
                        this.map[j+1][i].terrainId == 3 ||
                        this.map[j][i+1].terrainId == 3
                ) {
                    forestChance *= 8;
                }

                if (rand.nextInt(101) < forestChance){
                    
                    this.map[j][i].terrainId = 2;

                    continue;
                }
            }
        }

        // Place the town hall somewhere close to the middle

        int middleX = (this.size/2)+rand.nextInt(-centralizationFactorTownHall,centralizationFactorTownHall);
        int middleY = (this.size/2)+rand.nextInt(-centralizationFactorTownHall,centralizationFactorTownHall);

        this.map[middleY][middleX].structure = StructureFactory.createTownHall(); // Town Hall
        this.map[middleY][middleX].generateRandomPopulation(this.startingPopulationFactor); // Generate a random population for the town hall tile
        this.map[middleY][middleX].setOwner(MenuManager.playerCiv); // Set the owner of the town hall tile to the player civilization

        acquireSurroundingTiles(middleX, middleY, MenuManager.playerCiv);

    }
    

    public void build(int x, int y, Structure building, Civilization civ){ // <- REFACTOR MEE!!! ! ! ! ! I did so!
        // REFACTOR THIS SHI AGAIN. Did it, but i think it's better now.
        /*
        Checks if the following tile is buildable, if so, builds it.
        */
        if (this.map[y][x].terrainId >= building.minimumRequiredTerrainId || this.map[y][x].terrainId <= building.maximumRequiredTerrainId) {
            if(civ.woodResource >= building.woodCost && civ.foodResource >= building.foodCost && civ.stoneResource >= building.stoneCost){

                civ.structures.add(building);
                
                civ.woodResource -= building.woodCost;
                civ.foodResource -= building.foodCost;
                civ.stoneResource -= building.stoneCost;

                this.map[y][x].structure = building;
                System.out.println(building.buildingAnouncement);

                acquireSurroundingTiles(x, y, civ);

                return;
            }
            System.out.println("Could not build, not enough resources.");
            return;
            
        }
        System.out.println("Could not build, land not buildable.");
        return;
    }
    
    public void acquireSurroundingTiles(int x, int y, Civilization civ){
        // Acquires the surrounding tiles of a given tile for a civilization.
        for (int i = y-1; i <= y+1; i++){
            for (int j = x-1; j <= x+1; j++){
                if (this.map[j][i] != null) {
                    this.map[j][i].setOwner(civ);
                }
            }
        }
    }

    public void printMap(){

        /* // Not working because of font size. :( only able to do it in a window.
        System.out.printf("   ");
        for(int i = 0; i < this.size; i++){
            // Print the X coordinate on the top of the map
            System.out.printf("%1d ",i+1);
        }

        System.out.println("");
        */
        for(int i = 0; i < this.size; i++){
            for (int j = 0; j < this.size; j++){

                if (j == 0) System.out.printf("%2d  ",i+1); // Print the Y coordinate on the left side of the map

                // I really don't know why i need this, but without it the map won't work.
                // If you happen to understand why and how to fix it, please do so. I will be very grateful.s
                if (this.map[j][i] == null) {
                    System.out.printf("\u001B[34m██\u001B[0m"); // Print water for null tiles
                    continue;
                }

                //System.out.printf(" %d ",map[j][i]);
                if (this.map[j][i].structure == null) {
                    switch (this.map[j][i].terrainId) {  // Remake to handle structures as well, if a structure is present, print it instead of the terrain.
                        case 0: // Water
                            System.out.printf("\u001B[34m██\u001B[0m");
                            break;
                        case 1: // Land
                            System.out.printf("\u001B[32m██\u001B[0m");
                            break;
                        case 2: // Forest
                            System.out.printf("\u001B[2;32m██\u001B[0m");
                            break;
                        case 3: // Hill
                            System.out.printf("\u001B[2m██\u001B[0m");
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.printf(this.map[j][i].structure.colorCode);
                }
            }
            System.out.println();
        }
    }

    public void printPopulationMap(){
        
        for(int i = 0; i < this.size; i++){
            for (int j = 0; j < this.size; j++){

                if (j == 0) System.out.printf("%2d  ",i+1); // Print the Y coordinate on the left side of the map

                if (this.map[j][i] == null) {
                    System.out.printf("  "); // Print empty space for null tiles
                    continue;
                }

                System.out.printf("%2d ",this.map[j][i].getPopulation());
            }
            System.out.println();
        }
    }

}
