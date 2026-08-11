package microciv.world;
/*
    This class represents a tile on the game map.
    The tile can be of different types, such as water, land, or mountain.
    Each tile has an ID that corresponds to its type, and it can also hold a structure if one is built on it.
*/

import microciv.society.Civilization;

public class Tile {
    
    public int terrainId = 0; // 0 = water, 1 = land, 2 = forest, 3 = hill
    public Structure structure; // The structure built on this tile, if any
    private Civilization owner; // The civilization that owns this tile, if any

    private int population = 0; // The population on this tile, none by default.
    private int growthRate = 1; // The growth rate of the population on this tile, default is 1.
    //private int growthRate; // The growth rate of the population on this tile <- Apply on the future, use a default value for now.

    public Tile(int id) {
        this.terrainId = id;
        this.structure = null; // No structure by default
    }

    public void buildStructure(Structure structure) {
        this.structure = structure;
        updateTile();
    }

    public void removeStructure() {
        this.structure = null;
        updateTile();
    }

    private void updateTile(){
        this.growthRate = this.structure.developmentLevel - this.terrainId;
    }

    // Population management methods

    public void generateRandomPopulation(int factor) {
        // Generate a random population between 0 and 10 for now
        this.population = (int) ((Math.random() * 11) * factor)/10;
    }

    public void growPopulation() {  // Run this every turns
        this.population += this.population * (this.growthRate / 100.0); // Increase population by the growth rate for now
    }

    public int getPopulation() {
        return this.population;
    }

    // Getters and setters

    public void setOwner(Civilization owner) {
        this.owner = owner;
    }

    public Civilization getOwner() {
        return this.owner;
    }

}
