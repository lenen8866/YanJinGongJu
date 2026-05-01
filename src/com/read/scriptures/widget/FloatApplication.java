package com.read.scriptures.widget;

import android.view.WindowManager;

/**
 * 存储屏幕参数信息
 * <一句话功能简述>
 * <功能详细描述>
 * @author  Administrator
 * @version  [版本号, 2015-6-9]
 * @since  [产品/模块版本]
 */
public final class FloatApplication
{
    /**
     * 屏幕参数信息
     */
    private static WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
    
    /**
     * <默认构造函数>
     */
    private FloatApplication()
    {
    }
    
    /**
     * 获取屏幕参数信息
     * <一句话功能简述>
     * <功能详细描述>
     * @return WINDOWPARAMS 屏幕参数
     * @see [类、类#方法、类#成员]
     */
    public static synchronized WindowManager.LayoutParams getWindowParams()
    {
        return windowParams;
    }
    
}
