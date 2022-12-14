package fi.tuni.pepper;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Say;


public class GuideActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private String text = "";
    private View read_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Log.i("create", "created");
        QiSDK.register(this, this);
        View back = findViewById(R.id.back);
        read_btn = findViewById(R.id.read);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), GameActivity.class);
            intent.putExtra("fromOtherActivity", true);
            view.getContext().startActivity(intent);
        });
        LinearLayout guide_texts = findViewById(R.id.guide_texts);
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < guide_texts.getChildCount(); i++) {
            TextView child = (TextView) guide_texts.getChildAt(i);
            output.append(child.getText().toString());
            output.append(" ");
        }
        text = output.toString();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i("focus", "focus gained");
        read_btn.setOnClickListener((view) -> new Thread(() -> {
            runOnUiThread(()->read_btn.setEnabled(false));
            Say say = SayBuilder.with(qiContext)
                    .withText(text)
                    .withBodyLanguageOption(BodyLanguageOption.DISABLED)
                    .build();
            Future<Void> sayFuture = say.async().run();
            sayFuture.andThenConsume(ignore -> runOnUiThread(()->read_btn.setEnabled(true)));
        }).start());

    }

    @Override
    public void onRobotFocusLost() {
        Log.i("focus", "focus lost");
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }
}