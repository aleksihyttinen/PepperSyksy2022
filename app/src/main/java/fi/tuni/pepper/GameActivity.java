package fi.tuni.pepper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.aldebaran.qi.sdk.object.locale.Region;

import fi.tuni.pepper.gamelogic.GameManager;
import fi.tuni.pepper.gamelogic.HuntTheWumpus;
import fi.tuni.pepper.gamelogic.Player;
import fi.tuni.pepper.gamelogic.Wumpus;


public class GameActivity extends RobotActivity implements RobotLifecycleCallbacks {
    public static View menu;
    public static boolean gameOn = true;
    private Chat chat;
    private LinearLayout[] gameBoard;
    private Button voice_btn;
    private Boolean shootMode = false;
    HuntTheWumpus htw = new HuntTheWumpus();
    GameManager gm = new GameManager();
    Player player = new Player(gm.generateCoord(), gm.generateCoord());
    Wumpus wumpus = new Wumpus(gm.generateCoord(), gm.generateCoord());
    private View btn_shoot;
    private int collisionType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        QiSDK.register(this, this);
        Log.i("create", "created");
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);
        });
        View btn_left = findViewById(R.id.btn_left);
        btn_left.setOnClickListener((view) -> moveOrShoot("vasen"));
        View btn_up = findViewById(R.id.btn_up);
        btn_up.setOnClickListener((view) -> moveOrShoot("ylös"));
        View btn_down = findViewById(R.id.btn_down);
        btn_down.setOnClickListener((view) -> moveOrShoot("alas"));
        View btn_right = findViewById(R.id.btn_right);
        btn_right.setOnClickListener((view) -> moveOrShoot("oikea"));
        btn_shoot = findViewById(R.id.btn_shoot);
        btn_shoot.setOnClickListener((view) -> {
            shootMode = !shootMode;
            if (shootMode) {
                btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            } else {
                btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            }
        });
        LinearLayout row0 = findViewById(R.id.row0);
        LinearLayout row1 = findViewById(R.id.row1);
        LinearLayout row2 = findViewById(R.id.row2);
        LinearLayout row3 = findViewById(R.id.row3);
        LinearLayout row4 = findViewById(R.id.row4);
        voice_btn = findViewById(R.id.voice);
        gameBoard = new LinearLayout[]{row0, row1, row2, row3, row4};
        gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
        gm.gameMap = gm.generateMap(player.getPlayerYCoordinate(), player.getPlayerXCoordinate(), wumpus);
    }



    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i("focus", "focus gained");
        Say say = SayBuilder.with(qiContext)
                .withText("Tervetuloa pelaamaan Wumpus-peliä")
                .withBodyLanguageOption(BodyLanguageOption.DISABLED)
                .build();
        say.run();
        voice_btn.setOnClickListener((view) -> startVoiceControl(qiContext));

    }

    @Override
    public void onRobotFocusLost() {
        Log.i("focus", "focus lost");
        if(chat!=null){
            chat.removeAllOnStartedListeners();
            chat.removeAllOnHeardListeners();
        }

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
    }

    private void moveOrShoot(String direction) {
        Log.i("mode", shootMode.toString());
        switch(direction) {
            case("ylös"): {
                if(!shootMode) {
                    if(player.getPlayerYCoordinate() != 0) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            moveAndCheckCollision('w');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                        });
                    }
                } else {
                    shootMode = false;
                    btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                }
                break;
            }
            case("alas"): {
                if(!shootMode) {
                    if(player.getPlayerYCoordinate() != 4) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            moveAndCheckCollision('s');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                        });
                    }
                } else {
                    shootMode = false;
                    btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                }
                break;
            }
            case("vasen"): {
                if(!shootMode) {
                    if(player.getPlayerXCoordinate() != 0) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            moveAndCheckCollision('a');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                        });
                    }
                } else {
                    shootMode = false;
                    btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                }
                break;
            }
            case("oikea"): {
                if(!shootMode) {
                    if(player.getPlayerXCoordinate() != 4) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            moveAndCheckCollision('d');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                        });
                    }
                } else {
                    shootMode = false;
                    btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                }
                break;
            }
            case("ammu"): {
                shootMode = true;
                btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            }
            case("liiku"): {
                shootMode = false;
                btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            }
        }
    }

    private void startVoiceControl(QiContext qiContext) {
        runOnUiThread(()-> voice_btn.setText("Lopeta ääniohjaus"));

        new Thread(() -> {
            Log.i("testi", "jee");
            Topic topic = TopicBuilder.with(qiContext).withResource(R.raw.moves).build();
            QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();

            Locale locale = new Locale(Language.FINNISH, Region.FINLAND);
            chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).withLocale(locale).build();
            chat.addOnStartedListener(() -> Log.i("testi", "chatti aloitettu"));
            chat.setListeningBodyLanguage(BodyLanguageOption.DISABLED);
            qiChatbot.setSpeakingBodyLanguage(BodyLanguageOption.DISABLED);
            Future<Void> chatFuture = chat.async().run();
            voice_btn.setOnClickListener((view) -> {
                chatFuture.requestCancellation();
                runOnUiThread(() -> voice_btn.setText("Ääniohjaus"));
                voice_btn.setOnClickListener((view1) -> startVoiceControl(qiContext));
            });
            chat.addOnHeardListener(heardPhrase -> {
                Log.i("käsky", heardPhrase.toString());
                moveOrShoot(heardPhrase.getText());
            });
            qiChatbot.addOnEndedListener(endPhrase -> {
                        Log.i("testi", "qichatbot end reason = " + endPhrase);
                        chatFuture.requestCancellation();
                        runOnUiThread(() -> voice_btn.setText("Ääniohjaus"));
                        voice_btn.setOnClickListener((view) -> startVoiceControl(qiContext));
                    }
            );
            chatFuture.thenConsume(future -> {
                if (future.hasError()) {
                    Log.e("Error", "Discussion finished with error.", future.getError());
                }
            });
        }).start();
    }
    public void moveAndCheckCollision(char dir) {
        if(gameOn) {
            gm.displayMap(gm.gameMap);
            player.movePlayer(dir);
            collisionType = gm.checkCollisionEvent(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
            //System.out.println("Collision type: " + collisionType);

            //Sets current updates to the map only after checking the collisions
            if (collisionType == 0) {
                gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
            } else if (collisionType == 3) {
                //Relocate player to a random spot, check collision again
                System.out.println("Bats, do this later");
                gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
            } else if (collisionType == 1 || collisionType == 2) {
                System.out.println("hävisit");
                new AlertDialog.Builder(this)
                        .setMessage("Hävisit pelin")
                        .setNegativeButton("Palaa päävalikkoon", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                menu.performClick();
                            }
                        })
                        .setPositiveButton("Pelaa uudelleen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                gameOn = true;
                                player.setPlayerYCoordinate(gm.generateCoord());
                                player.setPlayerXCoordinate(gm.generateCoord());
                                wumpus.setWumpusStartPosition(gm.generateCoord(), gm.generateCoord());
                                gm.gameMap = gm.generateMap(player.getPlayerYCoordinate(), player.getPlayerXCoordinate(), wumpus);
                                for (LinearLayout row : gameBoard) {
                                    for (int j = 0; j < row.getChildCount(); j++) {
                                        row.getChildAt(j).setBackgroundResource(R.drawable.game_grid_border);
                                    }
                                }
                                gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);

                            }
                        })
                        .create().show();
                //Show death ASCII (text for now)
                if (collisionType == 1) {
                    gm.printPlayerEaten();
                } else {
                    gm.printPlayerFall();
                }

            }
        }
    }
}