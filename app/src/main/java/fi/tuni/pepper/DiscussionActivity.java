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
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        Log.i("create", "created");
        QiSDK.register(this, this);
        background = findViewById(R.id.background);
        text = findViewById(R.id.text);
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