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

    int playerMove = 1;
    int playerShoot = 2;
    int playerToMenu = 3;

    public boolean isAlive = true;

    //Set player's coordinate by one step to certain direction
    public void movePlayer(char dir) {
        //Take direction, make limit assesment
        switch(dir) {
            case playerUp:
                //System.out.println("Move up");
                setPlayerYCoordinate(-1);
                break;
            case playerDown:
                //System.out.println("Move down");
                setPlayerYCoordinate(1);
                break;
            case playerLeft:
                //System.out.println("Move left");
                setPlayerXCoordinate(-1);
                break;
            case playerRight:
                //System.out.println("Move right");
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
        //System.out.println("Y is: " + input);
        //System.out.println("Y is: " + input);

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

