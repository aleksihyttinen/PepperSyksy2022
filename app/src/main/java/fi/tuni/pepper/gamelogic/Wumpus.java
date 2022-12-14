package fi.tuni.pepper.gamelogic;

public class Wumpus {

    int wumpusX = 0;
    int wumpusY = 0;

    public Wumpus(int x, int y) {
        wumpusX = x;
        wumpusY = y;
    }

    public void setWumpusXCoordinate(int input) {
        //Allow movement to the left
        if((wumpusX > 0) && (input == -1)) {
            wumpusX = wumpusX + input;
            //Allow movement to the right
        } else if((wumpusX < 4) && (input == 1)) {
            wumpusX = wumpusX + input;
        }
    }

    public int getWumpusXCoordinate() {
        return wumpusX;
    }

    public void setWumpusYCoordinate(int input) {
        //Allow movement up
        if((wumpusY > 0) && (input == -1)) {
            wumpusY = wumpusY + input;
            //Allow movement down
        } else if((wumpusY < 4) && (input == 1)) {
            wumpusY = wumpusY + input;
        }
    }

    public int getWumpusYCoordinate() {
        return wumpusY;
    }

    public void setWumpusStartPosition(int wY, int wX) {
        wumpusY = wY;
        wumpusX = wX;
    }

    //Moves wumpus one spot/line up/down or right/left
    public void moveWumpus(String[][] gameMap) {
        int wumpusMovement = 0;

        //Decide movement direction -1/1
        int wumpusStep = (int) ((Math.random() * 2));

        if(wumpusStep < 1) {
            wumpusMovement = 1;
        } else {
            wumpusMovement = -1;
        }

        //Decide movement horizontal or vertical
        //0 or 1, switch case
        int moveDir = (int) ((Math.random() * 2));
        int checkSpotY = getWumpusYCoordinate();
        int checkSpotX = getWumpusXCoordinate();

        //Choose X or Y change before validation check
        if(moveDir < 1) {
            checkSpotX = checkSpotX + wumpusMovement;
        } else {
            checkSpotY = checkSpotY + wumpusMovement;
        }

        //Parse through game map and check if the direction has no hazards
        boolean letWumpusMove = false;

        //Check if new spot values are within map limits
        if((checkSpotY < gameMap.length && checkSpotY >= 0) && (checkSpotX < gameMap[0].length && checkSpotX >= 0)) {
            //Check if spot has no pit or bat
            if(gameMap[checkSpotY][checkSpotX] != "[U]" && gameMap[checkSpotY][checkSpotX] != "[B]") {
                //System.out.println("Spot can be reached: " + checkSpotY + ":" + checkSpotX);
                letWumpusMove = true;
            }
        }

        if(letWumpusMove) {
            //Put this after validation check
            switch(moveDir) {
                case 0:
                    setWumpusXCoordinate(wumpusMovement);
                    break;
                case 1:
                    setWumpusYCoordinate(wumpusMovement);
                    break;
            }
        }

    }

}

