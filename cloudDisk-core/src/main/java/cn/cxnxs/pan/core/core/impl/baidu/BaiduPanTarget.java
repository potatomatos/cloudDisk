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

    private Long fsId;

    public BaiduPanTarget(BaiduPanTarget parent,Volume volume, String path, Long fsId) {
        this.parent = parent;
        this.volume = volume;
        this.path = path;
        this.fsId = fsId;
    }

    public BaiduPanTarget(Volume volume, String path, Long fsId) {
        this.volume = volume;
        this.path = path;
        this.fsId = fsId;
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
        return path;
    }

    public Long getFsId() {
        return fsId;
    }

    public JSONObject getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(JSONObject fileInfo) {
        this.fileInfo = fileInfo;
    }

    public static class TargetInfo {
        private String path;
        private Long fsId;

        public TargetInfo() {
        }

        public TargetInfo(String path, Long fsId) {
            this.path = path;
            this.fsId = fsId;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Long getFsId() {
            return fsId;
        }

        public void setFsId(Long fsId) {
            this.fsId = fsId;
        }
    }

    public BaiduPanTarget getParent() {
        return parent;
    }

    public void setParent(BaiduPanTarget parent) {
        this.parent = parent;
    }
}
