package fi.tuni.pepper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.aldebaran.qi.sdk.object.locale.Region;


public class GameActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private Button menu;
    private Chat chat;
    private LinearLayout row0;
    private LinearLayout row1;
    private LinearLayout row2;
    private LinearLayout row3;
    private LinearLayout row4;
    private LinearLayout[] gameBoard;
    private int playerX = 2;
    private int playerY = 2;
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
        btn_left.setOnClickListener((view) -> {
            if(playerX != 0) {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerX--;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            }
        });
        View btn_up = findViewById(R.id.btn_up);
        btn_up.setOnClickListener((view) -> {
            if(playerY != 0) {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerY--;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            }
        });
        View btn_down = findViewById(R.id.btn_down);
        btn_down.setOnClickListener((view) -> {
            if(playerY != 4) {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerY++;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            }
        });
        View btn_right = findViewById(R.id.btn_right);
        btn_right.setOnClickListener((view) -> {
            if(playerX != 4) {
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player_visited);
                playerX++;
                gameBoard[playerY].getChildAt(playerX).setBackgroundResource(R.drawable.game_grid_player);
            }
        });
        row0 = findViewById(R.id.row0);
        row1 = findViewById(R.id.row1);
        row2 = findViewById(R.id.row2);
        row3 = findViewById(R.id.row3);
        row4 = findViewById(R.id.row4);
        gameBoard = new LinearLayout[]{row0, row1, row2, row3, row4};
        Log.i("board", gameBoard[0].getChildAt(0).toString());




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
        super.onDestroy();
    }
}