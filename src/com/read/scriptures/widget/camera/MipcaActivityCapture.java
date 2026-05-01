package com.read.scriptures.widget.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.music.player.lib.util.XToast;
import com.read.scriptures.EIUtils.EIApplication;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.widget.camera.handler.CaptureActivityHandler;

import java.util.List;

/**
 * 这个Activity主要处理扫描界面的类，比如，扫描成功有声音和振动等等
 *
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
public class MipcaActivityCapture extends BaseActivity implements Callback, OnClickListener {
    /**
     * 音量大小
     */
    //private static final float BEEP_VOLUME = 0.10f;

    /**
     * 振动时长
     */
    private static final long VIBRATE_DURATION = 200L;

    /**
     * 超时时间
     */
    private static final int SCANTIMEOUT = 1;

    /**
     * Handler
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final android.os.Message msg) {
            if ((getScanTimeout() == msg.what) && (null != handler)) {
                handler.quitSynchronously();
                childTimeOutOpera(MipcaActivityCapture.this);
            }

        }

        ;
    };

    /**
     * 处理解码handle
     */
    private CaptureActivityHandler handler;

    private ViewfinderView viewfinderView;

    private boolean hasSurface;

    private List<BarcodeFormat> decodeFormats;

    private String characterSet;

    private InactivityTimer inactivityTimer;

    private MediaPlayer mediaPlayer;

    private boolean playBeep;

    private boolean vibrate;

    private boolean lightFlag = true;

    private String titleStr;

    /**
     * 监听音频播放
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        /**
         * 音频播放结束后回调方法
         */
        @Override
        public void onCompletion(final MediaPlayer medPlayer) {
            //音频播放结束回到开始位置
            medPlayer.seekTo(0);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scancode_activity_capture);

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        //设置扫描页面的title
        final TextView tv = (TextView) findViewById(R.id.navigation_label);
        tv.setText(titleStr);

        //返回键
        final View mButtonBack = findViewById(R.id.navigation_back);
        mButtonBack.setOnClickListener(this);

        //闪光灯
        final View mButtonLight = findViewById(R.id.navigation_right);
        mButtonLight.setVisibility(View.VISIBLE);
        mButtonLight.setOnClickListener(this);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    protected void setTitleStr(final String titleStr) {
        this.titleStr = titleStr;
    }

    /**
     * 生命周期方法
     */
    @Override
    protected void onResume() {
        super.onResume();

        //这里就是一系列初始化相机view的过程
        final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);

        //一般view都是在主线程更新，但是surfaceView是在子线程更新的(好像违反了主线程不安全原则)
        //surfaceView.getHolder() 有玄机，源码俺还没研究
        final SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            //            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        //这里就是看看是否是正常声音播放模式，如果是就播放声音，如果不是，则不播放
        playBeep = true;
        final AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }

        //初始化声音
        initBeepSound();

        //是否震动
        vibrate = true;

    }

    /**
     * 生命周期方法
     */
    @Override
    protected void onPause() {
        super.onPause();

        //关掉相机，关掉解码线程，清空looper队列中message
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }

        // 关掉相机
        CameraManager.getInstance(EIApplication.getInstance()).closeDriver();
    }

    /**
     * 生命周期方法
     */
    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result 结果
     */
    public void handleDecode(final Result result) {
        //重新计时
        inactivityTimer.onActivity();

        playBeepSoundAndVibrate();
        final String resultString = result.getText();
        if (resultString.equals("")) {
            XToast.showToast(HuDongApplication.getInstance(), "Scan failed!");
        } else {
            childhandleDecode(result);
        }
        finish();
    }

    /**
     * 初始化Camera
     *
     * @param surfaceHolder surfaceHolder
     */
    private void initCamera(final SurfaceHolder surfaceHolder) {
        try {
            CameraManager.getInstance(EIApplication.getInstance()).openDriver(surfaceHolder);
        } catch (final RuntimeException e) {
            XToast.showToast(HuDongApplication.getInstance(), getString(R.string.scan_camera_ban_log));
            return;
        }
        if (handler == null) {
            // 新建解码结果handler
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        // 初始化相机
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        hasSurface = false;

    }

    /**
     * 返回显示的view
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @return 返回显示的view
     * @see [类、类#方法、类#成员]
     */
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    /**
     * 返回处理解码结果的handler
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @return 返回处理解码结果的handler
     * @see [类、类#方法、类#成员]
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * 清空view中先前扫描成功的图片
     *
     * @see [类、类#方法、类#成员]
     */
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    /**
     * 播放音频
     * 扫描二维码完成有声音，声音文件R.raw.beep(现在是空文件，故扫描没有声音)
     */
    private void initBeepSound() {
        if (playBeep && (mediaPlayer == null)) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            //            try
            //            {
            //                final AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            //                //设置要播放的音频文件的位置。
            //                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            //                file.close();
            //
            //                //设置音量
            //                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            //
            //                //在开始播放之前调用这个方法完成准备工作
            //                mediaPlayer.prepare();
            //            }
            //            catch (final IOException e)
            //            {
            //                mediaPlayer = null;
            //            }
        }
    }

    /**
     * 播放音频和振动
     */
    private void playBeepSoundAndVibrate() {
        //播放音频
        if (playBeep && (mediaPlayer != null)) {
            //开始或继续播放音频。
            mediaPlayer.start();
        }

        //振动
        if (vibrate) {
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final View v) {
        //返回
        if (R.id.navigation_back == v.getId()) {
            MipcaActivityCapture.this.finish();
        }
        //闪光灯
        else if (R.id.navigation_right == v.getId()) {
            light();
        }

    }

    /**
     * 操作手机闪光灯
     *
     * @see [类、类#方法、类#成员]
     */
    private void light() {
        if (lightFlag) {
            lightFlag = false;
            // 开闪光灯
            CameraManager.getInstance(EIApplication.getInstance()).openLight();
        } else {
            lightFlag = true;
            // 关闪光灯
            CameraManager.getInstance(EIApplication.getInstance()).offLight();
        }

    }

    /**
     * 子类继承该类时可对返回的result做具体处理
     *
     * @param result 结果
     * @see [类、类#方法、类#成员]
     */
    protected void childhandleDecode(final Result result) {

    }

    /**
     * 子类继承该类可以对扫描超时处理
     *
     * @param activity act
     * @see [类、类#方法、类#成员]
     */
    protected void childTimeOutOpera(final Activity activity) {

    }

    /**
     * 获取handler
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @return handler
     * @see [类、类#方法、类#成员]
     */
    public Handler getmHandler() {
        return mHandler;
    }

    public int getScanTimeout() {
        return SCANTIMEOUT;
    }

    public void doOnCreate(Bundle savedInstanceState) {

    }

}