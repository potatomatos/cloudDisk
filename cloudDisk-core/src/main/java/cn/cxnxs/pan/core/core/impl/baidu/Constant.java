package cn.cxnxs.pan.core.core.impl.baidu;

import java.io.File;

/**
 * @Description: 百度网盘基本常量信息
 */
public interface Constant {

    String APP_NAME = "云存储";

    String APP_PATH = "/apps/" + APP_NAME + "/";

    String SEPARATE_PATH = File.separator + "tmp" + File.separator + APP_NAME + File.separator;

    /**
     * 普通用户单个分片大小固定为4MB（文件大小如果小于4MB，无需切片，直接上传即可），单文件总大小上限为4G
     * 普通会员用户单个分片大小上限为16MB，单文件总大小上限为10G
     * 超级会员用户单个分片大小上限为32MB，单文件总大小上限为20G
     */
    Integer UNIT = 32;

    /**
     * 操作文件 copy, mover, rename, delete
     */
    String FILE_MANAGER_URL = "https://pan.baidu.com/rest/2.0/xpan/file";

    /**
     * 分片上传
     */
    String SLICING_UPLOAD_FILE_URL = "https://d.pcs.baidu.com/rest/2.0/pcs/superfile2";

    /**
     * 文件信息
     */
    String FILE_INFO_URL = "https://pan.baidu.com/rest/2.0/xpan/multimedia";
}
