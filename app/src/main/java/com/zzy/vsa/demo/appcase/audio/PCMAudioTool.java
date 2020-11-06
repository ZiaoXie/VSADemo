package com.zzy.vsa.demo.appcase.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.zzy.vsa.demo.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PCMAudioTool {

    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int mSampleRateInHz=44100 ;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig= AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat=AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private static final int mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz,mChannelConfig, mAudioFormat);

    private File mRecordingFile;//储存AudioRecord录下来的文件
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord=null;


    private String mPath;

    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;
    private DataOutputStream mDataOutputStream;

    public PCMAudioTool (){
        initDatas();
    }

    public boolean isUseful(){

        return null != mAudioRecord;
    }

    //初始化数据
    private void initDatas() {

        mRecordingFile = FileUtil.initPCMAudioStorage();
        mPath = mRecordingFile.getAbsolutePath();

        mAudioRecord = new AudioRecord(mAudioSource,mSampleRateInHz,mChannelConfig,
                mAudioFormat, mBufferSizeInBytes);//创建AudioRecorder对象

        mThread = null;
        mThread =new Thread(new Runnable() {
            @Override
            public void run() {
                //标记为开始采集状态
                isRecording = true;
                //创建一个流，存放从AudioRecord读取的数据

                if(mRecordingFile.exists()){//音频文件保存过了删除
                    mRecordingFile.delete();
                }
                try {
                    mRecordingFile.createNewFile();//创建新文件
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    //获取到文件的数据流
                    mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));
                    byte[] buffer = new byte[mBufferSizeInBytes];

                    //判断AudioRecord未初始化，停止录音的时候释放了，状态就为STATE_UNINITIALIZED
                    if(mAudioRecord.getState() == mAudioRecord.STATE_UNINITIALIZED){
                        initDatas();
                    }

                    mAudioRecord.startRecording();//开始录音
                    //getRecordingState获取当前AudioReroding是否正在采集数据的状态
                    while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                        int bufferReadResult = mAudioRecord.read(buffer,0,mBufferSizeInBytes);
                        for (int i = 0; i < bufferReadResult; i++)
                        {
                            mDataOutputStream.write(buffer[i]);
                        }
                    }
                    mDataOutputStream.close();
                } catch (Throwable t) {
                    stopRecord();
                }
            }
        });
    }


    //开始录音
    public String startRecord() {

        initDatas();

        //AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        }else{
           // destroyThread();
            isRecording = true;
            mThread.start();//开启线程
        }
        return mPath;
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(500);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }

    //停止录音
    public void stopRecord() {
        isRecording = false;
        //停止录音，回收AudioRecord对象，释放内存
        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {//初始化成功
                mAudioRecord.stop();
            }
            if (mAudioRecord  !=null ) {
                mAudioRecord.release();
            }
        }
        destroyThread();
    }

    //创建AudioTrack对象   依次传入 :流类型、采样率（与采集的要一致）、音频通道（采集是IN 播放时OUT）、量化位数、最小缓冲区、模式
    private final static AudioTrack player = new AudioTrack(AudioManager.STREAM_MUSIC,mSampleRateInHz,AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, mBufferSizeInBytes, AudioTrack.MODE_STREAM);

    //播放音频（PCM）
    public static void play(final String path)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dis=null;
                try {
                    //从音频文件中读取声音
                    dis=new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //最小缓存区
                int bufferSizeInBytes= AudioTrack.getMinBufferSize(mSampleRateInHz,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);

                byte[] data =new byte [bufferSizeInBytes];
                player.play();//开始播放
                while(true)
                {
                    int i=0;
                    try {
                        while(dis.available()>0&&i<data.length)
                        {
                            data[i]=dis.readByte();//录音时write Byte 那么读取时就该为readByte要相互对应
                            i++;
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    player.write(data,0,data.length);

                    if(i!=bufferSizeInBytes) //表示读取完了
                    {
                        player.stop();//停止播放
                        player.release();//释放资源
                        break;
                    }
                }
            }
        }).start();

    }



}
