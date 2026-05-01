package com.read.scriptures.widget.camera;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;


/**
 * 解码从android扫描到的图片位图
 * @author  s00223601
 * @version  [版本号, 2015-10-8]
 * @since  [产品/模块版本]
 */
public final class PlanarYUVLuminanceSource extends LuminanceSource
{
    private final byte[] yuvData;
    
    private final int dataWidth;
    
    private final int dataHeight;
    
    private final int left;
    
    private final int top;
    
    /**
     * 构造函数
     * <一句话功能简述>
     * <功能详细描述>
     * @param model 构造模型
     * @see [类、类#方法、类#成员]
     */
    public PlanarYUVLuminanceSource(final PlanarYUVConstrModel model)
    {
        super(model.getWidth(), model.getHeight());
        
        final byte[] yuData = model.getYuvData();
        
        final int datWidth = model.getDataWidth();
        
        final int datHeight = model.getDataHeight();
        
        final int leftVal = model.getLeft();
        
        final int topVal = model.getTop();
        
        final int width = model.getWidth();
        
        final int height = model.getHeight();
        
        if (((leftVal + width) > datWidth) || ((topVal + height) > datHeight))
        {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
        
        yuvData = yuData;
        dataWidth = datWidth;
        dataHeight = datHeight;
        left = leftVal;
        top = topVal;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getRow(final int y, byte[] row)
    {
        if ((y < 0) || (y >= getHeight()))
        {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        final int width = getWidth();
        if ((row == null) || (row.length < width))
        {
            row = new byte[width];
        }
        final int offset = ((y + top) * dataWidth) + left;
        System.arraycopy(yuvData, offset, row, 0, width);
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatrix()
    {
        final int width = getWidth();
        final int height = getHeight();
        
        if ((width == dataWidth) && (height == dataHeight))
        {
            return yuvData;
        }
        
        final int area = width * height;
        final byte[] matrix = new byte[area];
        int inputOffset = (top * dataWidth) + left;
        
        if (width == dataWidth)
        {
            System.arraycopy(yuvData, inputOffset, matrix, 0, area);
            return matrix;
        }
        
        final byte[] yuv = yuvData;
        for (int y = 0; y < height; y++)
        {
            final int outputOffset = y * width;
            System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
            inputOffset += dataWidth;
        }
        return matrix;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCropSupported()
    {
        return true;
    }
    
    public int getDataWidth()
    {
        return dataWidth;
    }
    
    public int getDataHeight()
    {
        return dataHeight;
    }
    
    /**
     * 返回剪切后的灰度图片
     * @return 图形
     * @see [类、类#方法、类#成员]
     */
    public Bitmap renderCroppedGreyscaleBitmap()
    {
        final int width = getWidth();
        final int height = getHeight();
        final int[] pixels = new int[width * height];
        final byte[] yuv = yuvData;
        int inputOffset = (top * dataWidth) + left;
        
        for (int y = 0; y < height; y++)
        {
            final int outputOffset = y * width;
            for (int x = 0; x < width; x++)
            {
                final int grey = yuv[inputOffset + x] & 0xff;
                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
            }
            inputOffset += dataWidth;
        }
        
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
