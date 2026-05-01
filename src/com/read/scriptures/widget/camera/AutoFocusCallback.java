package com.read.scriptures.widget.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 设置自动聚焦
 * 
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
final class AutoFocusCallback implements Camera.AutoFocusCallback {

	private static final String TAG = AutoFocusCallback.class.getSimpleName();

	/**
	 * 设置自动聚焦的时间延迟，其实也就是时间间隔
	 */
	private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

	/**
	 * 处理自动聚焦的handler(CaptureActivityHandler)
	 */
	private Handler autoFocusHandler;

	/**
	 * 自动聚焦的发送信息
	 */
	private int autoFocusMessage;

	/**
	 * 初始化
	 * 
	 * @param autoFocusHd
	 * @param autoFocusMsg
	 * @see [类、类#方法、类#成员]
	 */
	void setHandler(final Handler autoFocusHd, final int autoFocusMsg) {
		autoFocusHandler = autoFocusHd;
		autoFocusMessage = autoFocusMsg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAutoFocus(final boolean success, final Camera camera) {
		if (autoFocusHandler != null) {
			final Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);

			// 延迟一定的时间发送自动聚焦的信息
			autoFocusHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);

			// 设置处理器为空,因为在消息处理的时候，会重新设置handler
			autoFocusHandler = null;
		} else {
			Log.i(TAG, "Got auto-focus callback, but no handler for it");
		}
	}

}
