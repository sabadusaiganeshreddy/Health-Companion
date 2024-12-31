package com.example.first;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private EditText etSymptoms;
    private Button btnPredict;
    private TextView tvPrediction;
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSymptoms = findViewById(R.id.etSymptoms);
        btnPredict = findViewById(R.id.btnPredict);
        tvPrediction = findViewById(R.id.tvPrediction);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String symptoms = etSymptoms.getText().toString();
                float[] input = preprocessSymptoms(symptoms);
                float[] output = new float[1];
                tflite.run(input, output);
                tvPrediction.setText("Predicted Health Risk: " + Arrays.toString(output));
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(getAssets().openFd("model.tflite").getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = getAssets().openFd("model.tflite").getStartOffset();
        long declaredLength = getAssets().openFd("model.tflite").getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private float[] preprocessSymptoms(String symptoms) {
        //  i will code preprocessing logic here

        return new float[]{Float.parseFloat(symptoms)};
    }
}