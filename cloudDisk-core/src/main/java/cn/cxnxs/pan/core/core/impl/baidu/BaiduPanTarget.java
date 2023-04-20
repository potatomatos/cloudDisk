package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;

/**
 * @author potatomato
 */
public class BaiduPanTarget implements Target {
    private final Volume volume;

    private final String path;


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

    public String getAccessToken() {
       /* HttpServletRequest request = HttpUtil.getReq();
        return request.getHeader("baidu_pan_token");*/
        return "123.93d0ee516a062dd833e36759215f3896.Y5j1AALTnFN2A0JjqX4ns0JRCf7AkpjH4lfVK-D.9JLrSA";
    }
}
