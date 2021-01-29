package com.bluetoothexample.thundercap_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


        public void onBtnClick (View view){
            //TextView txtHello = findViewById(R.id.txtHello);
            //Button button = findViewById(R.id.button);
            // button.setBackgroundColor(45);
            //Button b1 = findViewById(R.id.button);
            // b1.setBackgroundColor(Color.parseColor("FF51F60B"));
            EditText launchDelay1 = findViewById(R.id.editTextNumber);
            EditText launchDelay2 = findViewById(R.id.editTextNumber2);
            EditText launchDelay3 = findViewById(R.id.editTextNumber3);
            EditText launchDelay4 = findViewById(R.id.editTextNumber4);
            // Button b1 = findViewById(R.id.button4);
            String delay1 = launchDelay1.getText().toString();
            String delay2 = launchDelay2.getText().toString();
            String delay3 = launchDelay3.getText().toString();
            String delay4 = launchDelay4.getText().toString();

            int finalValue1 = Integer.parseInt(delay1);
            int finalValue2 = Integer.parseInt(delay2);
            int finalValue3 = Integer.parseInt(delay3);
            int finalValue4 = Integer.parseInt(delay4);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button b1 = findViewById(R.id.button4);
                    b1.setBackgroundColor(Color.GREEN);

                }
            }, finalValue1 * 1000);

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button b2 = findViewById(R.id.button2);
                    b2.setBackgroundColor(Color.GREEN);

                }
            }, finalValue2 * 1000);

            final Handler handler3 = new Handler();
            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button b3 = findViewById(R.id.button3);
                    b3.setBackgroundColor(Color.GREEN);

                }
            }, finalValue3 * 1000);

            final Handler handler4 = new Handler();
            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button b4 = findViewById(R.id.button5);
                    b4.setBackgroundColor(Color.GREEN);

                }
            }, finalValue4 * 1000);
        /*
        try
       {
            Thread.sleep(finalValue1 * 1000);
            Button b1 = findViewById(R.id.button4);
            b1.setBackgroundColor(Color.GREEN);
        }
        catch(InterruptedException ex)
        {
           Thread.currentThread().interrupt();
        }
       //txtHello.setText("Hello Jake");
        //Button b1 = findViewById(R.id.button4);
       // b1.setBackgroundColor(Color.GREEN);


        try
        {
            Thread.sleep(finalValue2 * 1000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        Button b2 = findViewById(R.id.button2);
        b2.setBackgroundColor(Color.GREEN);*/
        }
    }
