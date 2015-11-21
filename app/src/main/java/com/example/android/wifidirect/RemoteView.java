package com.example.android.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pDevice;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO: document your custom view class.
 */
public class RemoteView extends SurfaceView implements Runnable{

    SurfaceHolder holder;
    Thread paintThread;
    boolean running = false;

    float tx;
    float ty;
    Paint mPaint;

    public RemoteView(Context context) {
        super(context);
        holder = getHolder();
        tx = -100;
        ty = -100;
        mPaint = new Paint();
        mPaint.setColor(Color.CYAN);
    }

    public RemoteView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

   public void resume()
   {
       paintThread = new Thread(this);
       running = true;
       paintThread.start();
   }
    public void pause()
    {
        running = false;
    }

    @Override
    public void run() {
        while(running)
        {
            if(!holder.getSurface().isValid())
                continue;
            Canvas canvas = holder.lockCanvas();
            canvas.drawARGB(120,0,0,0);
            holder.unlockCanvasAndPost(canvas);

        }
    }





}



