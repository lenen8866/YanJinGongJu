package com.read.scriptures.util;

/**
 * Created by 醉雪乱 on 2018/3/20.
 */

public class PayUtil {
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2016042601336228";
//    public static final String APPID = "2088231876541991";//新的

    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    public static final String PID = "2016042601336228";
    /**
     * 支付宝账户登录授权业务：入参target_id值
     */
    public static final String TARGET_ID = "";
    /**
     * 商户私钥，pkcs8格式
     */
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJvrrFYtdYyTYF90z71Fmm4pYG4cglNUheZLw/UwTl+kYjnxZnWkn4EsqTOJkSfy/j56fZWNGKRK93rjJTFZKt/HpOY6Z8aq8BmzNHFv8OZTrFiwQ42LJxwOx5JLXLcf+JH56xs4I4v19BYFm2M0uUI/xfd7K4bRfe3+2/ZAoN1FAgMBAAECgYA8GpH5OlbY1KrOwSmfTWhlVL/eUlEOrENdbhFgv7lGdwiCS4LykH4H/l5PBpWU779/+BnW/2fjVR6nGU5qAnRa6veMKBK41OrjNgmpwSLEIHMMrdYCvlVZ+SavGIy02gNc3bklc803M7WzW+6tnEQmsOFSzS6/6uAsYZgIpjyItQJBAMzZDsV33nYTCqae6cFUIdidsR9Q8Gj8I7QvplcNClxa0yMlcp7LJ58Ld7/EUL1RiW5FIU3bwSEm6ThMaYfSaT8CQQDC2u3TNZhrdxCZrstvni77AoYJNDD3UqjXl6VFgxugVvRsaW7J23J3WL5b3qgTksd4j/LhOXe0vKTVwbKJeLR7AkBx2/fs5GqfQIg8Aro0/hlt+IB5wphOEk+aGJ4hZpsqqEURFQB3qL3WFh6yVWD3SbnUs9IGH65iSel5pGdH2jkrAkATJ0X9XuVOmvOMx7vDiv7/OW5HdVPi28Mw/f0G6i5LAoEgLvTu7p0/0f9UNHy8/86PsUakkOuNXoWtZU7RaIYFAkBR2OP08tq27CHpl+pSQwQGpzn7VjBuquv0484qzvM3M5s2jciM/+7iGEmUyLdS+QQocWAUUwlSRF30ZnL4TxmL";
    //新的
//    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJlhc9WeFedzi5SE2qBCTDWAlKVeICNmDsmZPf8kdAFSN1hVN7CnH2t5c07NxurZdkHaVs/8B8GOTwKBZAhHLTHrNO9Ul6hvSII6fYghM2YbDgCrQsvkQ7e5DkQGqMyiiwjkqOTjt54AxWuZiKMjVFZPPP1PR+3IOspey9W3t/HnAgMBAAECgYBH58vqJYaXTy/snKvx3aLkojcNn6bpcXsVP+Y0qt5HkEep/alyA6dTN0i7dPUAPJqYHMf+EWWBdDnOLuK6jsO8nA5wSy4UqhbNyK7SKuEuBccDdnNWAcbPw4wNSVYr6oFJQEppL5ELOihQSYAyO+N+9exnDIp4GinXnHVYDNa1oQJBAOB2MALf4+pCQJAmgzDbk7giBAm0L0G4AT/HStPCGqfpEWeUh2bTakSOtgd3XUdsQadbu0hVkm+nmQXGXLMOFzsCQQCu7oE/RGJMM/Oc+E7QpxIWOSIUpvAudDyA2C5QTUV8zkNVDeMWWlSBF4a6bMNgEy9GGRNs4y/QJKi2iNn0Ph1FAkEAqfJSH5sMoU1QPNQr+E4H5h47GG3IaeW7bGKkEPih3gflGZuneP8RMp8qyzRg97bLbr9RaU8A9HDctzac8ST2CQJAGUhfZcV8b4cSrp00xvNeqdeYFWQLUqt6EyUrjG+em2s0UBaxu4lSrVW5IziVBE3Bh2AYbqRnRK4HtFoTJjnUAQJBAJB20KUj89WrfSnPsnw/2oozqK3uTsWzP0w0C1DtmC4DMQn0VyWSAs3/NsBotMvwteshdORSJBtSpNiYaFL/qt8=";

    public static final int SDK_PAY_FLAG = 1;
}
