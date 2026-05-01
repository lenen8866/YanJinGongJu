package com.read.scriptures.widget.camera.handler;

import android.content.Intent;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;
import com.read.scriptures.widget.camera.Intents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 解码格式管理器
 *
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
final class DecodeFormatManager {

    static final List<BarcodeFormat> PRODUCT_FORMATS;

    static final List<BarcodeFormat> ONE_D_FORMATS;

    static final List<BarcodeFormat> QR_CODE_FORMATS;

    static final List<BarcodeFormat> DATA_MATRIX_FORMATS;

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    static {
        PRODUCT_FORMATS = new ArrayList<BarcodeFormat>(6);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_A); // UPC标准码(通用商品)
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_E); // UPC缩短码(商品短码)
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
        PRODUCT_FORMATS.add(BarcodeFormat.RSS_14);
        PRODUCT_FORMATS.add(BarcodeFormat.RSS_EXPANDED);

        ONE_D_FORMATS = new ArrayList<BarcodeFormat>(PRODUCT_FORMATS.size() + 5);
        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
        ONE_D_FORMATS.add(BarcodeFormat.ITF);
        ONE_D_FORMATS.add(BarcodeFormat.CODABAR);

        QR_CODE_FORMATS = new ArrayList<BarcodeFormat>(1);
        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE); //QR_CODE即二维码

        DATA_MATRIX_FORMATS = new ArrayList<BarcodeFormat>(1);
        DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX); //也属于一种二维码
    }

    private DecodeFormatManager() {
    }

    static List<BarcodeFormat> parseDecodeFormats(final Intent intent) {
        if (null == intent) {
            return null;
        }
        List<String> scanFormats = null;
        String scanModeString = null;
        try {
            final String scanFormatsString = intent.getStringExtra(Intents.Scan.SCAN_FORMATS);
            if (scanFormatsString != null) {
                scanFormats = Arrays.asList(COMMA_PATTERN.split(scanFormatsString));
            }
            scanModeString = intent.getStringExtra(Intents.Scan.MODE);
        } catch (final NullPointerException e) {
        }
        return parseDecodeFormats(scanFormats, scanModeString);
    }

    static List<BarcodeFormat> parseDecodeFormats(final Uri inputUri) {
        List<String> formats = inputUri.getQueryParameters(Intents.Scan.SCAN_FORMATS);
        if ((formats != null) && (formats.size() == 1) && (formats.get(0) != null)) {
            formats = Arrays.asList(COMMA_PATTERN.split(formats.get(0)));
        }
        return parseDecodeFormats(formats, inputUri.getQueryParameter(Intents.Scan.MODE));
    }

    private static List<BarcodeFormat> parseDecodeFormats(final Iterable<String> scanFormats, final String decodeMode) {
        if (scanFormats != null) {
            final List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
            try {
                for (final String format : scanFormats) {
                    formats.add(BarcodeFormat.valueOf(format));
                }
                return formats;
            } catch (final IllegalArgumentException iae) {
            }
        }
        if (decodeMode != null) {
            if (Intents.Scan.PRODUCT_MODE.equals(decodeMode)) {
                return PRODUCT_FORMATS;
            }
            if (Intents.Scan.QR_CODE_MODE.equals(decodeMode)) {
                return QR_CODE_FORMATS;
            }
            if (Intents.Scan.DATA_MATRIX_MODE.equals(decodeMode)) {
                return DATA_MATRIX_FORMATS;
            }
            if (Intents.Scan.ONE_D_MODE.equals(decodeMode)) {
                return ONE_D_FORMATS;
            }
        }
        return null;
    }

}
