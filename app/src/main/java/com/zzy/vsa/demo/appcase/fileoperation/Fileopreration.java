package com.zzy.vsa.demo.appcase.fileoperation;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import com.zzy.vsa.demo.util.Native;
import com.zzy.vsa.demo.util.UHandler;


public class Fileopreration {

    public static class CopyPartThread extends Thread {
        private String mSrc;
        private String mdes;
        private long moffset;
        private long mlength;

        CopyPartThread(String src, String des, long offset, long length) {
            mSrc = src;
            mdes = des;
            moffset = offset;
            mlength = length;

        }

        @Override
        public void run() {
            super.run();
            Log.d("moffset = ",moffset + "");
            Log.d("length = ",mlength + "");
            Native.copyFile(mSrc, mdes, moffset, mlength, 1);
        }
    }
    public static class CopyByPThread extends Thread {
        private String mSrc;
        private String mdes;
        private long moffset;
        private long mlength;

        CopyByPThread(String src, String des, long offset, long length) {
            mSrc = src;
            mdes = des;
            moffset = offset;
            mlength = length;
        }

        @Override
        public void run() {
            super.run();
            Native.copyFile(mSrc, mdes, moffset, mlength, 3);
        }
    }

    public static void multiThreadCopy(final String selectable, final String inapt, int choose, long length ) {
        if(choose == 1) {
            final long a = length;
            Native.truncateFile(inapt, a);
            final long quater = a / 4;
            final long offset1 = 0;
            final long offset2 = quater;
            final long offset3 = offset2 + quater;
            final long offset4 = offset3 + quater;

            CopyPartThread th1 = new CopyPartThread (selectable, inapt, offset1, quater);
            Log.d("offset1 = ",offset1 + "");
            CopyPartThread th2 = new CopyPartThread (selectable, inapt, offset2, quater);
            Log.d("offset2 = ",offset2 + "");
            CopyPartThread th3 = new CopyPartThread (selectable, inapt, offset3, quater);
            Log.d("offset3 = ",offset3 + "");
            CopyPartThread th4 = new CopyPartThread (selectable, inapt, offset4, a - offset4);
            Log.d("offset4 = ",offset4 + "");
            th1.start();
            th2.start();
            th3.start();
            th4.start();
        }
        else if(choose == 2){
            Native.copyFile(selectable, inapt, 0, length, choose);
        }
        else if(choose == 3){
            final long a = length;
            Native.truncateFile(inapt, a);
            final long quater = a / 4;
            final long offset1 = 0;
            final long offset2 = quater;
            final long offset3 = offset2 + quater;
            final long offset4 = offset3 + quater;

            CopyByPThread th1 = new CopyByPThread(selectable, inapt, offset1, quater);
            CopyByPThread th2 = new CopyByPThread(selectable, inapt, offset2, quater);
            CopyByPThread th3 = new CopyByPThread(selectable, inapt, offset3, quater);
            CopyByPThread th4 = new CopyByPThread(selectable, inapt, offset4, a - offset4);
            th1.start();
            th2.start();
            th3.start();
            th4.start();
        }
    }
}






