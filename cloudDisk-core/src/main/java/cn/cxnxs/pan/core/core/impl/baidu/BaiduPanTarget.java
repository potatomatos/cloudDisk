package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author potatomato
 */
public class BaiduPanTarget implements Target {
    private final Volume volume;

    private JSONObject fileInfo;

    private final String path;

    private Long fsId;

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

    public String getAccessToken(String tokenKey) {
        HttpServletRequest request = HttpUtil.getReq();
        String token = request.getHeader(tokenKey);
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter(tokenKey);
        }
        return token;
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
}
