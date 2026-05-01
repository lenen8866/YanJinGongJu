package com.read.scriptures.widget.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 相机配置
 * 
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
final class CameraConfigurationManager {

	private static final String TAG = CameraConfigurationManager.class.getSimpleName();

	private static final int TEN_DESIRED_ZOOM = 27;

	private static final int DESIRED_SHARPNESS = 30;

	private static final double TEN = 10.0;

	/**
	 * 用来切割尺寸的字符串，里面的尺寸是用 , 分开的
	 */
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");

	private final Context context;

	private Point screenResolution;

	private Point cameraResolution;

	private int previewFormat;

	private String previewFormatString;

	CameraConfigurationManager(final Context context) {
		this.context = context;
	}

	void initFromCameraParameters(final Camera camera) {
		final Camera.Parameters parameters = camera.getParameters();
		previewFormat = parameters.getPreviewFormat();
		previewFormatString = parameters.get("preview-format");
		final WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Point point = new Point();
		manager.getDefaultDisplay().getSize(point);
		screenResolution = new Point(point.x, point.y);
		final Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;
		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}
		cameraResolution = getCameraResolution(parameters, screenResolutionForCamera);
	}

	void setDesiredCameraParameters(final Camera camera) {
		final Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		setFlash(parameters);
		setZoom(parameters);
		setDisplayOrientation(camera, 90);
		camera.setParameters(parameters);
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	int getPreviewFormat() {
		return previewFormat;
	}

	String getPreviewFormatString() {
		return previewFormatString;
	}

	private static Point getCameraResolution(final Camera.Parameters parameters, final Point screenResolution) {

		String previewSizeValueString = parameters.get("preview-size-values");
		// saw this on Xperia
		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}

		Point cameraResolution = null;

		if (previewSizeValueString != null) {
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
		}

		if (cameraResolution == null) {
			// 确保cameraResolution是8的倍数，除8再乘8
			cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
		}

		return cameraResolution;
	}

	private static Point findBestPreviewSizeValue(final CharSequence previewSizeValueString,
			final Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
			/**
			 * 1280x720,864x480,640x480,480x320,352x288,320x240,176x144
			 */
			previewSize = previewSize.trim();
			final int dimPosition = previewSize.indexOf('x');
			if (dimPosition < 0) {
				Log.i(TAG, "Bad preview-size");
				continue;
			}

			int newX;
			int newY;
			try {
				/**
				 * 根据x位置得到尺寸
				 */
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch (final NumberFormatException nfe) {
				Log.i(TAG, "Bad preview-size NumberFormatException");
				continue;
			}

			final int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);

			/**
			 * 如果相机参数中获得的尺寸与屏幕尺寸相等，那么这个尺寸就是合适的尺寸
			 */
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			}

			/**
			 * 否则差距最小的那个就是最合适的尺寸
			 */
			else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}

		if ((bestX > 0) && (bestY > 0)) {
			return new Point(bestX, bestY);
		}
		return null;
	}

	/**
	 * 如果mot-zoom-values为空，这个方法没有用到
	 */
	private static int findBestMotZoomValue(final CharSequence stringValues, final int tenDesiredZoom) {
		int tenBestValue = 0;
		for (String stringValue : COMMA_PATTERN.split(stringValues)) {
			stringValue = stringValue.trim();
			double value;
			try {
				value = Double.parseDouble(stringValue);
			} catch (final NumberFormatException nfe) {
				return tenDesiredZoom;
			}
			final int tenValue = (int) (10.0 * value);
			if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
				tenBestValue = tenValue;
			}
		}
		return tenBestValue;
	}

	private void setFlash(final Camera.Parameters parameters) {
		if (Build.MODEL.contains("Behold II") && (CameraManager.SDK_INT == 3)) {
			parameters.set("flash-value", 1);
		} else {
			parameters.set("flash-value", 2);
		}
		parameters.set("flash-mode", "off");
	}

	private void setZoom(final Camera.Parameters parameters) {

		final String zoomSupportedString = parameters.get("zoom-supported");
		if ((zoomSupportedString != null) && !Boolean.parseBoolean(zoomSupportedString)) {
			return;
		}

		int tenDesiredZoom = TEN_DESIRED_ZOOM;

		final String maxZoomString = parameters.get("max-zoom");
		tenDesiredZoom = compareTenZoom(maxZoomString, tenDesiredZoom);

		final String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
		if (takingPictureZoomMaxString != null) {
			try {
				final int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (final NumberFormatException nfe) {
				Log.i(TAG, "Bad taking-picture-zoom-max");
			}
		}

		final String motZoomValuesString = parameters.get("mot-zoom-values");
		if (motZoomValuesString != null) {
			tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
		}

		final String motZoomStepString = parameters.get("mot-zoom-step");
		if (motZoomStepString != null) {
			try {
				final double motZoomStep = Double.parseDouble(motZoomStepString.trim());
				final int tenZoomStep = (int) (10.0 * motZoomStep);
				if (tenZoomStep > 1) {
					tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
				}
			} catch (final NumberFormatException nfe) {
				Log.i(TAG, "NumberFormatException error");
			}
		}

		setParameters(maxZoomString, motZoomValuesString, takingPictureZoomMaxString, parameters, tenDesiredZoom);
	}

	private int compareTenZoom(final String maxZoomString, int tenDesiredZoom) {
		if (maxZoomString != null) {
			try {
				final int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (final NumberFormatException nfe) {
				Log.i(TAG, "Bad max-zoom: " + maxZoomString);
			}
		}
		return tenDesiredZoom;
	}

	private void setParameters(final String maxZoomString, final String motZoomValuesString,
			final String takingPictureZoomMaxString, final Camera.Parameters parameters, final int tenDesiredZoom) {
		if ((maxZoomString != null) || (motZoomValuesString != null)) {
			parameters.set("zoom", String.valueOf(tenDesiredZoom / TEN));
		}

		if (takingPictureZoomMaxString != null) {
			parameters.set("taking-picture-zoom", tenDesiredZoom);
		}
	}

	public static int getDesiredSharpness() {
		return DESIRED_SHARPNESS;
	}

	/**
	 * compatible 1.6
	 * 
	 * @param camera
	 * @param angle
	 */
	protected void setDisplayOrientation(final Camera camera, final int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null) {
				downPolymorphic.invoke(camera, new Object[] { angle });
			}
		} catch (final Exception e1) {
			Log.i(TAG, "setDisplayOrientation failed.");
		}
	}

}
