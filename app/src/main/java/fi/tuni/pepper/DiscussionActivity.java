package fi.tuni.pepper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class DiscussionActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private View background;
    private Chat chat;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("create", "created");
        QiSDK.register(this, this);
        background = findViewById(R.id.background);
        text = findViewById(R.id.text);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i("focus", "focus gained");
        runOnUiThread(() -> background.setBackgroundColor(R.color.white));
        Animation animation = AnimationBuilder.with(qiContext).withResources(R.raw.wave).build();
        Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
        animate.async().run();
        Say say = SayBuilder.with(qiContext)
                .withText("Hei ihminen!")
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
            runOnUiThread(() -> text.setText(heardPhrase.getText()));
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