package cn.cxnxs.pan.core.core.impl.baidu;

/**
 *  文件类型映射
 * @author potatomato
 */

public enum FileType {
    VIDEO(1),
    AUDIO(2),
    IMAGE(3),
    DOCUMENT(4),
    APPLICATION(5),
    OTHER(6),
    TORRENT(7),
    ;
    private final Integer code;

    FileType(Integer code) {
        this.code = code;
    }

    public static FileType getType(Integer code){
        for (FileType fileType:FileType.values()){
            if (fileType.code.equals(code)){
                return fileType;
            }
        }
        return null;
    }
}
