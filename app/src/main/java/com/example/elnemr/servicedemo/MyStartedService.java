package com.example.elnemr.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class MyStartedService extends Service {

    private static final String TAG = MyStartedService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind , Thread Name " + Thread.currentThread().getName());
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate , Thread Name " + Thread.currentThread().getName());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand , Thread Name " + Thread.currentThread().getName());

        int sleepTime = intent.getIntExtra("sleepTime", 1);

        new MyAsyncTask().execute(sleepTime);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy , Thread Name " + Thread.currentThread().getName());
        super.onDestroy();
    }

    public class MyAsyncTask extends AsyncTask<Integer, String, String> {

        private final String TAG = MyAsyncTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            Log.i(TAG, " onPreExecute , Thread Name " + Thread.currentThread().getName());
            super.onPreExecute();
        }

        @Override //Long Time Tasks
        protected String doInBackground(Integer... voids) {
            Log.i(TAG, " doInBackground , Thread Name " + Thread.currentThread().getName());

            int sleepTime = voids[0];
            int ctr = 1;

            while(ctr <= sleepTime){
                publishProgress("Counter is now "+ ctr);
                try {
                    Thread.sleep( 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctr++;
            }
            return "Counter Ends at "+ ctr-- +" Seconds";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(MyStartedService.this, values[0], Toast.LENGTH_SHORT).show();
            Log.i(TAG, " Counter Value is "+values[0]+"" +
                    "onProgressUpdate Thread Name   " + Thread.currentThread().getName());

        }

        @Override
        protected void onPostExecute(String str) {
            Log.i(TAG, " onPostExecute , Thread Name " + Thread.currentThread().getName());
            super.onPostExecute(str);

            stopSelf(); //destroy the Service within the Service class Itself

            Intent intent = new Intent("action.service.to.activity");
            intent.putExtra("sendResultBroadcast", str);
            sendBroadcast(intent);
        }
    }

}
