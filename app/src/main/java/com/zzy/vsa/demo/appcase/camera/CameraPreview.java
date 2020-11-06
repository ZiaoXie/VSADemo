package com.zzy.vsa.demo.appcase.camera;

import android.content.Context;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    String TAG = "CameraPreview";
    private final SurfaceHolder mHolder;
    private Camera mCamera;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri outputMediaFileUri;
    private File photoFile;
    private MediaRecorder mMediaRecorder;
    private String mVideoPath;


    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public boolean isImageUseful(){
        return null != mCamera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //surface第一次创建时回调
        //打开相机
        if(mCamera==null){
            mCamera = Camera.open();
            Log.i("相机","获取相机");
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        photoFile = FileUtil.initPhotoStorage();
//        try {
//            photoFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //surface变化的时候回调(格式/大小)
        if(mCamera==null) {
            mCamera = Camera.open();
        }
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //surface销毁的时候回调
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

        Log.d("相机", "相机销毁");
    }

    public Uri getOutputMediaFileUri() {
        return outputMediaFileUri;
    }


    public File getPhotoFile(){
        return photoFile;
    }



    volatile byte[] imagedata = null ;
    public void takePicture(final Context context) {
        Log.i("test","开始");
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i("test","回调");
                imagedata = data;
                try {
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    fos.write(imagedata);
                    fos.close();
                    mCamera.startPreview();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    context.sendBroadcast(new Intent(AppEnv.PACKAGE_NAME+".image_done"));
                }

            }
        });

//        try {
////            countDownLatch.await();
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }



    public void startRecord(String path) {
        if(TextUtils.isEmpty(path)){
            Log.i(TAG,"Camera1 Record path is empty");
            return;
        }

        mVideoPath=path;
        setUpMediaRecorder();


        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            Log.e(TAG,"Camera1 has start record");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG,"Camera1 start failed:"+e.getMessage());
        }

    }

    //这个方法的顺序很重要，并且一些设置还不能少
    private void setUpMediaRecorder(){
        //要在实例化MediaRecorder之前就解锁好相机
        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        //将相机设置给MediaRecorder
        mMediaRecorder.setCamera(mCamera);
        // 设置录制视频源和音频源
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置录制的视频编码和音频编码
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mMediaRecorder.setVideoSize(1920, 1080);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoEncodingBitRate(1024*1024*20);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        // 设置视频文件输出的路径
        mMediaRecorder.setOutputFile(mVideoPath);


        Log.i(TAG,"Camera1 OutputFilePath:"+mVideoPath);

        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.d(TAG,"MediaRecorder error:"+what+"-"+extra);
            }
        });

    }

    public void stopRecord() {
        if(mMediaRecorder!=null){
            mCamera.lock();
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder=null;
            Log.d(TAG,"Camera1 has stop record");
        }
    }

    class ImageLoaderTask extends AsyncTask{



        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }

}



