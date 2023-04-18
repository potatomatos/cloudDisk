package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;

public class BaiduPanTarget implements Target {
    private final Volume volume;

    private final Path path;


    public BaiduPanTarget(Volume volume, Path path) {
        this.volume = volume;
        this.path = path;
    }

    @Override
    public Volume getVolume() {
        return volume;
    }

    public Path getPath() {
        return path;
    }

    public String getAccessToken(){
        HttpServletRequest request = HttpUtil.getReq();
        return request.getParameter("baidu_pan_token");
    }
}
