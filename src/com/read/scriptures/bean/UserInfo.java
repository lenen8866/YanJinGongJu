package com.read.scriptures.bean;

import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.TimeUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description: 用户信息
 */
public class UserInfo implements Serializable {

    @Override
    public String toString() {
        return "UserInfo{" +
                "wxAccessToken='" + wxAccessToken + '\'' +
                ", wxOpenId='" + wxOpenId + '\'' +
                ", wxRefreshToken='" + wxRefreshToken + '\'' +
                ", token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", sequence='" + sequence + '\'' +
                ", create_time=" + create_time +
                ", level=" + level +
                '}';
    }

    public static final String VIP_NORMAL = "v1";
    public static final String VIP_HIGH = "v2";
    public static final String VIP_SUPPER = "v3";

    //微信授权access_token
    private String wxAccessToken;
    //微信授权openId
    private String wxOpenId;
    //微信授权刷新
    private String wxRefreshToken;
    //用户token（后台给的）
    private String token;

    private String username;

    private String sequence;

    private long create_time;

    /**
     * 用户会有多个会员
     */
    private List<UserNetInfo.LevelBean> level;

    public String getWxAccessToken() {
        return wxAccessToken;
    }

    public void setWxAccessToken(String wxAccessToken) {
        this.wxAccessToken = wxAccessToken;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public String getToken() {
        return token == null ? "" : token;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UserNetInfo.LevelBean> getLevel() {
        return level;
    }

    public void setLevel(List<UserNetInfo.LevelBean> level) {
        this.level = level;
    }

    public String getWxRefreshToken() {
        return wxRefreshToken;
    }

    public void setWxRefreshToken(String wxRefreshToken) {
        this.wxRefreshToken = wxRefreshToken;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    /**
     * 判断会员功能是否能用（高级、超级只要有一个没过期都能用）
     *
     * @return
     */
    public boolean levelAvailable(String vipName) {
        if (getLevel() == null) {
            return false;
        }
        long maxExpire = 0;
        for (UserNetInfo.LevelBean levelBean : getLevel()) {
            String level = levelBean.getVal();
            if (vipName.compareTo(level) <= 0) {
                long expire = levelBean.getExpire();
                if (maxExpire < expire) {
                    maxExpire = expire;
                }
            }
        }

        if (maxExpire < TimeUtils.getNowStamp()) {
            //已过期
            //判断免费是否过期
            if (isFree()) {
                //没过期可以免费使用
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置中查看过期时间的字符串
     *
     * @return
     */
    public String getLevelExpireTimeStr() {
        StringBuilder sbExpire = new StringBuilder("");
        if (getLevel() != null && getLevel().size() > 0) {
            for (UserNetInfo.LevelBean levelBean : getLevel()) {
                if ("v0".equals(levelBean.getVal()) || "普通用户".equals(levelBean.getText())) {
                    continue;
                }
                String levenName = levelBean.getText();
                long expire = levelBean.getExpire();
                String timeStr = TimeUtils.timeStamp2DateNoSecond1(expire);
                if (TimeUtils.getDistanceDays(TimeUtils.getNowStamp(), expire) >= (7 * 365)) {
                    //大于，等于7年
//                timeStr = "<b><font color=\"#4caf50\">" + timeStr + "</font></b>";
                    sbExpire.append("<b><font color=\"#4caf50\">您好，您是我们的vip用户</font></b><br/>");
                } else {
                    boolean isLessThanThree = TimeUtils.getDistanceDays(TimeUtils.getNowStamp(), expire) <= 3;

                    if (isLessThanThree) {
                        //剩余小于等于3天
                        timeStr = "<font color=\"#ff0000\">" + timeStr + "</font>";
                    }
                    sbExpire.append(levenName)
                            .append("：截止 ")
                            .append(timeStr)
                            .append("<br/>");
                }
            }
        }
        String expireTimeStr = sbExpire.toString();
        if (expireTimeStr.endsWith("<br/>")) {
            expireTimeStr = expireTimeStr.substring(0, expireTimeStr.lastIndexOf("<br/>"));
        }

        if (StringUtil.isEmpty(expireTimeStr)) {//用户没有购买过任何会员，或都过期
            //判断是否开启免费
            if (isFree()) {//免费开启
                //获取免费到期时间
                long freeActiveTimeEnd = PreferenceConfig.getFreeActiveTimeEnd(HuDongApplication.getInstance());
                //三个月时间戳
                long threeMonthTime = 90 * 24 * 60 * 60;
                //获取今天的时间戳
                long now = TimeUtils.getNowStamp();
                //三天时间戳
                long threeDayTime = 3 * 24 * 60 * 60;
                if (freeActiveTimeEnd - now > threeDayTime) {
                    expireTimeStr = "本软件暂时免费！";
                } else {
                    expireTimeStr = "<font color=\"#ff0000\">" +  "试用到期时间:" + TimeUtils.timeStamp2DateC(freeActiveTimeEnd) + "</font>";;
                }
            } else {
                expireTimeStr = "您尚未激活或激活天数过期，请续费！";
            }
        }
        return expireTimeStr;
    }


    public long getMaturity(String levelType) {
        if (levelType == null) {
            levelType = "";
        }
        long expire = 0;
        for (UserNetInfo.LevelBean levelBean : getLevel()) {
            if (levelType.equals(levelBean.getVal())) {
                expire = levelBean.getExpire();
            }
        }
        return expire;
    }

    /**
     * 是否开启免费,true：开启，false: 关闭
     *
     * @return
     */
    public boolean isFree() {
        boolean isFree = PreferenceConfig.getFreeActiveTimeOpen(HuDongApplication.getInstance());
        if (isFree) {
            //判读是否过期
            long freeActiveTimeEnd = PreferenceConfig.getFreeActiveTimeEnd(HuDongApplication.getInstance());
            if (freeActiveTimeEnd > TimeUtils.getNowStamp()) {
                isFree = true;
            } else {
                isFree = false;
            }
        }
        return isFree;

    }
}
