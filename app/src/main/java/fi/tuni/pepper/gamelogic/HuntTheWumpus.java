package fi.tuni.pepper.gamelogic;

import java.util.Scanner;

public class HuntTheWumpus {

    public int collisionType = 0;
    public static boolean gameOn = true;

    public static void main(String[] args) {
        //Scanner menusc = new Scanner(System.in);

        HuntTheWumpus htw = new HuntTheWumpus();

        System.out.println("Welcome to Hunt The Wumpus!");

        GameManager gm = new GameManager();
        Player player = new Player(gm.generateCoord(), gm.generateCoord());

        htw.playGame(gm, player);

    }

    public void playGame(GameManager gm, Player player) {
        Scanner sc = new Scanner(System.in);
        Wumpus wumpus = new Wumpus(gm.generateCoord(), gm.generateCoord());

        System.out.println("You start at: " + player.getPlayerYCoordinate() + ":" + player.getPlayerXCoordinate());
        gm.gameMap = gm.generateMap(player.getPlayerYCoordinate(), player.getPlayerXCoordinate(), wumpus);

        gameOn = true;
        char dir = 'N';
        char arrowDir = 'N';
        boolean arrowHit = false;

        do {
            //First parse all info about the vicinity of the player spot
            //gm.parsePlayerVicinity(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());

            //Show current map
            gm.displayMap(gm.gameMap);

            //Take player input for movement/shooting
            do {

                try {
                    System.out.println("Please give direction or shoot:");
                    System.out.println("w=up, s=down, a=left, d=right, f=shoot");
                    dir = sc.next().charAt(0);


                } catch (Exception e) {

                    System.out.println("Please give only single character![wasd]");

                    e.printStackTrace();
                    dir = 'N';
                }

                System.out.println("Given input: " + dir);

            } while (dir == 'N');

            //Check player shooting commands
            if (dir == 'f') {
                //Take shooting direction and check if hit IF you have ammo left
                if (player.getPlayerArrows() > 0) {

                    boolean isAiming = true;
                    //Player shooting loop
                    do {

                        try {
                            System.out.println("Give shooting direction:");
                            System.out.println("w=up, s=down, a=left, d=right, g = cancel");

                            //Take arrow direction
                            arrowDir = sc.next().charAt(0);

                        } catch (Exception e) {
                            System.out.println("Invalid input!");
                            isAiming = false;
                        }


                        isAiming = false;

                    } while (isAiming);

                    //Set shooting direction with int 1-4
                    int arrowStep = 0;

                    //Possible aim directions with aim limit checks
                    if (arrowDir == 'w') {
                        if (player.getPlayerYCoordinate() > 0) {
                            arrowStep = 1;
                        }
                    } else if (arrowDir == 's') {
                        if (player.getPlayerYCoordinate() < 4) {
                            arrowStep = 2;
                        }

                    } else if (arrowDir == 'a') {
                        if (player.getPlayerXCoordinate() > 0) {
                            arrowStep = 3;
                        }

                    } else if (arrowDir == 'd') {
                        if (player.getPlayerXCoordinate() < 4) {
                            arrowStep = 4;
                        }

                    } else {
                        System.out.println("Shooting cancelled!");
                        arrowDir = 'g';
                    }

                    if (!(arrowDir == 'g')) {
                        //Remove one arrow from inventory
                        player.setPlayerArrows(-1);
                        arrowHit = gm.checkArrowHit(arrowStep, wumpus, player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
                    }

                } else {
                    System.out.println("Sorry, you have no arrows left");
                }

            } else {
                //Don't shoot, move player instead
                player.movePlayer(dir);
            }


            //System.out.println("Player at: " + player.getPlayerYCoordinate() +":" + player.getPlayerXCoordinate());
            gm.showInfo(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());

            /* OLD STUFF
            //Check any collisions
            collisionType = gm.checkCollisionEvent(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
            //System.out.println("Collision type: " + collisionType);

            //Sets current updates to the map only after checking the collisions
            if(collisionType == 0) {
                gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
            } else if(collisionType == 3) {
                //Relocate player to a random spot, check collision again
                System.out.println("Bats, do this later");
                gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
            } else if(collisionType == 1 || collisionType == 2) {
                gameOn = false;

                //Show death ASCII (text for now)
                if(collisionType == 1) {
                    gm.printPlayerEaten();
                } else {
                    gm.printPlayerFall();
                }

            }

            */

            //NEW CHECK, CAN DO BATS TOO
            //Check any collisions after moving
            collisionType = gm.checkCollisionEvent(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
            //System.out.println("Collision type: " + collisionType);

            //IF we hit a bat spot
            if (collisionType == 3) {
                //Relocate player to a random spot, check collision again
                System.out.println("Bats pick you up!");

                //Relocate and check collisions type for the bat move
                int batCol = gm.checkBatThrow(player);//goes to checkCollisionEvent()

                //THIS SHOULD NOT GO TO BAT SPOT AGAIN!
                respondToCollision(batCol, gm, player, wumpus);
                //Only then update the map changes (might be safe spot or not)
                //gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());

            } else {
                respondToCollision(collisionType, gm, player, wumpus);
            }

            //YOU WIN
            if (arrowHit) {
                gameOn = false;
                gm.printPlayerWin();
            }

        } while (gameOn);


        sc.close();

        System.out.println("Thank you for playing!");
    }

    //Make a response based on collisions after player movement
    public static void respondToCollision(int collisionType, GameManager gm, Player player, Wumpus wumpus) {

        //Sets current updates to the map only after checking the collisions
        if (collisionType == 0) {
            //No collision, continue normally
            gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());

        } else if (collisionType == 3) {
            //Relocate player to a random spot, check collision again
            System.out.println("Bats pick you up!");

            //Relocate and check collisions type for the bat move
            int batCol = gm.checkBatThrow(player);//goes to checkCollisionEvent()

            //Only then update the map changes (might be safe spot or not)
            gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());

        } else if (collisionType == 4) {
            //Give player an arrow, remove arrow from spot
            System.out.println("You found an arrow!");
            player.setPlayerArrows(1);
            gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());

        } else if (collisionType == 1 || collisionType == 2) {
            gameOn = false;

            //Show death ASCII (text for now)
            if (collisionType == 1) {
                gm.printPlayerEaten();
            } else {
                gm.printPlayerFall();
            }
        }


        //Simple menu loop
        //NOT Currently used
        /*
        boolean runMenu = true;
        char menuAnswer = '0';
        do {
            System.out.println("What would you like to do?");
            System.out.println("[Play game (1)] [Instructions (2)] [Quit (3)]");

            try {
                menuAnswer = menusc.next().charAt(0);
            } catch(Exception e) {
                e.printStackTrace();
                menuAnswer = '0';
                System.out.println("Please only use numbers!");
            }

            if(menuAnswer == '1' || menuAnswer == '3') {
                runMenu = false;
            } else if (menuAnswer == '2') {
                gm.showInstructions();
            }

        } while (runMenu);

        if(menuAnswer == '1') {
            htw.playGame(gm, player);
        } else {
            System.out.println("Good bye");
        }
        */
    }
}

