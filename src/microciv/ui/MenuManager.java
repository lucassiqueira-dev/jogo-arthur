package microciv.ui;

/*
    This class is responsible for managing the menu system of the game.
    It handles user input, displays options, and navigates through different menus based on player choices.
*/

import java.util.Scanner;

import microciv.App;
import microciv.society.Civilization;
import microciv.society.Culture;
import microciv.world.Map;

public class MenuManager {

    public static Map map;
    public static Civilization playerCiv;

    private static Scanner sc = new Scanner(System.in);

    public static void displayMainMenu() {
        System.out.println("\u001B[31mWelcome to MicroCiv: Island Expansion\u001B[0m");
        System.out.println("1) Start New Game");
        System.out.println("2) Load Game");
        System.out.println("3) Exit");

        System.out.print("Your choice: ");
        int choice = Integer.parseInt(sc.next());

        switch (choice) {
            case 1:
                // Start a new game
                map.generateIslandMap(); // Generate a new map of size 20
                
                playerCiv = createNewCivilization();
                map.printMap(); // Print the generated map

                displayGameMenu(playerCiv);
                
                break;
            case 2:
                // Load an existing game
                break;
            case 3:
                // Exit the game
                System.out.println("Exiting the game. Goodbye!");
                sc.close();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                displayMainMenu();
        }
    }

    public static Civilization createNewCivilization() {
        Civilization civ = new Civilization();

        System.out.print("Enter the name of your civilization: ");
        civ.civName = sc.next();

        System.out.print("Enter the name of your leader: ");
        civ.leaderName = sc.next();

        civ.foodResource = 100;
        civ.woodResource = 100;
        civ.stoneResource = 100;

        civ.setId("player1");
        civ.setCulture(new Culture()); // Set the default culture for the civilization

        return civ;
    }

    public static void displayGameMenu(Civilization civ) {
        System.out.println("Game Menu:");
        System.out.println("1) View Map/Resources");
        System.out.println("2) View Population/Send Settlers");
        System.out.println("3) Build Structure");
        System.out.println("4) End Turn");
        System.out.println("5) Save Game");
        System.out.println("6) Exit to Main Menu");

        System.out.print("Your choice: ");
        int choice = Integer.parseInt(sc.next());

        switch (choice) {
            case 1:
                // View Map

                map.printMap();
                System.out.println("The current resources are as follows:");
                System.out.printf(" Food: %d Wood: %d Stone: %d\n", civ.foodResource, civ.woodResource, civ.stoneResource);
                displayGameMenu(civ); // Return to game menu after viewing the map
                break;
            case 2:
                // Send Settlers
                map.printPopulationMap();
                displayGameMenu(civ); // Return to game menu after viewing the population
                break;
            case 3:
                // Build Structure

                displayBuildMenu(civ);
                break;
            case 4:
                // End Turn
                civ.produceResources(); // Produce resources for the civilization
                civ.turnsTaken++; // Increment the number of turns taken
                System.out.println("Current turn: " + civ.turnsTaken);
                System.out.println("Turn ended. Resources have been produced.");
                displayGameMenu(civ); // Return to game menu after ending the turn
                break;
            case 5:
                // Save Game
                break;
            case 6:
                // Exit to Main Menu
                displayMainMenu();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                displayGameMenu(civ);
        }
    }

    public static void displayBuildMenu(Civilization civ) {
        System.out.println("Build Menu:");
        System.out.println("1) Build House");
        System.out.println("2) Build Farm");
        System.out.println("3) Build Logging Camp");
        System.out.println("4) Back to Game Menu");

        System.out.print("Your choice: ");
        int choice = Integer.parseInt(sc.next());

        if (choice == 4) displayGameMenu(civ); // Return to game menu if the player chooses to go back
        if (choice < 1 || choice > 4) {
            System.out.println("Invalid choice. Please try again.");
            displayBuildMenu(civ);
            return;
        }
        
        System.out.println("You have chosen to build: " + App.structures.get(choice - 1).name);
        System.out.printf("This will cost: Food: %d, Wood: %d, Stone: %d\n", App.structures.get(choice - 1).foodCost, App.structures.get(choice - 1).woodCost, App.structures.get(choice - 1).stoneCost);

        int posX = 0, posY = 0;

        boolean hasBuilt = false;
        while (!hasBuilt) {
            
            System.out.print("Enter X coordinate: ");
            posX = Integer.parseInt(sc.next())-1;
            System.out.print("Enter Y coordinate: ");
            posY = Integer.parseInt(sc.next())-1;

            if (map.map[posY][posX].getOwner() != playerCiv) {
                System.out.println("That tile does not belong to you, building is not allowed there.");
                continue;
            }
            if (posX < 0 || posX >= map.size || posY < 0 || posY >= map.size) {
                System.out.println("Invalid coordinates. Please try again.");
                continue;
            }

            hasBuilt = true;
        }

        map.build(posX, posY, App.structures.get(choice - 1), civ);
        civ.structures.add(App.structures.get(choice - 1)); // Add the house to the civilization's structures
        displayGameMenu(civ); // Return to game menu after building
    }

}
