package com.read.scriptures.model;

import java.io.Serializable;
import java.util.List;

public class FontListModel implements Serializable {


    /**
     * total : 19
     * rows : [{"id":19,"font_name":"简约字体","pre_img":"https://sdacn.cn/font/PinYin_svg/FX简约字体.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":20,"font_name":"古风拼音","pre_img":"https://sdacn.cn/font/PinYin_svg/Q版古风拼音体.svg","category":"拼音字体","price":"1.00","pay_status":0},{"id":21,"font_name":"小帅字","pre_img":"https://sdacn.cn/font/PinYin_svg/丑帅字体.svg","category":"拼音字体","price":"2.00","pay_status":0},{"id":22,"font_name":"小世界","pre_img":"https://sdacn.cn/font/PinYin_svg/世界那么大.svg","category":"拼音字体","price":"2.08","pay_status":0},{"id":23,"font_name":"仿宋体","pre_img":"https://sdacn.cn/font/PinYin_svg/义启仿宋体.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":24,"font_name":"小领带","pre_img":"https://sdacn.cn/font/PinYin_svg/义启小领带.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":26,"font_name":"桃李满天下","pre_img":"https://sdacn.cn/font/PinYin_svg/义启桃李满天下.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":27,"font_name":"简黑体","pre_img":"https://sdacn.cn/font/PinYin_svg/义启简黑体.svg","category":"拼音字体","price":"1.00","pay_status":0},{"id":28,"font_name":"粗楷体","pre_img":"https://sdacn.cn/font/PinYin_svg/义启粗楷体.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":29,"font_name":"邻家小甜","pre_img":"https://sdacn.cn/font/PinYin_svg/义启邻家小甜甜.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":30,"font_name":"卡西莫多","pre_img":"https://sdacn.cn/font/PinYin_svg/卡西莫多.svg","category":"拼音字体","price":"0.30","pay_status":0},{"id":31,"font_name":"奇奇体","pre_img":"https://sdacn.cn/font/PinYin_svg/奇奇体.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":32,"font_name":"小地星","pre_img":"https://sdacn.cn/font/PinYin_svg/小地星字体.svg","category":"拼音字体","price":"0.33","pay_status":0},{"id":33,"font_name":"小宝贝儿","pre_img":"https://sdacn.cn/font/PinYin_svg/小宝贝儿拼音体.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":34,"font_name":"情书字体","pre_img":"https://sdacn.cn/font/PinYin_svg/情书字体.svg","category":"拼音字体","price":"0.00","pay_status":0},{"id":35,"font_name":"新宋拼音","pre_img":"https://sdacn.cn/font/PinYin_svg/新宋拼音体.svg","category":"拼音字体","price":"0.35","pay_status":0},{"id":36,"font_name":"爱自由(简体)","pre_img":"https://sdacn.cn/font/PinYin_svg/爱自由简体.svg","category":"拼音字体","price":"0.36","pay_status":0},{"id":37,"font_name":"老街","pre_img":"https://sdacn.cn/font/PinYin_svg/老街の字体.svg","category":"拼音字体","price":"0.37","pay_status":0},{"id":38,"font_name":"那么大","pre_img":"https://sdacn.cn/font/PinYin_svg/那么大体.svg","category":"拼音字体","price":"0.43","pay_status":0}]
     * pagess : 1
     */

    private int total;
    private int pagess;
    private List<RowsBean> rows;

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

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean {
        /**
         * id : 19
         * font_name : 简约字体
         * pre_img : https://sdacn.cn/font/PinYin_svg/FX简约字体.svg
         * category : 拼音字体
         * price : 0.00
         * pay_status : 0
         */

        private int id;
        private String font_name;
        private String size;
        private String pre_img;
        private String category;
        private String price;
        private int pay_status;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFont_name() {
            return font_name;
        }

        public void setFont_name(String font_name) {
            this.font_name = font_name;
        }

        public String getPre_img() {
            return pre_img;
        }

        public void setPre_img(String pre_img) {
            this.pre_img = pre_img;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getPay_status() {
            return pay_status;
        }

        public void setPay_status(int pay_status) {
            this.pay_status = pay_status;
        }
    }
}
