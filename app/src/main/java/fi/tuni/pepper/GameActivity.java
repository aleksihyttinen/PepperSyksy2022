package fi.tuni.pepper;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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


public class GameActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private Chat chat;
    private LinearLayout[] gameBoard;
    private int playerX = (int)(Math.random() * 4);
    private int playerY = (int)(Math.random() * 4);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        QiSDK.register(this, this);
        Log.i("create", "created");
        Button menu = findViewById(R.id.menu);
        menu.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);
        });
        View btn_left = findViewById(R.id.btn_left);
        btn_left.setOnClickListener((view) -> moveLeft());
        View btn_up = findViewById(R.id.btn_up);
        btn_up.setOnClickListener((view) -> moveUp());
        View btn_down = findViewById(R.id.btn_down);
        btn_down.setOnClickListener((view) -> moveDown());
        View btn_right = findViewById(R.id.btn_right);
        btn_right.setOnClickListener((view) -> moveRight());
        LinearLayout row0 = findViewById(R.id.row0);
        LinearLayout row1 = findViewById(R.id.row1);
        LinearLayout row2 = findViewById(R.id.row2);
        LinearLayout row3 = findViewById(R.id.row3);
        LinearLayout row4 = findViewById(R.id.row4);
        gameBoard = new LinearLayout[]{row0, row1, row2, row3, row4};
        gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
    }



    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i("focus", "focus gained");
        Say say = SayBuilder.with(qiContext)
                .withText("Tervetuloa pelaamaan Wumpus-peliä")
                .build();
        say.run();
        Topic topic = TopicBuilder.with(qiContext).withResource(R.raw.moves).build();
        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();

        Locale locale = new Locale(Language.FINNISH, Region.FINLAND);
        chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).withLocale(locale).build();
        chat.addOnStartedListener(() -> Log.i("testi", "chatti aloitettu"));
        chat.setListeningBodyLanguage(BodyLanguageOption.DISABLED);
        Future<Void> chatFuture = chat.async().run();
        chat.addOnHeardListener(heardPhrase -> {
            switch(heardPhrase.toString()) {
                case("ylös"): {
                    moveUp();
                    break;
                }
                case("alas"): {
                    moveDown();
                    break;
                }
                case("oikea"): {
                    moveRight();
                    break;
                }
                case("vasen"): {
                    moveLeft();
                    break;
                }
            }
        });
        qiChatbot.addOnEndedListener(endPhrase ->{
                    Log.i("testi", "qichatbot end reason = " + endPhrase);
                    chatFuture.requestCancellation();
                }
        );
        chatFuture.thenConsume(future -> {
            if(future.hasError()){
                Log.e("Error", "Discussion finished with error.", future.getError());
            }
        });
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

    private void moveUp() {
        if(playerY != 0) {
            runOnUiThread(()-> {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerY--;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            });
        }
    }
    private void moveDown() {
        if(playerY != 4) {
            runOnUiThread(()-> {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerY++;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            });
        }
    }
    private void moveLeft() {
        if(playerX != 0) {
            runOnUiThread(()-> {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerX--;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            });
        }
    }
    private void moveRight() {
        if(playerX != 4) {
            runOnUiThread(()-> {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerX++;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            });
        }
    }
}