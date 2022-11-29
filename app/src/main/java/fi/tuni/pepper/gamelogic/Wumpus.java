package fi.tuni.pepper.gamelogic;

public class Wumpus {

    int wumpusX = 0;
    int wumpusY = 0;

    public Wumpus(int x, int y) {
        wumpusX = x;
        wumpusY = y;
    }

    public void setWumpusXCoordinate(int input) {
        //wumpusX = wumpusX + input;

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
        //wumpusY = wumpusY + input;

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
    public void moveWumpus() {
        int wumpusMovement = 0;

        //Decide movement direction -1/1
        int wumpusStep = (int) ((Math.random() * 2));

        if(wumpusStep > 0.5) {
            wumpusMovement = 1;
        } else {
            wumpusMovement = -1;
        }
        /*
        switch(wumpusStep) {
            case 0:
                wumpusMovement = -1;
            case 1:
                wumpusMovement = 1;
        }
        */

        //Decide movement horizontal or vertical
        //0 or 1, switch case
        int moveDir = (int) ((Math.random() * 2));

        System.out.println("Wumpus moves dir: " + wumpusMovement);
        System.out.println("Wumpus moves: " + moveDir);

        switch(moveDir) {
            case 0:
                System.out.println("Wumpus case 0");
                setWumpusXCoordinate(wumpusMovement);
                break;
            case 1:
                System.out.println("Wumpus case 1");
                setWumpusYCoordinate(wumpusMovement);
                break;
        }

    }

}

