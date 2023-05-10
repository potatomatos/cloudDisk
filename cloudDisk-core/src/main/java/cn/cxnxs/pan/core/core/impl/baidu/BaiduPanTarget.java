package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import com.alibaba.fastjson.JSONObject;

/**
 * @author potatomato
 */
public class BaiduPanTarget implements Target {
    private final Volume volume;

    private JSONObject fileInfo;

    private BaiduPanTarget parent;

    private final String path;

    public BaiduPanTarget(BaiduPanTarget parent,Volume volume, String path){
        this.parent = parent;
        this.volume = volume;
        this.path = path;
    }

    public BaiduPanTarget(Volume volume, String path) {
        this.volume = volume;
        this.path = path;
    }

    @Override
    public Volume getVolume() {
        return volume;
    }

    public String getPath() {
        int index = path.indexOf(":\\");
        if (index!=-1) {
            return path.substring(index+2);
        }
        return path;
    }

    public Long getFsId() {
        try {
            int index = path.indexOf(":\\");
            if (index!=-1){
                String fsIdStr = path.substring(0,index);
                return Long.parseLong(fsIdStr);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public JSONObject getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(JSONObject fileInfo) {
        this.fileInfo = fileInfo;
    }

    public BaiduPanTarget getParent() {
        return parent;
    }

    public void setParent(BaiduPanTarget parent) {
        this.parent = parent;
    }
}
