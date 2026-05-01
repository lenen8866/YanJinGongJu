package com.read.scriptures.widget.camera;

/**
 * 构造PlanarYUVLuminanceSource使用的数据模型
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author  s00223601
 * @version  [版本号, 2015-10-23]
 * @since  [产品/模块版本]
 */
public class PlanarYUVConstrModel
{
    private byte[] yuvData;
    
    private int dataWidth;
    
    private int dataHeight;
    
    private int left;
    
    private int top;
    
    private int width;
    
    private int height;
    
    public byte[] getYuvData()
    {
        return yuvData;
    }
    
    public void setYuvData(final byte[] yuvData)
    {
        this.yuvData = yuvData;
    }
    
    public int getDataWidth()
    {
        return dataWidth;
    }
    
    public void setDataWidth(final int dataWidth)
    {
        this.dataWidth = dataWidth;
    }
    
    public int getDataHeight()
    {
        return dataHeight;
    }
    
    public void setDataHeight(final int dataHeight)
    {
        this.dataHeight = dataHeight;
    }
    
    public int getLeft()
    {
        return left;
    }
    
    public void setLeft(final int left)
    {
        this.left = left;
    }
    
    public int getTop()
    {
        return top;
    }
    
    public void setTop(final int top)
    {
        this.top = top;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public void setWidth(final int width)
    {
        this.width = width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setHeight(final int height)
    {
        this.height = height;
    }
    
}
