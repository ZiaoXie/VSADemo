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
        private int moffset;
        private int mlength;

        CopyPartThread(String src, String des, int offset, int length) {
            mSrc = src;
            mdes = des;
            moffset = offset;
            mlength = length;
        }

        @Override
        public void run() {
            super.run();

            Native.copyFilePart(mSrc, mdes, moffset, mlength);

        }

        public void sendMessage(){

        }
    }

    public static void multiThreadCopy(final String selectable, final String inapt) {
        final int a = Native.getFileSize(selectable);
        Native.truncateFile(inapt,a );
        Log.d("filesize",a+"");
        final int quater = a / 4;
        final int offset1 = 0;
        final int offset2 = quater;
        final int offset3 = offset2 + quater;
        final int offset4 = offset3 + quater;


        CopyPartThread th1 =  new CopyPartThread(selectable,inapt,offset1,quater);
        CopyPartThread th2 = new CopyPartThread(selectable,inapt,offset2,quater);
        CopyPartThread th3 = new CopyPartThread(selectable,inapt,offset3,quater);
        CopyPartThread th4 = new CopyPartThread(selectable,inapt,offset4,a - offset4);

        th1.start();
        th2.start();
        th3.start();
        th4.start();
    }
}
