package fi.tuni.pepper.gamelogic;

import java.util.HashSet;

public class GameManager{

    //Map limits for 2D Array map
    public final int minCoord = 0;
    public final int maxCoord = 4;

    //Number of pits on the map
    public final int pitAmount = 2;

    //Number of bats
    public final int batSpotAmount = 1;

    //Strings for all the cells of the cave
    public final String playerMark = "[P]";
    public final String wumpusMark = "[W]";
    public final String batMark = "[B]";
    public final String pitMark = "[U]";
    public final String arrowMark = "[A]";
    public final String emptyMark = "[ ]";

    public String[][] gameMap = new String[5][5];

    public int colAnswer = 0;

    public String[][] generateMap(int playerY, int playerX, Wumpus wumpus) {
        //Generate an empty base layout
        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                gameMap[i][j] = emptyMark;
            }
        }

        //Place Player first
        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                gameMap[playerY][playerX] = playerMark;
            }
        }

        //Place Wumpus at free spot
        //Make sure the spot is free
        boolean wumpusPlaced = false;
        while(!wumpusPlaced){
            System.out.println("Wumpus at: " + wumpus.getWumpusYCoordinate() + ":" + wumpus.getWumpusXCoordinate());

            for(int i=0; i <= maxCoord; i++ ) {
                for(int j=0; j <= maxCoord; j++) {
                    //Consider only if spot is empty
                    if(gameMap[i][j] == emptyMark) {
                        //Place if coordinates for empty spot match
                        if(i == wumpus.getWumpusYCoordinate() && j == wumpus.getWumpusXCoordinate()) {
                            gameMap[i][j] = wumpusMark;
                            wumpusPlaced = true;
                        }
                    }
                }
            }

            //IF no spot, generate new coords for wumpus
            if(!wumpusPlaced){
                wumpus.setWumpusStartPosition(generateCoord(), generateCoord());
            }

        }


        //Generate hazards for all the rest free spots
        //Place pits for the map
        for(int k = 0; k < pitAmount; k++) {

            //Coords
            int pitY = (int) ((Math.random() * (maxCoord - minCoord + 1)) + minCoord);
            int pitX = (int) ((Math.random() * (maxCoord - minCoord + 1)) + minCoord);

            for(int i=0; i <= maxCoord; i++ ) {
                for(int j=0; j <= maxCoord; j++) {
                    //Check if spot is empty
                    if(gameMap[i][j] == emptyMark) {
                        //Place if coordinates for empty spot match
                        if(i == pitY && j == pitX) {
                            gameMap[i][j] = pitMark;
                        }
                    }
                }
            }
        }

        //Place bats spot
        //Place bat spot to a free location
        boolean batsPlaced = false;
        int batY = 0;
        int batX = 0;

        while(!batsPlaced) {
            batY = generateCoord();
            batX = generateCoord();
            for(int i=0; i <= maxCoord; i++ ) {
                for(int j=0; j <= maxCoord; j++) {
                    //Consider only if spot is empty
                    if(gameMap[i][j] == emptyMark) {
                        //Place if coordinates for empty spot match
                        if(i == batY && j == batX) {
                            gameMap[i][j] = batMark;
                            batsPlaced = true;
                        }
                    }
                }
            }
        }
        //Place arrows
        //Place arrow (might be spawned if free spot)
        //Coords
        int arrowY = generateCoord();
        int arrowX = generateCoord();

        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                //Check if spot is empty
                if(gameMap[i][j] == emptyMark) {
                    //Place if coordinates for empty spot match
                    if(i == arrowY && j == arrowX) {
                        gameMap[i][j] = arrowMark;
                    }
                }
            }
        }

        //return the generated map
        return gameMap;
    }

    //Generates int between 0-4 as coordinate
    public int generateCoord() {
        return (int) ((Math.random() * (maxCoord - minCoord + 1)) + minCoord);
    }

    //Compare aimed spot from player coords to actual Wumpus spot
    public boolean checkArrowHit(int arrowDir, Wumpus wumpus, int playerY, int playerX) {

        //Check wumpus position
        System.out.println("Wumpus at: " + wumpus.getWumpusYCoordinate()+  ":" + wumpus.getWumpusXCoordinate());

        //These will be the wumput spot coords
        //int shotY = wumpus.getWumpusYCoordinate();
        //int shotX = wumpus.getWumpusXCoordinate();

        //Decide target adjustments via arrowDir value
        switch(arrowDir){
            case 1:
                playerY--;
                break;
            case 2:
                playerY++;
                break;
            case 3:
                playerX--;
                break;
            case 4:
                playerX++;
                break;
        }

        System.out.println("Arrow flying into: " + playerY + ":" + playerX);

        //Check if game map spot has wumpus in it
        boolean shotHit = gameMap[playerY][playerX] == wumpusMark;
        System.out.println(shotHit);

        //Move wumpus if arrow did not hit
        if(!shotHit) {
            wumpus.moveWumpus();
        }

        return shotHit;
    }

    public void displayMap(String[][] gameMap) {
        //Display current map
        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                System.out.print(gameMap[i][j]);
            }
            System.out.println();
        }
    }

    //Update map changes, remove old markings for moving objects (player and wumpus)
    public void updateMap(int playerX, int playerY, int wumpX, int wumpY) {
        //Generate an empty base layout
        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                //Overwrite the moving object position marking, keep the rest as they were
                if(gameMap[i][j] == emptyMark || gameMap[i][j] == playerMark || gameMap[i][j] == wumpusMark) {
                    gameMap[i][j] = emptyMark;
                }

            }
        }

        //Place Player
        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                gameMap[playerY][playerX] = playerMark;
            }
        }

        //Place Wumpus
        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                gameMap[wumpY][wumpX] = wumpusMark;
            }
        }

    }

    //Parse all hazards nearby within limits (one step from Player?)
    public HashSet<String> parsePlayerVicinity(int playerY, int playerX) {

        System.out.println("You are at " + playerY + ":" + playerX);

        //Set for hazards ()
        HashSet<String> hazardSet = new HashSet<String>();

        //Parse the map based from player's x and y
        for(int y = -1; y < 2; y++) {
            //System.out.println("Inside Y parse");
            for(int x = -1; x < 2; x++) {
                //System.out.println("Inside X parse");
                //No need to check player's spot 0:0 at all
                if(!(x == 0 && y == 0)) {
                    int vicY = playerY + y;
                    int vicX = playerX + x;
                    //System.out.println("Parse at: " + vicY +":" + vicX);
                    //Check if the spot x,y are within the max/min values
                    if((vicY >= minCoord && vicY <= maxCoord) && (vicX >= minCoord && vicX <= maxCoord)) {
                        //System.out.println("Inside parse");
                        if(gameMap[vicY][vicX] == pitMark || gameMap[vicY][vicX] == wumpusMark || gameMap[vicY][vicX] == batMark) {
                            //System.out.println(gameMap[vicY][vicX]);
                            hazardSet.add(gameMap[vicY][vicX]);
                        }
                    }

                }

            }
        }

        //Print hazard texts if found from the hashset
        for(String haz : hazardSet) {
            switch(haz) {
                case "[W]":
                    System.out.println("You smell Wumpus!");
                    break;
                case "[U]":
                    System.out.println("You feel a strong breeze!");
                    break;
                case "[B]":
                    System.out.println("You hear wings flapping!");
            }
        }
    return hazardSet;
    }

    //Throw player to new spot, check collision type
    public int checkBatThrow(Player player) {

        //Generate new coords first and check their uniqueness
        int newY = 0;
        int newX = 0;

        do {
            newY = generateCoord();
            newX = generateCoord();
            System.out.println("New y,x " + newY + ":" + newX);
        } while(newY == player.getPlayerYCoordinate() && newX == player.getPlayerXCoordinate());

        //Send player to new random location
        player.setPlayerStartPosition(newY, newX);

        //Quick info check
        System.out.println("Player thrown at: " + player.getPlayerYCoordinate() + ":" + player.getPlayerXCoordinate());

        int batCol = checkCollisionEvent(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());

        return batCol;
    }

    //Check after Player has moved if there is anything else on the spot
    public int checkCollisionEvent(int playerY, int playerX) {
        //Parse game map and check any markings
        String collisionSpot = "[ ]";

        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                //Check the player's new position
                if(i == playerY && j == playerX) {
                    collisionSpot = gameMap[i][j];
                }
            }
        }

        //Check this first
        //System.out.println("Collision spot: " + collisionSpot);

        switch(collisionSpot) {
            case "[W]":
                colAnswer = 1;
                break;
            case "[U]":
                colAnswer = 2;
                break;
            case "[B]":
                colAnswer = 3;
                break;
            case "[A]":
                colAnswer = 4;
                break;
            default:
                colAnswer = 0;
        }

        return colAnswer;
    }

    //Tell Player's coordinates and any info about nearby hazards, print movement options
    public void showInfo(int pY, int pX) {
        System.out.println("You are at " + pY +"," + pX);


        //Extra!!! Parse hazard spots and tell them
        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
                //Check any hazard spot
                /*
                if(!(gameMap[i][j] == playerMark || gameMap[i][j] == emptyMark)) {
                    System.out.println("Hazard at: " + i + ":" + j);
                }
                */
                //Tell Wumpus spot
                if(gameMap[i][j] == wumpusMark) {
                    System.out.println("Wumpus at: " + i + ":" + j);
                }

            }
        }

    }

    public void showInstructions() {
        System.out.println("Instructions");
    }

    //Display ASCII art when Player falls into pit
    public void printPlayerFall() {
        System.out.println("You fell to your death!");
    }

    //Display ASCII art when Player is eaten by Wumpus
    public void printPlayerEaten() {
        System.out.println("You were eaten by the Wumpus!");
    }

    public void printPlayerWin() {
        System.out.println("You got the Wumpus!");
    }

}

