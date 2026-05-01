package com.read.scriptures.model;

import com.read.scriptures.bean.BookBean;

import java.util.List;

public class BookResult {

    /**
     * total : 4
     * rows : []
     * pagess : 1
     */

    private int total;
    private int pagess;
    private List<BookBean> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPagess() {
        return pagess;
    }

    public void setPagess(int pagess) {
        this.pagess = pagess;
    }

    public List<BookBean> getRows() {
        return rows;
    }

    public void setRows(List<BookBean> rows) {
        this.rows = rows;
    }
}
