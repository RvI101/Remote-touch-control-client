package com.example.android.wifidirect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
    An activity that uses a custom surface view as its main view. This activity registers touch events on the surface
     and sends its touch co-ordinates to the server device.
 */
public class RemoteScreen extends Activity implements View.OnTouchListener, View.OnDragListener{
    RemoteView remoteView;
    List<Integer> coordinates = new ArrayList<Integer>();
    Bundle b;
    //StringBuilder builder = new StringBuilder();
    float x = 0;
    float y = 0;
    float x1 = 0;
    float y1 = 0;
    int action = 0;
    Intent returnIntent;
    //DeviceDetailFragment DeviceDetails;
    //WifiP2pDevice Device;
    String IP_SERVER;
    int PORT;
    String localIP;
    String clientIP;
    String client_mac_fixed;
    String deviceAddress;
    int width;
    int height;
    String type;
    int touches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remoteView = new RemoteView(this);
        b = new Bundle();
        Log.d("RemoteScreen", "Before getting the intent extras");
        returnIntent = new Intent();
        returnIntent = getIntent();
        touches = 0;
        deviceAddress = returnIntent.getStringExtra("DeviceAddress");
        //Log.d("Remote Screen onCreate", deviceAddress);
        IP_SERVER = returnIntent.getStringExtra("IP_SERVER");
        PORT = returnIntent.getIntExtra("PORT", 8988);
        localIP = Utils.getLocalIPAddress();
        // Trick to find the ip in the file /proc/net/arp
        client_mac_fixed = new String(deviceAddress).replace("99", "19");
        Log.d("Remote Screen onCreate", client_mac_fixed);
        clientIP = Utils.getIPFromMac(client_mac_fixed);
        Log.d("Remote Screen onCreate", localIP);
        remoteView.setOnTouchListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        setContentView(remoteView);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        remoteView.resume();
        //May have to reinitialise returnIntent here
    }

    @Override
    public void onPause()
    {
        super.onPause();
        remoteView.pause();
    }

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            x*=-1;
            y*=-1;
            Log.d("Touch Special", "Long press");
            sendData();
        }
    };
    @Override
    public boolean onTouch(View v, MotionEvent motionEvent)
    {
        touches++;
        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                handler.postDelayed(mLongPressed, 1000);
                x = (motionEvent.getX()/(float)width);
                y = (motionEvent.getY()/(float)height);
                x1 = x;
                y1 = y;
                Log.d("onTouchDown",x + "," + y);
                //Log.d("Width, Height", width + "x" + height);
                //coordinates.add(x);
                //coordinates.add(y);
                action = 0;
                type = "DOWN";
                //if(touches%100==0)
                sendData();
                break;
            case MotionEvent.ACTION_MOVE:
                x = (motionEvent.getX()/(float)width);
                y = (motionEvent.getY()/(float)height);
                handler.removeCallbacks(mLongPressed);

                if(dist(x1, y1, x, y) > 18) {
                    Log.d("onDrag", x + "," + y);
                    action = 1;
                    type = "DRAG";
                    sendData();
                    x1 = x;
                    y1 = y;
                }

                break;
            case MotionEvent.ACTION_UP:
                x = (motionEvent.getX()/(float)width);
                y = (motionEvent.getY()/(float)height);
                handler.removeCallbacks(mLongPressed);
                Log.d("onTouchUp", x + "," + y);
                //Log.d("At end of swipe", "At" + System.currentTimeMillis());
                Log.d("Width, Height", width + "x" + height);
                //coordinates.add(x);
                //coordinates.add(y);
                action = 2;
                type = "UP";
                sendData();
                break;


        }
        return true;
    }
    public int dist(float x, float y, float x1, float y1)
    {
        x = x * (float)width;
        x1 = x1 * (float)width;
        y = y * (float)height;
        y1 = y1 * (float)height;
        return (int)Math.sqrt(Math.pow((x1 - x),2) + Math.pow((y1 - y), 2));
    }
    public void sendData()
    {
        Intent serviceIntent = new Intent(this, FileTransferService.class);
        //Log.d("SendData", clientIP);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_NUMBER_STREAM, x + " " + y + " " + action);
        serviceIntent.putExtra("TYPE", type);//useless right now

        if(localIP.equals(IP_SERVER)){
            serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, clientIP);
        }
        else{
            serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, IP_SERVER);
        }

        serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, PORT);
        startService(serviceIntent);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {//also useless right now

        return false;
    }
}


