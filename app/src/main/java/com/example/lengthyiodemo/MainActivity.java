package com.example.lengthyiodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    Button button2;

    ExecutorService executor;
    Future<String> future;

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString("myKey");
            textView.setText(string);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick(view);
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusClick(view);
            }
        });


    }


    public void buttonClick(View view) {

        executor = Executors.newSingleThreadExecutor();

        future = executor.submit(new Callable<String>() {
            @Override
            public String call() {
                long endTime = System.currentTimeMillis() + 10 * 1000;
                while (System.currentTimeMillis() < endTime){
                    synchronized(this){
                        try{
                            wait(endTime - System.currentTimeMillis());
                        }catch(Exception e){e.printStackTrace();}
                    }
                }
                return("Task Completed");
            }
        });
    }

    public void statusClick(View view){
        if (future.isDone()){
            String result = null;
            try {
                result = future.get(300, TimeUnit.MILLISECONDS);
            } catch(Exception e){
                e.printStackTrace();
            }
            textView.setText(result);
            executor.shutdownNow();
        } else {
            textView.setText("Waiting");
        }

    }
}