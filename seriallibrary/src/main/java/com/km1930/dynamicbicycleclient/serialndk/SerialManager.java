package com.km1930.dynamicbicycleclient.serialndk;

import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:
 * Author：Giousa
 * Date：2016/8/15
 * Email：giousa@chinayoutu.com
 */
public class SerialManager {

    private final String TAG = SerialManager.class.getSimpleName();
    private static SerialManager mSerialManager;
    private final Timer timer = new Timer();
    private TimerTask task;
    private boolean isSerialOpend;
    private short[] mShorts;
    private int serialBuf;

    public static SerialManager getInstance() {
        Log.d("SerialManager", "getInstance");
//        if (mSerialManager == null) {
        mSerialManager = new SerialManager();
//        }

        return mSerialManager;

    }

    public interface SerialSpeedChangeListener {
        void onSerialSpeedChanged(short speed);
    }

    public interface SerialAngleChangeListener {
        void onSerialAngleChanged(float angle);
    }

    public interface SerialDistanceChangeListener {
        void onSerialDistanceChanged(float distance, short speed);
    }

    private SerialSpeedChangeListener mSerialSpeedChangeListener;
    private SerialAngleChangeListener mSerialAngleChangeListener;
    private SerialDistanceChangeListener mSerialDistanceChangeListener;

    public void setSerialSpeedChangeListener(SerialSpeedChangeListener serialSpeedChangeListener) {
        mSerialSpeedChangeListener = serialSpeedChangeListener;
    }

    public void setSerialAngleChangeListener(SerialAngleChangeListener serialAngleChangeListener) {
        mSerialAngleChangeListener = serialAngleChangeListener;
    }

    public void setSerialDistanceChangeListener(SerialDistanceChangeListener
                                                        serialDistanceChangeListener) {
        mSerialDistanceChangeListener = serialDistanceChangeListener;
    }

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            // 要做的事情
//            readSerial();
//            super.handleMessage(msg);
//        }
//    };

    private Handler mHandler = new Handler();
    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            readSerial();
            mHandler.postDelayed(mTimerRunnable, 50);
        }
    };

    public boolean isSerialOpend() {
        return isSerialOpend;
    }

    public void setSerialOpend(boolean serialOpend) {
        isSerialOpend = serialOpend;
    }

    public void openSerial() {
        Log.d(TAG, "openSerial");
        Serial.OpenSerial(4);
        setSerialBaud(555);
        isSerialOpend = true;
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
//            }
//        };
//
//        timer.schedule(task, 50, 100);
        mHandler.postDelayed(mTimerRunnable, 50);
    }

    public void closeSerial() {
//        mHandler.removeCallbacks(mTimerRunnable);
        mHandler.removeCallbacksAndMessages(null);
        Serial.CloseSerial();
        isSerialOpend = false;
    }

    public void setSerialBaud(long baud) {
        Serial.SetSerialBaud(baud);
    }

    private int noDataTimes = 0;

    public void readSerial() {
        mShorts = new short[9];
        serialBuf = 0;
        try {
            serialBuf = Serial.ReadSerialBuf(mShorts, 9);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "serialBuf:" + serialBuf);

        if (serialBuf == 9) {
            if (mShorts[0] == 0x55 && mShorts[1] == 0x01 && mShorts[8] == 0xAA) {
                parseSerialData(mShorts);
            }
            noDataTimes = 0;
        } else if (serialBuf == 0) {
            if (noDataTimes > 3) {
                mShorts[2] = 0;
                parseSerialData(mShorts);
                noDataTimes=0;
            } else {
                noDataTimes++;
            }
        }
    }

    public short mSpeed = 0;
    public float mAngle = 15;
    public float mDistance = 0;

    private void parseSerialData(short[] mShorts) {

//        LogUtils.d(TAG, "parseSerialData:  " + mShorts);

        short speed = mShorts[2];
        mDistance = mDistance + (float) (speed * 0.075) / 1000;
//        float dis = (float) ((Math.round(mDistance*100))/100);

        if (mSpeed != speed) {
            mSpeed = speed;
            if (mSerialSpeedChangeListener != null) {
                mSerialSpeedChangeListener.onSerialSpeedChanged(mSpeed);
            }
        }

        float angle = mShorts[3];
        if (mAngle != angle) {
            mAngle = angle;
            if (mSerialAngleChangeListener != null) {
                mSerialAngleChangeListener.onSerialAngleChanged(mAngle);
            }
        }


        if (mSerialDistanceChangeListener != null) {
            mSerialDistanceChangeListener.onSerialDistanceChanged(mDistance, speed);
        }

    }

    private short[] mNoweData;

    public void writeSerial(short WriteBuf) {
        if (mNoweData == null) {
            mNoweData = new short[9];
            mNoweData[0] = 0x55;
            mNoweData[1] = 0x02;
        }
        mNoweData[2] = WriteBuf;
        Serial.WriteSerialBuf(mNoweData, mNoweData.length);
    }
}
