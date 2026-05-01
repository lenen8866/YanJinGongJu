package com.read.scriptures.widget.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 *
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author  s00223601
 * @version  [版本号, 2015-10-8]
 * @since  [产品/模块版本]
 */
final class PreviewCallback implements Camera.PreviewCallback
{
    
    private static final String TAG = PreviewCallback.class.getSimpleName();
    
    /**
     * 相机配置管理
     */
    private final CameraConfigurationManager configManager;
    
    /**
     * 是否是一次性照相
     */
    private final boolean useOneShotPreviewCallback;
    
    /**
     * 解码的handler
     */
    private Handler previewHandler;
    
    /**
     * 解码信号
     */
    private int previewMessage;
    
    PreviewCallback(final CameraConfigurationManager configManager, final boolean useOneShotPreviewCallback)
    {
        this.configManager = configManager;
        this.useOneShotPreviewCallback = useOneShotPreviewCallback;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param previewHdl
     * @param previewMsg
     * @see [类、类#方法、类#成员]
     */
    void setHandler(final Handler previewHdl, final int previewMsg)
    {
        previewHandler = previewHdl;
        previewMessage = previewMsg;
    }
    
    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera)
    {
        final Point cameraResolution = configManager.getCameraResolution();
        if (!useOneShotPreviewCallback)
        {
            camera.setPreviewCallback(null);
        }
        if (previewHandler != null)
        {
            // 这个handler就是来自于DecodeThread中run（）中的handler
            final Message message =
                previewHandler.obtainMessage(previewMessage, cameraResolution.x, cameraResolution.y, data);
            message.sendToTarget();
            previewHandler = null;
        }
        else
        {
            Log.i(TAG, "Got preview callback, but no handler for it");
        }
    }
    
}
