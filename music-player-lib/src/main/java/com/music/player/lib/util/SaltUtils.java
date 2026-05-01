package com.music.player.lib.util;

import android.text.TextUtils;

import java.security.PublicKey;

public class SaltUtils {

    public static String getUrl(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        try {
            PublicKey privateKey = RSAUtils.loadPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3u8s1BISjv1o4XSDZiq/EUCsu\n" +
                    "dkuIOkw4tvcnL/6OIs8ckPqzIYrmBP2cDDT7dat6/8aDtP3gZfnPpM0VnEaBc9u4\n" +
                    "Tm4cg4ooMHQouv+BngXrz6OowGp0A3eGa4w9GVEdY1QkS3ChDJbxoyyphTbzjh1U\n" +
                    "CmDWj6WUjbkaHSdWzQIDAQAB");
            byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(content), privateKey);
            String decryptStr = new String(decryptByte);
            return decryptStr;
        } catch (Exception e) {
        }
        return content;
    }
}
