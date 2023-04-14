package cn.cxnxs.pan.core.param.baidu;

public enum BaiduErrorCode {
    ;

    private String errno;

    private String errmsg;

    private String desc;

    BaiduErrorCode(String errno, String errmsg, String desc) {
        this.errno = errno;
        this.errmsg = errmsg;
        this.desc = desc;
    }
}
