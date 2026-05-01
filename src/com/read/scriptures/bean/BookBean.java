package com.read.scriptures.bean;

import com.read.scriptures.util.StringUtil;

public class BookBean {
    /**
     * id : 49
     * status : 1
     * name : 测试
     * file : /uploads/20191124/825065d019ee198c9a161a90a6a917ea.png
     * create_time : 1574586167
     * type : 2
     * bookcode : 123123
     * size : NAN b
     * typename : {"id":2,"name":"02-百科"}
     */

    private int id;
    private int status;
    private String name;
    private String file;
    private String create_time;
    private String type;
    private String bookcode;
    private String size;
    private TypenameBean typename;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        if (StringUtil.isEmpty(file)){
          return "";
        }
        if (file.contains("http")) {
            return file;
        }else{
            return "http://"+file;
        }
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBookcode() {
        if (StringUtil.isEmpty(bookcode)){
            bookcode = "0";
        }
        return bookcode.trim();
    }

    public void setBookcode(String bookcode) {
        this.bookcode = bookcode;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public TypenameBean getTypename() {
        return typename;
    }

    public void setTypename(TypenameBean typename) {
        this.typename = typename;
    }

    public static class TypenameBean {
        /**
         * id : 2
         * name : 02-百科
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


//    /**
//     * id : 27
//     * file : http://sda777.com/appbooks/books/hudong.zip
//     * date : 1536832860
//     * type : 01-书库
//     * bookcode : 2018091307385
//     */
//
//    private String id;
//    private String file;
//    private int date;
//    private String type;
//    private String bookcode;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getFile() {
//        return file;
//    }
//
//    public void setFile(String file) {
//        this.file = file;
//    }
//
//    public int getDate() {
//        return date;
//    }
//
//    public void setDate(int date) {
//        this.date = date;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getBookcode() {
//        return bookcode;
//    }
//
//    public void setBookcode(String bookcode) {
//        this.bookcode = bookcode;
//    }
}
