package com.zzy.vsa.demo.appcase.fileoperation;

import android.util.Log;
import com.zzy.vsa.demo.util.Native;


public class Fileopreration {

    public static class CopyPartThread extends Thread {
        private String mSrc;
        private String mdes;
        private int offset;
        private int length;

        CopyPartThread(String src, String des, int offset1, int length1) {
            mSrc = src;
            mdes = des;
            offset = offset1;
            length = length1;
        }

        @Override
        public void run() {
            super.run();

            Native.copyFilePart(mSrc, mdes, offset, length);
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

        new CopyPartThread(selectable,inapt,offset1,quater).start();
        new CopyPartThread(selectable,inapt,offset2,quater).start();
        new CopyPartThread(selectable,inapt,offset3,quater).start();
        new CopyPartThread(selectable,inapt,offset4,a - offset4).start();

    }
}
