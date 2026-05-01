package com.read.scriptures.manager.alispeech.optimize;

/**
 * 操作实体
 */
public class OperationQueue{
    public static final int TYPE_SPEAK  = 1;//播放语句
    public static final int  TYPE_PAUSE = 2;//暂停
    public static final int  TYPE_STOP = 3;//停止
    public static final int  TYPE_RESUME = 4;//恢复
    public static final int TYPE_DESTORY = 5;//销毁

    int id;
    int type;
    String msg;

    public OperationQueue(int id, int type, String msg) {
        this.id = id;
        this.type = type;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "OperationQueue{" +
                "id=" + id +
                ", type=" + type +"（"+getTypeStr()+"）"+
                ", msg='" + msg + '\'' +
                '}';
    }

    private String getTypeStr(){
        switch (getType()){
            case TYPE_SPEAK:
                return "播放";
            case TYPE_PAUSE:
                return "暂停";
            case TYPE_STOP:
                return "停止";
            case TYPE_RESUME:
                return "恢复";
            case TYPE_DESTORY:
                return "销毁";
            default:
                return "其他";
        }
    }
}
