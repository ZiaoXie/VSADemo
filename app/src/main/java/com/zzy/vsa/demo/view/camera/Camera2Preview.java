package com.zzy.vsa.demo.view.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.zzy.vsa.demo.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Preview extends TextureView {
    private static final String TAG = "Camera2Preview";
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    protected CameraCaptureSession mCameraCaptureSessions;
    protected CaptureRequest.Builder mPreviewRequestBuilder;
    private ImageReader mImageReader;
    private Context mContext;

    private Size mPreviewSize;

    public File outputfile;

    private static final String CAMERA_FONT = "0";
    private static final String CAMERA_BACK = "1";
    private String mCameraId;
    public Camera2Preview(Context context) {
        this(context, null);
        this.mContext = context;
        setKeepScreenOn(true);
        outputfile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
    }


    public Camera2Preview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
        setKeepScreenOn(true);
    }


    public Camera2Preview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setKeepScreenOn(true);
    }

    SurfaceTextureListener textureListener = new SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //开启摄像头
            setupCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public void onResume(){
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (isAvailable()) {
            setupCamera(getWidth(),getHeight());
        } else {
            setSurfaceTextureListener(textureListener);
        }
    }


    public void onPause() {
        Log.e(TAG, "onPause");
        if (mAvcEncoder != null) {
            mAvcEncoder.stopThread();
            mAvcEncoder = null;
        }
        closeCamera();
        stopBackgroundThread();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void closeCamera() {
        closePreviewSession();

        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    private void closePreviewSession() {
        if (null != mCameraCaptureSessions) {
            mCameraCaptureSessions.close();
            mCameraCaptureSessions = null;
        }
    }

    /**
     * 开启摄像机线程
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

    }

    /**
     * 开启摄像头
     */
    private void setupCamera(int width,int height) {
        Log.e(TAG,"setupCamera START");
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                // 默认打开后置摄像头 - 忽略前置摄像头
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) continue;
                // 获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                assert map != null;
                //获取预览输出尺寸
                mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class),width,height);
                Log.e(TAG, "setupCamera: best preview size width=" + mPreviewSize.getWidth()
                        + ",height=" + mPreviewSize.getHeight());
                transformImage(mPreviewSize.getWidth(),mPreviewSize.getHeight());

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                setupImageReader();

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }
                mCameraId = cameraId;
                break;
            }
            mCameraManager.openCamera(mCameraId,stateCallback,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        Log.e(TAG,"setupCamera END");
    }


    private Size getPreferredPreviewSize(Size[] mapSizes, int width, int height) {
        Log.e(TAG, "getPreferredPreviewSize: surface width=" + width + ",surface height=" + height);
        List<Size> collectorSizes = new ArrayList<>();
        for (Size option : mapSizes) {
            if (width > height) {
                if (option.getWidth() > width &&
                        option.getHeight() > height) {
                    collectorSizes.add(option);
                }
            } else {
                if (option.getWidth() > height &&
                        option.getHeight() > width) {
                    collectorSizes.add(option);
                }
            }
        }
        if (collectorSizes.size() > 0) {
            return Collections.min(collectorSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        Log.e(TAG, "getPreferredPreviewSize: best width=" +
                mapSizes[0].getWidth() + ",height=" + mapSizes[0].getHeight());
        return mapSizes[0];
    }


    private void transformImage(int width, int height) {
        Matrix matrix = new Matrix();
        int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(),
                    centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / mPreviewSize.getWidth(),
                    (float) height / mPreviewSize.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        setTransform(matrix);
    }

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_RECORD = 1;
    private int mState = STATE_PREVIEW;
    private AvcEncoder mAvcEncoder;
    private int mFrameRate = 30;

    private void setupImageReader() {
        Log.e(TAG, "setupImageReader width=" + mPreviewSize.getWidth() + ",height=" + mPreviewSize.getHeight());
        //2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(),mPreviewSize.getHeight(), ImageFormat.YUV_420_888,1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.e(TAG, "onImageAvailable: "+ Thread.currentThread().getName() );
                //这里一定要调用reader.acquireNextImage()和img.close方法否则不会一直回掉了
                Image img = reader.acquireNextImage();
                switch (mState){
                    case STATE_PREVIEW:
                        Log.e(TAG, "mState: STATE_PREVIEW");
                        if (mAvcEncoder != null) {
                            mAvcEncoder.stopThread();
                            mAvcEncoder = null;
                            Toast.makeText(mContext,"停止录制视频成功", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case STATE_RECORD:
                        Log.e(TAG, "mState: STATE_RECORD");
                        Image.Plane[] planes = img.getPlanes();
                        byte[] dataYUV = null;
                        if (planes.length >= 3) {
                            ByteBuffer bufferY = planes[0].getBuffer();
                            ByteBuffer bufferU = planes[1].getBuffer();
                            ByteBuffer bufferV = planes[2].getBuffer();
                            int lengthY = bufferY.remaining();
                            int lengthU = bufferU.remaining();
                            int lengthV = bufferV.remaining();
                            dataYUV = new byte[lengthY + lengthU + lengthV];
                            bufferY.get(dataYUV, 0, lengthY);
                            bufferU.get(dataYUV, lengthY, lengthU);
                            bufferV.get(dataYUV, lengthY + lengthU, lengthV);
                        }

                        if (mAvcEncoder == null) {
                            mAvcEncoder = new AvcEncoder(mPreviewSize.getWidth(),
                                    mPreviewSize.getHeight(), mFrameRate,
                                    outputfile, false);
                            mAvcEncoder.startEncoderThread();
                            Toast.makeText(mContext, "开始录制视频成功", Toast.LENGTH_SHORT).show();
                        }
                        mAvcEncoder.putYUVData(dataYUV);
                        break;
                        default:
                            break;
                }
                img.close();
            }
        },mBackgroundHandler);
    }

    /**
     * 获取输出照片视频路径
     *
     * @param mediaType
     * @return
     */
    public File getOutputMediaFile(int mediaType) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = null;
        File storageDir = null;
        if (mediaType == MEDIA_TYPE_IMAGE) {
            fileName = "JPEG_" + timeStamp;
//            storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        } else if (mediaType == MEDIA_TYPE_VIDEO) {
            fileName = "MP4_" + timeStamp;
//            storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
        storageDir = new File(FileUtil.getAppRootPath());

        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        File file = null;
        try {
//            file = File.createTempFile(
//                    fileName,  /* prefix */
//                    (mediaType == MEDIA_TYPE_IMAGE) ? ".jpg" : ".📛avc",         /* suffix */
//                    storageDir      /* directory */
//            );
            String suffix = ((mediaType == MEDIA_TYPE_IMAGE) ? ".jpg" : ".📛mp4");
            file = new File(storageDir + File.separator + fileName + suffix);
            Log.d(TAG, "getOutputMediaFile: absolutePath==" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG,"onOpened...");
            mCameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.e(TAG,"onDisconnected...");
            mCameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    };

    /**
     * 创建预览界面
     */
    private void createCameraPreview() {
        try {
            Log.e(TAG,"createCameraPreview");
            //获取当前TextureView的SurfaceTexture
            SurfaceTexture texture = getSurfaceTexture();
            assert texture != null;
            //设置SurfaceTexture默认的缓存区大小，为 上面得到的预览的size大小
            texture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
            Surface surface = new Surface(texture);
            //创建CaptureRequest对象，并且声明类型为TEMPLATE_PREVIEW，可以看出是一个预览类型

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            //CaptureRequest.
            //设置请求的结果返回到到Surface上
            mPreviewRequestBuilder.addTarget(surface);

            mPreviewRequestBuilder.addTarget(mImageReader.getSurface());

            //创建CaptureSession对象
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    //The camera is already closed
                    if (null == mCameraDevice) {
                        return;
                    }
                    Log.e(TAG, "onConfigured: ");
                    // When the session is ready, we start displaying the preview.
                    mCameraCaptureSessions = session;
                    //更新预览
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(mContext, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            },null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    /**
     * 更新预览
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        Log.e(TAG, "updatePreview: ");
        //设置相机的控制模式为自动，方法具体含义点进去（auto-exposure, auto-white-balance, auto-focus）
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            //设置重复捕获图片信息
            mCameraCaptureSessions.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }



    public boolean toggleVideo() {
        if (mState == STATE_PREVIEW) {
            mState = STATE_RECORD;
            return true;
        } else {
            mState = STATE_PREVIEW;
            return false;
        }
    }

}
