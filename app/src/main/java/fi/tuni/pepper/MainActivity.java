package fi.tuni.pepper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Say;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private Boolean playAnimation = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            playAnimation = extras == null;
        }
        Log.i("create", "created");
        Button peli = findViewById(R.id.peli);
        peli.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), GameActivity.class);
            view.getContext().startActivity(intent);
        });
        Button keskustelu = findViewById(R.id.keskustelu);
        keskustelu.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), DiscussionActivity.class);
            view.getContext().startActivity(intent);
        });
    }


    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i("focus", "focus gained");
        if(playAnimation) {
            Animation animation = AnimationBuilder.with(qiContext).withResources(R.raw.wave).build();
            Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
            animate.async().run();
            Say say = SayBuilder.with(qiContext)
                    .withText("Hei ihminen!")
                    .build();
            say.run();
        }
    }

    @Override
    public void onRobotFocusLost() {
        Log.i("focus", "focus lost");
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}