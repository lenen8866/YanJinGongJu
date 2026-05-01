package com.read.scriptures.bean;

public class FreeBean {

    /**
     * open : true
     * begin_dt : 2018-10-01 00:00:00
     * end_dt : 2018-10-31 23:59:59
     * days : 24
     */

    private boolean open;
    private long end_time;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
}
