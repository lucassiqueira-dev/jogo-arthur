package microciv.world;

    /*
    What each number id representes as a tile:

    # Natural
    0) water
    1) land
    2) forest
    3) hill

    # Buildings
    4) Town Hall
    5) Houses
    6) Farm
    7) Logging Camp
    */

public class StructureFactory {

    public static Structure createTownHall() {
        Structure townHall = new Structure();

        townHall.name = "Town Hall";
        townHall.colorCode = "\u001B[1m██\u001B[0m"; // Bold White

        townHall.id = 4;

        townHall.developmentLevel = 5;

        townHall.minimumRequiredTerrainId = 1;
        townHall.maximumRequiredTerrainId = 2;

        townHall.foodCost = 75;
        townHall.woodCost = 75;
        townHall.stoneCost = 75;

        townHall.foodProduction = 5;
        townHall.woodProduction = 5;
        townHall.stoneProduction = 5;

        townHall.buildingAnouncement = "A Town Hall was built last turn.";

        return townHall;
    }

    public static Structure createHouse() {
        Structure house = new Structure();

        house.name = "House";
        house.colorCode = "\u001B[31m██\u001B[0m"; // Red

        house.id = 5;

        house.minimumRequiredTerrainId = 1;
        house.maximumRequiredTerrainId = 2;

        house.developmentLevel = 3;
        
        house.foodCost = 15;
        house.woodCost = 10;

        house.buildingAnouncement = "A house was built last turn.";

        return house;
    }

    public static Structure createFarm() {
        Structure farm = new Structure();

        farm.name = "Farm";
        farm.colorCode = "\u001B[33m██\u001B]0m"; // Yellow

        farm.id = 6;

        farm.developmentLevel = 2;

        farm.minimumRequiredTerrainId = 1;
        farm.maximumRequiredTerrainId = 2;

        farm.foodCost = 10;
        farm.woodCost = 15;

        farm.foodProduction = 5;

        farm.buildingAnouncement = "A farm was built last turn.";

        return farm;
    }

    public static Structure createLoggingCamp() {
        Structure loggingCamp = new Structure();

        loggingCamp.name = "Logging Camp";
        loggingCamp.colorCode = "\u001B[35m██\u001B]0m"; // Has no color yet, find one out nig

        loggingCamp.id = 7;

        loggingCamp.minimumRequiredTerrainId = 2;
        loggingCamp.maximumRequiredTerrainId = 2;

        loggingCamp.foodCost = 20;
        loggingCamp.woodCost = 15;
        
        loggingCamp.woodProduction = 5;

        loggingCamp.buildingAnouncement = "A logging camp was built last turn.";

        return loggingCamp;
    }

    public static Structure createQuarry() {
        Structure quarry = new Structure();

        quarry.name = "Quarry";
        quarry.colorCode = "\u001B[37m██\u001B]0m"; // Has no color yet, find one out nig

        quarry.id = 8;

        quarry.minimumRequiredTerrainId = 2;
        quarry.maximumRequiredTerrainId = 2;

        quarry.foodCost = 20;
        quarry.woodCost = 15;
        
        quarry.woodProduction = 5;

        quarry.buildingAnouncement = "A quarry was built last turn.";

        return quarry;
    }
}
