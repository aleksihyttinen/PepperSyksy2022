package fi.tuni.pepper;

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


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private Button peli;
    private Button keskustelu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);
        Log.i("create", "created");
        peli = findViewById(R.id.peli);
        keskustelu = findViewById(R.id.keskustelu);
        keskustelu.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), DiscussionActivity.class);
            view.getContext().startActivity(intent);
        });
    }


    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i("focus", "focus gained");
        Animation animation = AnimationBuilder.with(qiContext).withResources(R.raw.wave).build();
        Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
        animate.async().run();
        Say say = SayBuilder.with(qiContext)
                .withText("Hei ihminen!")
                .build();
        say.run();
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}