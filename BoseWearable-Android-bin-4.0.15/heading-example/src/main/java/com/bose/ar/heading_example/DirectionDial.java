package com.bose.ar.heading_example;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DirectionDial extends SurfaceView  implements SurfaceHolder.Callback  {
    MainActivity main;
    int height;
    int width;

    public DirectionDial(Context context) {
        super(context);

    }
    public DirectionDial(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionDial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawColor();
        drawArrow(80);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    public void setup(MainActivity main) {
        this.main = main;
        getHolder().addCallback(this);
    }

    private void drawColor() {
        Canvas canvas = getHolder().lockCanvas();
        height = canvas.getHeight();
        width = canvas.getWidth();
        canvas.drawColor(getResources().getColor(R.color.colorAccent));
        getHolder().unlockCanvasAndPost(canvas);
    }


    public void drawArrow(float angle) {
        angle = angle - 90;
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(getResources().getColor(R.color.colorPrimary));

        Paint wallpaint = new Paint();
        wallpaint.setColor(Color.CYAN);
        wallpaint.setStyle(Paint.Style.FILL);

        double tipX = width / 2.0 + (Math.cos(Math.toRadians((double)angle)) * width / 2);
        double tipY = height / 2.0 + (Math.sin(Math.toRadians((double)angle)) * width / 2);

        double offsetLX = Math.cos(Math.toRadians(angle - 90)) * 20;
        double offsetLY = Math.sin(Math.toRadians(angle - 90)) * 20;

        double offsetRX = Math.cos(Math.toRadians(angle + 90)) * 20;
        double offsetRY = Math.sin(Math.toRadians(angle + 90)) * 20;

        double x90 =  width / 2.0 + (Math.cos(Math.toRadians((double)angle)) * (width / 2.0) * 0.8);
        double y90 =  height / 2.0 + (Math.sin(Math.toRadians((double)angle)) * (width / 2.0) * 0.8);

        double tipLeftX = x90 - 3 * offsetLX;
        double tipLeftY = y90  - 3 * offsetLY;

        double tipRightX = x90  - 3 * offsetRX;
        double tipRightY = y90  - 3 * offsetRY;



        Path wallpath = new Path();
        wallpath.reset(); // only needed when reusing this path for a new build
        wallpath.moveTo(width / 2, height / 2); // used for first poin
        wallpath.lineTo((float)(width / 2 - offsetLX), (float)(height / 2 - offsetLY));

        wallpath.lineTo((float)(x90  - offsetLX), (float)(y90 - offsetLY));
        wallpath.lineTo((float)tipLeftX, (float)tipLeftY);
        wallpath.lineTo((float)tipX,  (float)tipY);
        wallpath.lineTo((float)tipRightX, (float)tipRightY);
        wallpath.lineTo((float)(x90  - offsetRX), (float)(y90 - offsetRY));

        wallpath.lineTo((float)(width / 2 - offsetRX), (float)(height / 2 - offsetRY));
        wallpath.lineTo(width / 2, height / 2);

        canvas.drawPath(wallpath, wallpaint);
        getHolder().unlockCanvasAndPost(canvas);

    }




}
