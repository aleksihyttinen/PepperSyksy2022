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
        gameMap[playerY][playerX] = playerMark;

        //Place Wumpus at free spot
        boolean wumpusPlaced = false;
        while(!wumpusPlaced){
            System.out.println("Wumpus at: " + wumpus.getWumpusYCoordinate() + ":" + wumpus.getWumpusXCoordinate());

            if(gameMap[wumpus.getWumpusYCoordinate()][wumpus.getWumpusXCoordinate()] == emptyMark) {
                gameMap[wumpus.getWumpusYCoordinate()][wumpus.getWumpusXCoordinate()] = wumpusMark;
                wumpusPlaced = true;
            }

            //IF not placed, generate new coordinates for wumpus
            if(!wumpusPlaced){
                wumpus.setWumpusStartPosition(generateCoord(), generateCoord());
            }
        }


        //Generate hazards for all the rest free spots
        //Place pits for the map
        int k = 0;
        int pitY = 0;
        int pitX = 0;

        while(k < pitAmount) {
            pitY = generateCoord();
            pitX = generateCoord();
            //Only set restriction for ONE pit placement
            if(gameMap[pitY][pitX] == emptyMark && (checkHazardLimits(pitY, pitX) && k == 0)) {
                gameMap[pitY][pitX] = pitMark;
                k++;
            } else if (gameMap[pitY][pitX] == emptyMark && k < pitAmount) {
                gameMap[pitY][pitX] = pitMark;
                k++;
            }
        }

        //Place bat spot to a free location
        boolean batsPlaced = false;
        int batY = 0;
        int batX = 0;

        while(!batsPlaced) {

            batY = generateCoord();
            batX = generateCoord();

            if(gameMap[batY][batX] == emptyMark && checkHazardLimits(batY, batX)) {
                gameMap[batY][batX] = batMark;
                batsPlaced = true;
            }

        }

        //Place arrow
        int arrowY = 0;
        int arrowX = 0;
        boolean arrowPlaced = false;

        while(!arrowPlaced) {

            arrowY = generateCoord();
            arrowX = generateCoord();

            if(gameMap[arrowY][arrowX] == emptyMark) {
                gameMap[arrowY][arrowX] = arrowMark;
                arrowPlaced = true;
            }
        }

        //return the generated map
        return gameMap;
    }

    //Check if the hazard is too close to Wumpus or Player, return false if cannot place hazard
    public boolean checkHazardLimits(int hazardY, int hazardX) {
        for(int y = -1; y < 2; y++) {
            for(int x = -1; x < 2; x++) {
                if(!(x == 0 && y == 0)) {
                    int vicinityY = hazardY + y;
                    int vicinityX = hazardX + x;
                    //Check if the spot x,y are within the max/min values
                    if((vicinityY >= minCoord && vicinityY <= maxCoord) && (vicinityX >= minCoord && vicinityX <= maxCoord)) {
                        if(gameMap[vicinityY][vicinityX] == playerMark || gameMap[vicinityY][vicinityX] == wumpusMark) {
                            return false;
                        }
                    }

                }
            }
        }

        return true;
    }

    //Generates int between 0-4 as coordinate
    public int generateCoord() {
        return (int) ((Math.random() * (maxCoord - minCoord + 1)) + minCoord);
    }

    //Compare aimed spot from player coords to actual Wumpus spot
    public boolean checkArrowHit(int arrowDir, Wumpus wumpus, int playerY, int playerX) {

        //Check wumpus position
        System.out.println("Wumpus at: " + wumpus.getWumpusYCoordinate()+  ":" + wumpus.getWumpusXCoordinate());

        //Use new values for arrow movement, use player coords only for update
        int arrowY = playerY;
        int arrowX = playerX;

        ///Decide target adjustments via arrowDir value
        switch(arrowDir){
            case 1:
                arrowY--;
                break;
            case 2:
                arrowY++;
                break;
            case 3:
                arrowX--;
                break;
            case 4:
                arrowX++;
                break;
        }

        System.out.println("Arrow flying into: " + arrowY + ":" + arrowX);

        //Check if game map spot has wumpus in it
        boolean shotHit = gameMap[arrowY][arrowX] == wumpusMark;

        //Move wumpus if arrow did not hit
        if(!shotHit) {
            wumpus.moveWumpus(gameMap);
            //Update map immediatelly after wumpus moves
            updateMap(playerX, playerY, wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
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
        gameMap[playerY][playerX] = playerMark;

        //Place Wumpus
        gameMap[wumpY][wumpX] = wumpusMark;

    }

    //Parse all hazards nearby within limits (one step from Player?)
    public HashSet<String> parsePlayerVicinity(int playerY, int playerX) {

        System.out.println("You are at " + playerY + ":" + playerX);

        //Set for hazards ()
        HashSet<String> hazardSet = new HashSet<String>();

        //Parse the map based from player's x and y
        for(int y = -1; y < 2; y++) {
            for(int x = -1; x < 2; x++) {
                //No need to check player's spot 0:0 at all
                if(!(x == 0 && y == 0)) {
                    int vicY = playerY + y;
                    int vicX = playerX + x;
                    //Check if the spot x,y are within the max/min values
                    if((vicY >= minCoord && vicY <= maxCoord) && (vicX >= minCoord && vicX <= maxCoord)) {
                        if(gameMap[vicY][vicX] == pitMark || gameMap[vicY][vicX] == wumpusMark || gameMap[vicY][vicX] == batMark) {
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

        String collisionSpot = gameMap[playerY][playerX];

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

        for(int i=0; i <= maxCoord; i++ ) {
            for(int j=0; j <= maxCoord; j++) {
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

