package com.read.scriptures.widget.camera.handler;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.read.scriptures.widget.camera.MipcaActivityCapture;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 该线程将影像解码的所有繁重的任务。
 *
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
final class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";

    /**
     * 捕获activity
     */
    private final MipcaActivityCapture activity;

    /**
     * 定义一个容器，存放解码格式，字符集，结果回调类
     */
    private final Hashtable<DecodeHintType, Object> hints;

    /**
     * 定义解码handler
     */
    private Handler handler;

    /**
     * 线程安全的数数装置
     */
    private final CountDownLatch handlerInitLatch;

    DecodeThread(final MipcaActivityCapture activity, List<BarcodeFormat> decodeFormats, final String characterSet,
                 final ResultPointCallback resultPointCallback) {

        this.activity = activity;

        //设置计数为1
        handlerInitLatch = new CountDownLatch(1);

        //定义容器
        hints = new Hashtable<DecodeHintType, Object>(3);

        //设置解码格式，然后放到容器中
        if ((decodeFormats == null) || decodeFormats.isEmpty()) {
            decodeFormats = new ArrayList<BarcodeFormat>();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS); //一维码
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);// 二维码
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        //如果字符集不为空，放到容器中
        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        /**
         * 将结果回调函数放到容器中,这里的hints会被设置到解码器中，当解码器解析图片的时候会将可能是二维码的点返回到这个回调函数中
         * 就是foundPossibleResultPoint(ResultPoint point)这个方法，所以我们只要在这个方法中调用View绘制
         * 的方法，将这些可能点绘制出来，就是我们扫描看到的绿点
         */
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            //等待，当计数为0的时候继续往下执行
            handlerInitLatch.await();
        } catch (final InterruptedException ie) {
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();

        //定义解码handler
        handler = new DecodeHandler(activity, hints);

        //计算减1
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
