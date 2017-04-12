package app.firstapiaiapp.example.com.firstaiapiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
//Importing text buttons and views from activity_main.xml
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//Importing ai.api classes
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AIListener{


    private Button listenButton;
    private TextView resultTextView;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private AIService aiService;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
             super.onRequestPermissionsResult(requestCode, permissions, grantResults);
             switch (requestCode){
                       case REQUEST_RECORD_AUDIO_PERMISSION:
                               if (grantResults.length > 0
                                              && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                       // permission was granted, yay!
                                          }
                               else {
                                       // exit app
                                             finish();
                               }
             }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenButton = (Button) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        final AIConfiguration config = new AIConfiguration("a7527eff0fc04be69394e8ae2e44f87b", AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }


    public void listenButtonOnClick(final View view) {
        aiService.startListening();
    }

    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }


        // Show results in TextView.
        resultTextView.setText("Question:\n" + result.getResolvedQuery() +"\n"+
                "Response:\n" + result.getFulfillment().getSpeech());


    }

    @Override
    public void onError(final AIError error) {
        resultTextView.setText(error.toString());
    }
    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}

}
