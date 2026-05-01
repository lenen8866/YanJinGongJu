package com.read.scriptures.widget.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 *  CameraManager
 * @author  s00223601
 * @version  [版本号, 2015-10-8]
 * @since  [产品/模块版本]
 */
public final class CameraManager
{
    static final int SDK_INT;

    private static final String TAG = CameraManager.class.getSimpleName();

    /**
     * 定义一个静态的cameraManager
     */
    private static CameraManager cameraMgr = null;

    private Parameters parameter;

    /**
     * 获取构建版本
     */
    static
    {
        int sdkInt;
        try
        {
            sdkInt = Build.VERSION.SDK_INT;
        }
        catch (final NumberFormatException nfe)
        {
            sdkInt = 10000;
        }
        SDK_INT = sdkInt;
    }

    private final Context context;

    /**
     * 定义相机配置管理器
     */
    private final CameraConfigurationManager configManager;

    /**
     * 定义相机
     */
    private Camera camera;

    private Rect framingRect;

    private Rect framingRectInPreview;

    private boolean initialized;

    private boolean previewing;

    /**
     * 定义是否是一次性回调，如果为true的话就是拍一下就停了，再想拍要重新定义拍照的surface
     */
    private final boolean useOneShotPreviewCallback;

    /**
     * 预览帧被送到这里，我们传递给注册的处理程序。确保清除处理程序，以便它只会收到一个消息。
     */
    private final PreviewCallback previewCallback;

    /**
     * 自动对焦回调到达这里，并分派给提出要求的处理程序。
     */
    private final AutoFocusCallback autoFocusCallback;

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param context 上下文
     * @see [类、类#方法、类#成员]
     */
    private CameraManager(final Context context)
    {

        this.context = context;
        configManager = new CameraConfigurationManager(context);

        useOneShotPreviewCallback = Build.VERSION.SDK_INT > 3; // 3 = Cupcake

        previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
        autoFocusCallback = new AutoFocusCallback();
    }

    /**
     * 获取CameraManager单一实例。
     * @param context 上下文
     * @return CameraManager单一实例。
     * @see [类、类#方法、类#成员]
     */
    public static synchronized CameraManager getInstance(final Context context)
    {
        if (cameraMgr == null)
        {
            cameraMgr = new CameraManager(context);
        }
        return cameraMgr;
    }

    /**
     * 打开摄像头驱动程序，并初始化硬件参数。
     * @param holder viewHolder
     * @see [类、类#方法、类#成员]
     */
    public void openDriver(final SurfaceHolder holder)
    {
        if (camera == null)
        {
            camera = Camera.open();
            if (camera == null)
            {
                return;
            }
            try
            {
                camera.setPreviewDisplay(holder);
            }
            catch (final IOException e)
            {
               Log.i(TAG, " camera.setPreviewDisplay error.");
            }

            if (!initialized)
            {
                initialized = true;
                configManager.initFromCameraParameters(camera);
            }
            configManager.setDesiredCameraParameters(camera);

            FlashlightManager.enableFlashlight();
        }
    }

    /**
     * 关闭摄像头驱动程序
     * @see [类、类#方法、类#成员]
     */
    public void closeDriver()
    {
        if (camera != null)
        {
            FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
        }
    }

    /**
     * 相机准备
     * @see [类、类#方法、类#成员]
     */
    public void startPreview()
    {
        if ((camera != null) && !previewing)
        {
            camera.startPreview();
            previewing = true;
        }
    }

    /**
     * 相机暂停
     * @see [类、类#方法、类#成员]
     */
    public void stopPreview()
    {
        if ((camera != null) && previewing)
        {
            if (!useOneShotPreviewCallback)
            {
                camera.setPreviewCallback(null);
            }
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param handler hander
     * @param message 消息
     * @see [类、类#方法、类#成员]
     */
    public void requestPreviewFrame(final Handler handler, final int message)
    {
        if ((camera != null) && previewing)
        {
            previewCallback.setHandler(handler, message);
            if (useOneShotPreviewCallback)
            {
                camera.setOneShotPreviewCallback(previewCallback);
            }
            else
            {
                camera.setPreviewCallback(previewCallback);
            }
        }
    }

    /**
     * 询问照相机硬件来执行自动聚焦。
     * @param handler hander
     * @param message 消息
     * @see [类、类#方法、类#成员]
     */
    public void requestAutoFocus(final Handler handler, final int message)
    {
        if ((camera != null) && previewing)
        {
            //重新设置handler
            autoFocusCallback.setHandler(handler, message);

            //让相机自动聚焦
            camera.autoFocus(autoFocusCallback);
        }
    }

    /**
     * 设置扫描框的大小
     * 框大则取图就大，会多占一点内存，对应的扫描的时候快得多
     * @return framingRect
     * @see [类、类#方法、类#成员]
     */
    public Rect getFramingRect()
    {
        // 获得屏幕的大小
        final Point screenResolution = configManager.getScreenResolution();
        if (framingRect == null)
        {
            if (camera == null)
            {
                return null;
            }

            //扫描框的高宽分别是屏幕的2/5、4/5
            final int height = (screenResolution.y * 2) / 5;
            final int width = (screenResolution.x * 4) / 5;

            //左边的开始位置为屏幕宽减去矩形宽除2，上边开始位置同宽设置一样
            final int leftOffset = (screenResolution.x - width) / 2;
            final int topOffset = (screenResolution.y - height) / 2;

            //根据上面计算好的参数设置中间的矩形
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            Log.i(TAG, "Calculated framing rect: " + framingRect);
        }
        return framingRect;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return framingRectInPreview
     * @see [类、类#方法、类#成员]
     */
    public Rect getFramingRectInPreview()
    {
        final Rect rec = getFramingRect();
        if ((framingRectInPreview == null) && (rec != null))
        {
            final Rect rect = new Rect(rec);
            final Point cameraResolution = configManager.getCameraResolution();
            final Point screenResolution = configManager.getScreenResolution();
            rect.left = (rect.left * cameraResolution.y) / screenResolution.x;
            rect.right = (rect.right * cameraResolution.y) / screenResolution.x;
            rect.top = (rect.top * cameraResolution.x) / screenResolution.y;
            rect.bottom = (rect.bottom * cameraResolution.x) / screenResolution.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param data data
     * @param width width
     * @param height height
     * @return PlanarYUVLuminanceSource
     * @see [类、类#方法、类#成员]
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(final byte[] data, final int width, final int height)
    {
        final Rect rect = getFramingRectInPreview();
        final int previewFormat = configManager.getPreviewFormat();
        final String previewFormatString = configManager.getPreviewFormatString();
        final PlanarYUVConstrModel model = new PlanarYUVConstrModel();
        model.setYuvData(data);
        model.setDataWidth(width);
        model.setDataHeight(height);
        model.setLeft(rect.left);
        model.setTop(rect.top);
        model.setWidth(rect.width());
        model.setHeight(rect.height());
        switch (previewFormat)
        {
            case ImageFormat.NV21:
            case ImageFormat.NV16:
                return new PlanarYUVLuminanceSource(model);
            default:
                if ("yuv420p".equals(previewFormatString))
                {
                    return new PlanarYUVLuminanceSource(model);
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " + previewFormat + '/' + previewFormatString);
    }

    /**
     * 开启闪光灯
     */
    public void openLight()
    {
        if (camera != null)
        {
            parameter = camera.getParameters();
            parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
        }
    }

    /**
     * 关闭闪光灯
     */
    public void offLight()
    {
        if (camera != null)
        {
            parameter = camera.getParameters();
            parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
        }
    }

    public Context getContext()
    {
        return context;
    }

}
