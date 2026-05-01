package com.read.scriptures.widget.camera.handler;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.read.scriptures.widget.camera.ViewfinderView;

/**
 * 在扫描框设置一些点点(貌似已经把那些点去掉了)
 * @author  s00223601
 * @version  [版本号, 2015-10-8]
 * @since  [产品/模块版本]
 */
public final class ViewfinderResultPointCallback implements ResultPointCallback
{

    private final ViewfinderView viewfinderView;

    /**
     * 构造器
     * 相机依赖的view
     * @param viewfinderView viewfinderView
     * @see [类、类#方法、类#成员]
     */
    public ViewfinderResultPointCallback(final ViewfinderView viewfinderView)
    {
        this.viewfinderView = viewfinderView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void foundPossibleResultPoint(final ResultPoint point)
    {
        viewfinderView.addPossibleResultPoint(point);
    }

}
