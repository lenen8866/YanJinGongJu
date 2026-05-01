package com.read.scriptures.widget.camera.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.read.scriptures.EIUtils.EIApplication;
import com.read.scriptures.R;
import com.read.scriptures.widget.camera.CameraManager;
import com.read.scriptures.widget.camera.MipcaActivityCapture;
import com.read.scriptures.widget.camera.PlanarYUVLuminanceSource;

import java.util.Hashtable;

/**
 * 解码handler
 * 
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();

	/**
	 * 捕获activity
	 */
	private final MipcaActivityCapture activity;

	/**
	 * 定义解码器
	 */
	private final MultiFormatReader multiFormatReader;

	DecodeHandler(final MipcaActivityCapture activity, final Hashtable<DecodeHintType, Object> hints) {
		// 初始化一个解码器
		multiFormatReader = new MultiFormatReader();

		// 用在decodeThread中设置的格式设置解码器
		multiFormatReader.setHints(hints);

		this.activity = activity;
	}

	@Override
	public void handleMessage(final Message message) {
		if (message.what == R.id.decode) {
			// 处理发送过来的图片数据
			decode((byte[]) message.obj, message.arg1, message.arg2);
		}
		if (message.what == R.id.quit) {
			// 退出looper线程（解码线程）
			Looper.myLooper().quit();
		}
	}

	/**
	 * 解码了多长时间取景器的矩形内的数据和时间。为了提高效率， 从一个解码到下一个重复使用相同读者对象。
	 * 
	 * @param data
	 * @param width
	 * @param height
	 *            [参数说明]
	 */
	private void decode(final byte[] data, int width, int height) {
		Result rawResult = null;

		// modify here
		final byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				rotatedData[((x * height) + height) - y - 1] = data[x + (y * width)];
			}
		}
		final int tmp = width;
		width = height;
		height = tmp;

		PlanarYUVLuminanceSource source = null;
		BinaryBitmap bitmap;
		try {
			// 将相机获取的图片数据转化为binaryBitmap格式

			source = CameraManager.getInstance(EIApplication.getInstance()).buildLuminanceSource(rotatedData, width,
					height);
			bitmap = new BinaryBitmap(new HybridBinarizer(source));

			// 解析转化后的图片，得到结果
			rawResult = multiFormatReader.decodeWithState(bitmap);
		} catch (final NotFoundException re) {
		} catch (final OutOfMemoryError re) {
		} finally {
			multiFormatReader.reset();
			bitmap = null;
		}

		if (rawResult != null) {
			// 如果解析结果不为空，就是解析成功了，则发送成功消息，将结果放到message中
			final Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
			final Bundle bundle = new Bundle();
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
			message.setData(bundle);
			message.sendToTarget();
		} else {
			// 否则发送失败message
			final Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
			message.sendToTarget();
		}
	}

}
