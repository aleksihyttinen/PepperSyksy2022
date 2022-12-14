package fi.tuni.pepper.gamelogic;



public class Player{

    int playerX = 0;
    int playerY = 0;

    int arrowAmount = 3;

    public Player(int x, int y) {
        playerX = x;
        playerY = y;
    }
    public void setPlayerStartPosition(int x, int y) {
        playerX = x;
        playerY = y;
    }
    public final char playerUp = 'w';
    public final char playerDown = 's';
    public final char playerLeft = 'a';
    public final char playerRight = 'd';

    //Set player's coordinate by one step to certain direction
    public void movePlayer(char dir) {
        //Take direction, make limit assesment
        switch(dir) {
            case playerUp:
                setPlayerYCoordinate(-1);
                break;
            case playerDown:
                setPlayerYCoordinate(1);
                break;
            case playerLeft:
                setPlayerXCoordinate(-1);
                break;
            case playerRight:
                setPlayerXCoordinate(1);
                break;
        }
    }

    public void setPlayerXCoordinate(int input) {

        //System.out.println("X is: " + input);

        //Allow movement to the left
        if((playerX > 0) && (input == -1)) {
            playerX = playerX + input;
            //Allow movement to the right
        } else if((playerX < 4) && (input == 1)) {
            playerX = playerX + input;
        }

    }

    public int getPlayerXCoordinate() {
        return playerX;
    }

    public void setPlayerYCoordinate(int input) {
        //Allow movement up
        if((playerY > 0) && (input == -1)) {
            playerY = playerY + input;
            //Allow movement down
        } else if((playerY < 4) && (input == 1)) {
            playerY = playerY + input;
        }
    }

    public int getPlayerYCoordinate() {
        return playerY;
    }

    //Can be +/- new arrows
    public void setPlayerArrows(int pArrow) {

        if(arrowAmount > 0 || (arrowAmount == 0 && pArrow > 0)) {
            arrowAmount = arrowAmount + pArrow;
        }

    }
    public void setPlayerStartArrows(int arrows) {
        arrowAmount = arrows;
    }

    public int getPlayerArrows() {
        return arrowAmount;
    }



}

