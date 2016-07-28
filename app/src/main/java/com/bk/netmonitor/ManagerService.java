package com.bk.netmonitor;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Bk on 2016/7/28.
 */
public class ManagerService extends Service {
    private static final String TAG = "ManagerService";
    private LinearLayout mLinearLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private TextView mTextView;
    private ServiceBinder mBinder = new ServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void createFloatView(){
        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        mLayoutParams.format = PixelFormat.RGBA_8888; //图片格式， 背景透明
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //不可聚焦
        mLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
        mLayoutParams.width = WidgetUtils.dpToPx(getApplicationContext(),65);
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.x = (WidgetUtils.getScreenWidth(getApplication()) - mLayoutParams.width)/2;
        mLayoutParams.y = 10;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.float_layout,null);
        mWindowManager.addView(mLinearLayout,mLayoutParams);
        mTextView = (TextView) mLinearLayout.findViewById(R.id.speed);
        mLinearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                ,View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mLayoutParams.x = (int) motionEvent.getRawX() - mLinearLayout.getMeasuredWidth()/2;
                mLayoutParams.y = (int) motionEvent.getRawY() - mLinearLayout.getMeasuredHeight()/2 -25;
                mWindowManager.updateViewLayout(mLinearLayout,mLayoutParams);
                return false;
            }
        });

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2016/7/28 do whatever you want
            }
        });
    }

    public void setSpeed(String speed){
        mTextView.setText(speed);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLinearLayout != null && mWindowManager != null){
            mWindowManager.removeView(mLinearLayout);
        }
        startService(new Intent(this, ManagerService.class));
    }

    class ServiceBinder extends Binder{
        public  ManagerService getService(){
            return ManagerService.this;
        }
    }
}
