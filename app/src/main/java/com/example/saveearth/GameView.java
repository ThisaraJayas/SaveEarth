package com.example.saveearth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.Random;


public class GameView  extends View {
    int deviceWidth, deviceHeight;
    Bitmap trash, hand, plastic;
    Handler handler;
    Runnable runnable;
    long UPDATE_MILLIS =30;
    int handX, handY;
    int plasticX, plasticY;
    Random random;
    boolean plasticAnimation=false;
    int points=0;
    float TEXT_SIZE=120;
    Paint textPaint;
    Paint healthPaint;
    int life=3;
    Context context;
    int handSpeed;
    int trashX, trashY;


    public GameView(Context context) {
        super(context);
        this.context=context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;
        trash = BitmapFactory.decodeResource(getResources(), R.drawable.nest);
        hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
        plastic = BitmapFactory.decodeResource(getResources(), R.drawable.egg);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        random = new Random();
        handX = deviceWidth+random.nextInt(300);
        handY =random.nextInt(600);
        plasticX=handX;
        plasticY=handY+hand.getHeight()-30;
        textPaint = new Paint();
        textPaint.setColor(Color.rgb(255,0,0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint=new Paint();
        healthPaint.setColor(Color.GREEN);
        handSpeed=21+random.nextInt(30);
        trashX=deviceWidth/2 - trash.getWidth()/2;
        trashY=deviceHeight - trash.getHeight();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {

        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);
        if(plasticAnimation==false){
            handX -=handSpeed;
            plasticX -= handSpeed;
        }
        if(handX<= -hand.getWidth()){
            handX=deviceWidth+random.nextInt(300);
            plasticX=handX;
            handY=random.nextInt(600);
            plasticY=handY+hand.getHeight()-30;
            handSpeed=21+random.nextInt(30);
            trashX=hand.getWidth()+random.nextInt(deviceWidth-2*hand.getWidth());
            life--;
            if(life==0){
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("points",points);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        }
        if(plasticAnimation){
            plasticY+=40;
        }
        if(plasticAnimation
        &&(plasticX+plastic.getWidth()>=trashX)
        &&(plasticX<=trashX+trash.getWidth())
        &&(plasticY+plastic.getHeight()>=(deviceHeight-trash.getHeight()))
        && plasticY<=deviceHeight){
            handX=deviceWidth+random.nextInt(300);
            plasticX=handX;
            handY=random.nextInt(600);
            plasticY=handY+hand.getHeight()-30;
            handSpeed=21+random.nextInt(30);
            points++;
            trashX=hand.getWidth()+random.nextInt(deviceWidth-2*hand.getWidth());
            plasticAnimation=false;
        }
        if(plasticAnimation && (plasticY+plastic.getHeight())>=deviceHeight){
            life--;
            if(life==0){
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("points",points);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
            handX = deviceWidth+random.nextInt(300);
            plasticX=handX;
            handY=random.nextInt(600);
            plasticY=handY+hand.getHeight()-30;
            trashX=hand.getWidth()+random.nextInt(deviceWidth-2*hand.getWidth());
            plasticAnimation =false;
        }
        canvas.drawBitmap(trash, trashX, trashY, null);
        canvas.drawBitmap(hand, handX, handY, null);
        canvas.drawBitmap(plastic, plasticX, plasticY, null);
        canvas.drawText(""+points, 20, TEXT_SIZE, textPaint);
        if(life==2)
            healthPaint.setColor(Color.YELLOW);
        else if (life==1)
            healthPaint.setColor(Color.RED);
        canvas.drawRect(deviceWidth-200,30,deviceWidth-200+60*life, 80, healthPaint);
        if(life!=0)
            handler.postDelayed(runnable,UPDATE_MILLIS);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(plasticAnimation==false &&
                    (touchX>=handX && touchX<=(handY+hand.getHeight()))){
                plasticAnimation=true;
            }
        }

        return true;
    }
}
