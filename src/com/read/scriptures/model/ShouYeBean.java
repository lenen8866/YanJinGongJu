package com.read.scriptures.model;

import com.read.scriptures.util.MD5Util;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.TimeUtils;

import java.net.URLDecoder;

/**
 * Created by Administrator on 2018/8/29.
 */

public class ShouYeBean {
    /**
     * id : 57
     * pic_name : 欢迎页面1
     * weigh : 1
     * link : https://read.sdacn.cn/
     * picture : null
     * showtime : 3
     * enddate : {"val":"2020-10-30 11:16:03","text":41}
     * pic_url : http://app.sdacn.cn/my/%E6%AC%A2%E8%BF%8E%E7%95%8C%E9%9D%A201.png
     * type : 2
     */

    private int id;
    private String pic_name;
    private int weigh;
    private String link;
    private String picture;
    private String showtime;
    private EnddateBean enddate;
    private String pic_url;
    private int type;

    private String locPicFilePath;//保存本地图片的位置信息

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPic_name() {
        return pic_name;
    }

    public void setPic_name(String pic_name) {
        this.pic_name = pic_name;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getShowtime() {
        return showtime;
    }

    public void setShowtime(String showtime) {
        this.showtime = showtime;
    }

    public EnddateBean getEnddate() {
        return enddate;
    }

    public void setEnddate(EnddateBean enddate) {
        this.enddate = enddate;
    }

    public String getPic_url() {
        if (pic_url != null){
            return URLDecoder.decode(pic_url);
        }
        return "";
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocPicFilePath() {
        return locPicFilePath;
    }

    public void setLocPicFilePath(String locPicFilePath) {
        this.locPicFilePath = locPicFilePath;
    }

    public String getPicFileName() {
        if (StringUtil.isNotEmpty(getPic_url())) {
            String pic = getPic_url();
            String fileName = pic.substring(pic.lastIndexOf("/")+1);
            if (fileName.indexOf(".") == -1) {
                fileName = fileName + ".jpg";
            }
            //拼上时间
            fileName =  TimeUtils.getTime(getEnddate().getVal())+"-" + fileName;
            String name = fileName.substring(0,fileName.indexOf("."));
            String type = fileName.substring(fileName.indexOf("."));
            fileName = MD5Util.getMD5String(name) + type;
            return fileName;
        }
        return "";
    }

    public static class EnddateBean {
        /**
         * val : 2020-10-30 11:16:03
         * text : 41
         */

        private String val;
        private int text;

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public int getText() {
            return text;
        }

        public void setText(int text) {
            this.text = text;
        }
    }
//
//
//    /**
//     * Pid : 22
//     * pic_name : 测试3
//     * link : https://sda777.com
//     * sort : 1
//     * picture : http://101.200.169.236/Public/Uploads/Welcome/5b93d465ebca3.png
//     * each : 2
//     * showtime : 3
//     * adddate : 1536413640
//     * enddate : 1539005640
//     */
//
//    private String Pid;
//    private String pic_name;
//    private String link;
//    private String sort;
//    private String picture;
//    private String each;
//    private String showtime;
//    private int adddate;
//    private int enddate;
//    /**
//     * stitle : 1
//     */
//
//    private String stitle;
//
//    public String getPid() {
//        return Pid;
//    }
//
//    public void setPid(String Pid) {
//        this.Pid = Pid;
//    }
//
//    public String getPic_name() {
//        return pic_name;
//    }
//
//    public void setPic_name(String pic_name) {
//        this.pic_name = pic_name;
//    }
//
//    public String getLink() {
//        return link;
//    }
//
//    public void setLink(String link) {
//        this.link = link;
//    }
//
//    public String getSort() {
//        return sort;
//    }
//
//    public void setSort(String sort) {
//        this.sort = sort;
//    }
//
//    public String getPicture() {
//        return picture;
//    }
//
//    public void setPicture(String picture) {
//        this.picture = picture;
//    }
//
//    public String getEach() {
//        return each;
//    }
//
//    public void setEach(String each) {
//        this.each = each;
//    }
//
//    public String getShowtime() {
//        return showtime;
//    }
//
//    public void setShowtime(String showtime) {
//        this.showtime = showtime;
//    }
//
//    public int getAdddate() {
//        return adddate;
//    }
//
//    public void setAdddate(int adddate) {
//        this.adddate = adddate;
//    }
//
//    public int getEnddate() {
//        return enddate;
//    }
//
//    public void setEnddate(int enddate) {
//        this.enddate = enddate;
//    }
//
//    public String getStitle() {
//        return stitle;
//    }
//
//    public void setStitle(String stitle) {
//        this.stitle = stitle;
//    }


}
