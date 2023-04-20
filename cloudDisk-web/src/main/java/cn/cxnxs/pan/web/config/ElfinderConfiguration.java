package cn.cxnxs.pan.web.config;

import cn.cxnxs.pan.core.param.Node;
import cn.cxnxs.pan.core.param.Thumbnail;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties(prefix="file-manager")
public class ElfinderConfiguration {

    private Thumbnail thumbnail;

    private List<Node> volumes;

    /**
     * 默认不限制
     */
    private Long maxUploadSize = -1L;

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<Node> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Node> volumes) {
        this.volumes = volumes;
    }

    public Long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(Long maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }
}
