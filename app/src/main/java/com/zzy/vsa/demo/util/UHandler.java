package com.zzy.vsa.demo.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import androidx.annotation.NonNull;

public class UHandler implements Handler.Callback {
    public final static int MSG_UPDATE_COPY_STATUS = 0;
    private final Handler mHandler;
    private final HandlerThread mThread;

    private static UHandler sInstance = null;
    public static Handler getHandler() {
        if (null == sInstance) {
            synchronized (UHandler.class) {
                if (null == sInstance) {
                    try {
                        sInstance = new UHandler();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sInstance.getmHandler();
    }

    private UHandler() {
        mThread = new HandlerThread("demo.handler");
        mThread.start();
        mHandler = new Handler(mThread.getLooper(), this);
    }

    private Handler getmHandler() {
        return mHandler;
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        switch (message.what) {
            case MSG_UPDATE_COPY_STATUS: {

            } break;
            default:
                break;
        }

        return true;
    }
}
