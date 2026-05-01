package com.read.scriptures.widget.camera.handler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.read.scriptures.EIUtils.EIApplication;
import com.read.scriptures.R;
import com.read.scriptures.widget.camera.CameraManager;
import com.read.scriptures.widget.camera.MipcaActivityCapture;

import java.util.List;


/**
 * 这个handler其实就是用来处理相机捕获的结果
 *
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
public final class CaptureActivityHandler extends Handler {

    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    /**
     * 捕获二维码的activity
     */
    private final MipcaActivityCapture activity;

    /**
     * 处理捕获到图片的解析线程（是一个Looper线程）
     */
    private final DecodeThread decodeThread;

    /**
     * 当前捕获的状态
     */
    private State state;

    /**
     * 枚举类定义状态
     *
     * @author s00223601
     * @version [版本号, 2015-10-8]
     * @since [产品/模块版本]
     */
    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    /**
     * 构造器
     *
     * @param activity      activity
     * @param decodeFormats decodeFormats
     * @param characterSet  characterSet
     * @see [类、类#方法、类#成员]
     */
    public CaptureActivityHandler(final MipcaActivityCapture activity, final List<BarcodeFormat> decodeFormats,
                                  final String characterSet) {
        this.activity = activity;

        //设置解码线程
        decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));

        //解码线程开始执行
        decodeThread.start();

        // 状态设置为成功
        state = State.SUCCESS;

        // 开始自己拍摄预览和解码
        CameraManager.getInstance(EIApplication.getInstance()).startPreview();
        restartPreviewAndDecode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(final Message message) {
        switch (message.what) {
            case R.id.auto_focus:
                if (state == State.PREVIEW) {
                    //处理自动聚焦
                    CameraManager.getInstance(EIApplication.getInstance()).requestAutoFocus(this, R.id.auto_focus);
                }
                break;

            case R.id.restart_preview:
                restartPreviewAndDecode();
                break;

            case R.id.decode_succeeded:
                state = State.SUCCESS;
                activity.handleDecode((Result) message.obj);
                break;

            case R.id.decode_failed:
                state = State.PREVIEW;
                CameraManager.getInstance(EIApplication.getInstance())
                        .requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                break;

            case R.id.return_scan_result:
                activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                activity.finish();
                break;

            case R.id.launch_product_query:
                final String url = (String) message.obj;
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                activity.startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 退出解码线程
     *
     * @see [类、类#方法、类#成员]
     */
    public void quitSynchronously() {
        state = State.DONE;

        //关闭相机
        CameraManager.getInstance(EIApplication.getInstance()).stopPreview();

        //关闭looper解码线程
        final Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            //这里join是用来等待解码线程的结束
            decodeThread.join();
        } catch (final InterruptedException e) {
        }

        //  一定要确保我们不会发送任何排队的消息
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    /**
     * 开始自己拍摄预览和解码]
     */
    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            //设置状态为捕获准备，其实就是设置解码handler和显示图像的surface
            state = State.PREVIEW;
            CameraManager.getInstance(EIApplication.getInstance()).requestPreviewFrame(decodeThread.getHandler(),
                    R.id.decode);

            //设置相机自动聚焦
            CameraManager.getInstance(EIApplication.getInstance()).requestAutoFocus(this, R.id.auto_focus);

            //清空view中的结果图片,就是让view再绘制那个中间的框框和中间线
            activity.drawViewfinder();
        }
    }

}
