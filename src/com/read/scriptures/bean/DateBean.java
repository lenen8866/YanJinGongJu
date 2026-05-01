package com.read.scriptures.bean;


import java.util.ArrayList;
import java.util.Objects;

public class DateBean {
    public int type;//0:上月，1:当月，2:下月
    public boolean isToday;//是否为今天

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateBean dateBean = (DateBean) o;
        return Objects.equals(timeStr, dateBean.timeStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStr);
    }

    public String timeStr;//20180202
    public String day;
    public String monthDay;//7月1日
    public ArrayList<CollectAudioBean.DataBean.RowsBean> data;
    public boolean isOnClick;
}
