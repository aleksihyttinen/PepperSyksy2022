package fi.tuni.pepper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.GoToBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.builder.TransformBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.FreeFrame;
import com.aldebaran.qi.sdk.object.actuation.GoTo;
import com.aldebaran.qi.sdk.object.actuation.Mapping;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.aldebaran.qi.sdk.object.locale.Region;

public class DiscussionActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private View background;
    private Chat chat;
    private TextView hint1;
    private TextView hint2;
    private TextView hint3;
    private TextView hint4;
    private TextView hint5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        Log.i("create", "created");
        QiSDK.register(this, this);
        background = findViewById(R.id.background);
        hint1 = findViewById(R.id.hint1);
        hint2 = findViewById(R.id.hint2);
        hint3 = findViewById(R.id.hint3);
        hint4 = findViewById(R.id.hint4);
        hint5 = findViewById(R.id.hint5);
        Button menu = findViewById(R.id.menu);
        menu.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.putExtra("fromOtherActivity", true);
            view.getContext().startActivity(intent);
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Say say = SayBuilder.with(qiContext)
                .withText("Nyt voit keskustella kanssani, voit kysyä kysymyksiä minusta tai Tampereesta")
                .build();
        say.run();
        Topic topic = TopicBuilder.with(qiContext).withResource(R.raw.discussion).build();
        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();
        Locale locale = new Locale(Language.FINNISH, Region.FINLAND);
        chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).withLocale(locale).build();
        chat.addOnStartedListener(() -> Log.i("testi", "chatti aloitettu"));
        chat.setListeningBodyLanguage(BodyLanguageOption.DISABLED);
        Future<Void> chatFuture = chat.async().run();
        chat.addOnHeardListener(heardPhrase -> {
            switch (heardPhrase.getText()) {
                case("Kerro minulle Tampereesta"): {
                    runOnUiThread(()-> {
                        hint1.setText("Mikä on Tampereen pinta ala?");
                        hint2.setText("Mikä on Tampereen asukasluku?");
                        hint3.setText("Mitä Tampereella voi tehdä?");
                        hint4.setText("Onko Tampereella paikallisruokia?");
                        hint5.setText("Onko Tampereella lentokenttä?");
                    });
                    break;
                }
                case("Mitä Tampereella voi tehdä"): {
                    runOnUiThread(()-> {
                        hint1.setText("Mitä museoita Tampereella on?");
                        hint2.setText("Kerro minulle Tampereen nähtävyyksistä");
                        hint3.setText("Mitä jääkiekkojoukkueita Tampereella on?");
                        hint4.setText("");
                        hint5.setText("");
                    });
                    break;
                }
                case("Kerro itsestäsi"): {
                    runOnUiThread(()->{
                        hint1.setText("Mitä kanssasi voi tehdä?");
                        hint2.setText("");
                        hint3.setText("");
                        hint4.setText("");
                        hint5.setText("");
                    });
                    break;
                }
                case("Mitä kanssasi voi tehdä"): {
                    runOnUiThread(()->{
                        hint1.setText("Mikä on Wumpus-peli?");
                        hint2.setText("");
                        hint3.setText("");
                        hint4.setText("");
                        hint5.setText("");
                    });
                    break;
                }
                default: {
                    runOnUiThread(()->{
                        hint1.setText("Kerro minulle Tampereesta");
                        hint2.setText("Kerro itsestäsi");
                        hint3.setText("");
                        hint4.setText("");
                        hint5.setText("");
                    });
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onRobotFocusLost() {
        Log.i("focus", "focus lost");
        runOnUiThread(() -> background.setBackgroundColor(com.google.android.material.R.color.design_default_color_error));
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
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }
}