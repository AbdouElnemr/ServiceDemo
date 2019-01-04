package com.example.elnemr.servicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txStartedServiceResult, txIntentServiceResult;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txIntentServiceResult = findViewById(R.id.txvIntentServiceREsult);
        txStartedServiceResult = findViewById(R.id.txvStartedServiceREsult);
    }

    public void startStartedService(View view) {
        Intent intent = new Intent(MainActivity.this, MyStartedService.class);
        intent.putExtra("sleepTime", 10);
        startService(intent);
    }

    public void stopStartedService(View view) {
        Intent intent = new Intent(MainActivity.this, MyStartedService.class);
        stopService(intent);
    }

    public void startIntentService(View view) {
        myResultReceiver myResultReciver = new myResultReceiver(null);

        Intent intent = new Intent(MainActivity.this, MyIntentService.class);
        intent.putExtra("sleepTime", 10);
        intent.putExtra("receiver", myResultReciver);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.service.to.activity");
        registerReceiver(myStartedServiceReceiver, intentFilter);
    }

    // this class to get the result  back from the mystartedService.java using broadcastreciver
    private BroadcastReceiver myStartedServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String data = intent.getStringExtra("sendResultBroadcast");
            txStartedServiceResult.setText(data);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myStartedServiceReceiver);
    }

    // this class to get the result  back from the myintentService.java using resultreceiver
    private class myResultReceiver extends ResultReceiver {
        public myResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            Log.i("MyResultReceiver", Thread.currentThread().getName());
            if (resultCode == 10 && resultData != null) {
                final String data = resultData.getString("resultIntentService");
                // because the textview is in main thread and this method
                // run on the background Thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MyHandler", Thread.currentThread().getName());
                        txIntentServiceResult.setText(data.toString());
                    }
                });
            }
        }
    }
}
