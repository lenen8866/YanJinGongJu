package com.read.scriptures.util.rsa;

import android.annotation.SuppressLint;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * @author 醉雪乱
 *
 */
public class RSA extends Coder {
    public static final String KEY_ALGORTHM = "RSA";//
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    public static final String PUBLIC_KEY = "RSAPublicKey";// 公钥
    public static final String PRIVATE_KEY = "RSAPrivateKey";// 私钥

    /** 存放公钥私钥 */
    public static Map<String, Object> keyMap;

    /**
     * 用私钥加密
     * 
     * @param data 加密数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    @SuppressLint("TrulyRandom") // 需要4.3以上版本
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        // 解密密钥
        byte[] keyBytes = decryptBASE64(key);
        // 取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 用公钥解密
     * 
     * @param data 加密数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 用公钥解密
     * 
     * @param data 加密数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKeyNoBase64(byte[] data, byte[] key) throws Exception {
        // 考虑到可能因为base64对密钥加密转换有影响，就不再对密钥进行base64加密解密操作了
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 用公钥加密
     * 
     * @param data 加密数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 用私钥解密
     * 
     * @param data 加密数据
     * 
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        // 对私钥解密
        byte[] keyBytes = decryptBASE64(key);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 用公钥加密
     * 
     * @param data 加密数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKeyNoBase64(byte[] data, byte[] key) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 用私钥解密
     * 
     * @param data 加密数据
     * 
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKeyNoBase64(byte[] data, byte[] key) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 初始化密钥
     * 
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORTHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);

        return keyMap;
    }

    /**
     * 取得公钥，并转化为String类型
     * 
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static byte[] getPublicKey() throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 取得私钥，并转化为String类型
     * 
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static byte[] getPrivateKey() throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encryptBASE64(key.getEncoded());
    }

}