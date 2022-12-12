package fi.tuni.pepper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.HashSet;

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
    private TextView player_location;
    private TextView wumpus_message;
    private TextView bats_message;
    private TextView pits_message;
    private TextView arrow_message;
    GameManager gm = new GameManager();
    Player player = new Player(gm.generateCoord(), gm.generateCoord());
    Wumpus wumpus = new Wumpus(gm.generateCoord(), gm.generateCoord());
    private View btn_shoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        QiSDK.register(this, this);
        Log.i("create", "created");
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.putExtra("fromOtherActivity", true);
            view.getContext().startActivity(intent);
        });
        View guide = findViewById(R.id.guide);
        guide.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), GuideActivity.class);
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
        player_location = findViewById(R.id.player_location);
        wumpus_message = findViewById(R.id.wumpus_message);
        bats_message = findViewById(R.id.bats_message);
        pits_message = findViewById(R.id.pits_message);
        arrow_message = findViewById(R.id.arrow_message);
        player_location.setText("Olet ruudussa: " + (player.getPlayerXCoordinate()+1) +","+ (player.getPlayerYCoordinate()+1));
        gm.gameMap = gm.generateMap(player.getPlayerYCoordinate(), player.getPlayerXCoordinate(), wumpus);
        HashSet<String> dangers = gm.parsePlayerVicinity(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
        updateDangerMessages(dangers);
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
        System.out.println( shootMode.toString());
        System.out.println(direction);
        switch(direction) {
            case("ylös"): {
                if(!shootMode) {
                    if(player.getPlayerYCoordinate() != 0) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            movePlayer('w');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                            player_location.setText("Olet ruudussa: " + (player.getPlayerXCoordinate()+1) +","+ (player.getPlayerYCoordinate()+1));
                        });
                    }
                } else {
                    if (player.getPlayerArrows() != 0) {
                        shootMode = false;
                        runOnUiThread(()-> {
                            btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                        });
                        player.setPlayerArrows(-1);
                        if(player.getPlayerYCoordinate() != 0) {
                            if(gm.checkArrowHit(1, wumpus, player.getPlayerYCoordinate(), player.getPlayerXCoordinate())) {
                                endGame(5);
                            } else {
                                runOnUiThread(()-> {
                                    arrow_message.setText("Ammuit ohi!");
                                });
                            }
                        } else {
                            runOnUiThread(()-> {
                                arrow_message.setText("Ammuit ulos kentästä");
                            });
                        }
                    } else {
                        runOnUiThread(()-> {
                            arrow_message.setText("Ei nuolia jäljellä");
                        });
                    }
                }
                break;
            }
            case("alas"): {
                if(!shootMode) {
                    if(player.getPlayerYCoordinate() != 4) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            movePlayer('s');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                            player_location.setText("Olet ruudussa: " + (player.getPlayerXCoordinate()+1) +","+ (player.getPlayerYCoordinate()+1));
                        });
                    }
                } else {
                    if (player.getPlayerArrows() != 0) {
                        shootMode = false;
                        runOnUiThread(()-> {
                            btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                        });
                        player.setPlayerArrows(-1);
                        if(player.getPlayerYCoordinate() != 4) {
                            if(gm.checkArrowHit(2, wumpus, player.getPlayerYCoordinate(), player.getPlayerXCoordinate())) {
                                endGame(5);
                            } else {
                                runOnUiThread(()-> {
                                    arrow_message.setText("Ammuit ohi!");
                                });
                            }
                        } else {
                            runOnUiThread(()-> {
                                arrow_message.setText("Ammuit ulos kentästä");
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            arrow_message.setText("Ei nuolia jäljellä");
                        });
                    }
                }
                break;
            }
            case("vasen"): {
                if(!shootMode) {
                    if(player.getPlayerXCoordinate() != 0) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            movePlayer('a');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                            player_location.setText("Olet ruudussa: " + (player.getPlayerXCoordinate()+1) +","+ (player.getPlayerYCoordinate()+1));
                        });
                    }
                } else {
                    if (player.getPlayerArrows() != 0) {
                        shootMode = false;
                        runOnUiThread(() -> {
                            btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                        });
                        player.setPlayerArrows(-1);
                        if(player.getPlayerXCoordinate() != 0) {
                            if(gm.checkArrowHit(3, wumpus, player.getPlayerYCoordinate(), player.getPlayerXCoordinate())) {
                                endGame(5);
                            } else {
                                runOnUiThread(()-> {
                                    arrow_message.setText("Ammuit ohi!");
                                });
                            }
                        } else {
                            runOnUiThread(()-> {
                                arrow_message.setText("Ammuit ulos kentästä");
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            arrow_message.setText("Ei nuolia jäljellä");
                        });
                    }
                }
                break;
            }
            case("oikea"): {
                if(!shootMode) {
                    if(player.getPlayerXCoordinate() != 4) {
                        runOnUiThread(() -> {
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                            movePlayer('d');
                            gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                            player_location.setText("Olet ruudussa: " + (player.getPlayerXCoordinate()+1) +","+ (player.getPlayerYCoordinate()+1));
                        });
                    }
                } else {
                    System.out.println("sisällä");
                    if (player.getPlayerArrows() != 0) {
                        System.out.println("sisällä nuolet");
                        shootMode = false;
                        runOnUiThread(()-> {
                            btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                        });
                        player.setPlayerArrows(-1);
                        if(player.getPlayerXCoordinate() != 4) {
                            System.out.println("sisällä 2");
                            if(gm.checkArrowHit(4, wumpus, player.getPlayerYCoordinate(), player.getPlayerXCoordinate())) {
                                endGame(5);
                            } else {
                                runOnUiThread(()-> {
                                    arrow_message.setText("Ammuit ohi!");
                                });
                            }
                        } else {
                            runOnUiThread(()-> {
                                arrow_message.setText("Ammuit ulos kentästä");
                            });
                        }
                    } else {
                        runOnUiThread(()-> {
                            arrow_message.setText("Ei nuolia jäljellä");
                        });
                    }
                }
                break;
            }
            case("ammu"): {
                shootMode = true;
                runOnUiThread(() -> {
                    btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
                });
                break;
            }
            case("liiku"): {
                shootMode = false;
                runOnUiThread(()-> {
                    btn_shoot.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                });
                break;
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
    private void updateDangerMessages(HashSet<String> dangers) {
        arrow_message.setText("");
        wumpus_message.setText("");
        bats_message.setText("");
        pits_message.setText("");
        for(String haz : dangers) {
            switch(haz) {
                case "[W]":
                    wumpus_message.setText("Haistat Wumpuksen!");
                    break;
                case "[U]":
                    pits_message.setText("Tunnet kylmän viiman");
                    break;
                case "[B]":
                    bats_message.setText("Kuulet siipien lepatusta");
                    break;
            }
        }
    }
    private void endGame(int endReason) {
        gameOn = false;
        System.out.println("hävisit");
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setMessage(endReason == 5 ? "Voitit pelin!": endReason == 1 ? "Törmäsit Wumpukseen, hävisit pelin" : endReason == 2 ? "Tipuit kuoppaan, hävisit pelin" : "Hävisit pelin")
                    .setNegativeButton("Palaa päävalikkoon", (arg0, arg1) -> menu.performClick())
                    .setPositiveButton("Pelaa uudelleen", (arg0, arg1) -> {
                        gameOn = true;
                        player.setPlayerStartPosition(gm.generateCoord(), gm.generateCoord());
                        wumpus.setWumpusStartPosition(gm.generateCoord(), gm.generateCoord());
                        gm.gameMap = gm.generateMap(player.getPlayerYCoordinate(), player.getPlayerXCoordinate(), wumpus);
                        HashSet<String> dangers = gm.parsePlayerVicinity(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
                        updateDangerMessages(dangers);
                        for (LinearLayout row : gameBoard) {
                            for (int j = 0; j < row.getChildCount(); j++) {
                                row.getChildAt(j).setBackgroundResource(R.drawable.game_grid_border);
                            }
                        }
                        gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                        player_location.setText("Olet ruudussa: " + (player.getPlayerXCoordinate()+1) +","+ (player.getPlayerYCoordinate()+1));

                    })
                    .setCancelable(false)
                    .create()
                    .show();
        });
    }
    public void checkCollisionAndUpdate(int collisionType) {
        if (collisionType == 0) {
            gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
        } else if (collisionType == 3) {
            int batCol = gm.checkBatThrow(player);//goes to checkCollisionEvent()
            if(batCol != 1 && batCol != 2 ) {
                gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player_visited);
                gameBoard[player.getPlayerYCoordinate()].getChildAt(player.getPlayerXCoordinate()).setBackgroundResource(R.drawable.game_grid_player);
                gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
                HashSet<String> dangers = gm.parsePlayerVicinity(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
                updateDangerMessages(dangers);
                checkCollisionAndUpdate(batCol);
            } else {
                endGame(batCol);
            }
        } else if(collisionType == 4) {
            player.setPlayerArrows(1);
            arrow_message.setText("Löysit nuolen!");
            gm.updateMap(player.getPlayerXCoordinate(), player.getPlayerYCoordinate(), wumpus.getWumpusXCoordinate(), wumpus.getWumpusYCoordinate());
        } else if (collisionType == 1 || collisionType == 2) {
            endGame(collisionType);
        }
    }
    public void movePlayer(char dir) {
        if(gameOn) {
            gm.displayMap(gm.gameMap);
            player.movePlayer(dir);
            int collisionType = gm.checkCollisionEvent(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
            //System.out.println("Collision type: " + collisionType);
            HashSet<String> dangers = gm.parsePlayerVicinity(player.getPlayerYCoordinate(), player.getPlayerXCoordinate());
            updateDangerMessages(dangers);
            //Sets current updates to the map only after checking the collisions
            checkCollisionAndUpdate(collisionType);
        }
    }
}