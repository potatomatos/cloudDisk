package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.core.VolumeBuilder;
import cn.cxnxs.pan.core.param.Node;
import cn.cxnxs.pan.core.util.HttpUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.HttpMethods;
import com.arronlong.httpclientutil.exception.HttpProcessException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static cn.cxnxs.pan.core.service.VolumeSources.BAIDU_PAN;

public class BaiduPanVolume implements Volume {

    private final String alias;
    private final String source;
    private final Target rootTarget;
    private final Path rootDir;
    private final String apiUrl;

    public BaiduPanVolume(Builder builder, Path rootDir, Node nodeConfig) {
        Properties config = nodeConfig.getConfig();
        if (config == null) {
            throw new RuntimeException("Please config your baidu pan config");
        }
        this.apiUrl = config.getProperty("apiUrl");
        this.alias = builder.alias;
        this.source = BAIDU_PAN.name();
        this.rootDir = rootDir;
        this.rootTarget = new BaiduPanTarget(this, rootDir);
    }

    private HttpConfig buildOption(String url) throws HttpProcessException {
        HttpConfig httpConfig = HttpUtil.buildOption(this.apiUrl + url);
        HttpHeader httpHeader = HttpHeader.custom()
                .host("pan.baidu.com")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36 SE 2.X MetaSr 1.0");
        httpConfig.headers(httpHeader.build());
        return httpConfig;
    }

    private HttpConfig buildOption(String url, HttpMethods methods) throws HttpProcessException {
        HttpConfig httpConfig = HttpUtil.buildOption(this.apiUrl + url, methods);
        HttpHeader httpHeader = HttpHeader.custom()
                .host("pan.baidu.com")
                .userAgent("pan.baidu.com");
        httpConfig.headers(httpHeader.build());
        return httpConfig;
    }

    @Override
    public void createFile(Target target) throws HttpProcessException {
        HttpConfig httpConfig = this.buildOption("/rest/2.0/xpan/file");
        Map<String,Object> param = new HashMap<>();
        BaiduPanTarget baiduPanTarget = (BaiduPanTarget) target;
        param.put("access_token",baiduPanTarget.getAccessToken());
        param.put("method","create");
        httpConfig.map(param);
        HttpUtil.request(httpConfig);
    }

    @Override
    public void createFolder(Target target) throws IOException {

    }

    @Override
    public void deleteFile(Target target) throws IOException {

    }

    @Override
    public void deleteFolder(Target target) throws IOException {

    }

    @Override
    public boolean exists(Target target) {
        return false;
    }

    @Override
    public Target fromPath(String path) {
        return null;
    }

    @Override
    public long getLastModified(Target target) throws IOException {
        return 0;
    }

    @Override
    public String getMimeType(Target target) throws IOException {
        return null;
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public String getName(Target target) {
        return null;
    }

    @Override
    public Target getParent(Target target) {
        return null;
    }

    @Override
    public String getPath(Target target) throws IOException {
        return null;
    }

    @Override
    public Target getRoot() {
        return null;
    }

    @Override
    public long getSize(Target target) throws IOException {
        return 0;
    }

    @Override
    public boolean hasChildFolder(Target target) throws IOException {
        return false;
    }

    @Override
    public boolean isFolder(Target target) {
        return false;
    }

    @Override
    public boolean isRoot(Target target) throws IOException {
        return false;
    }

    @Override
    public Target[] listChildren(Target target) throws IOException {
        return new Target[0];
    }

    @Override
    public InputStream openInputStream(Target target) throws IOException {
        return null;
    }

    @Override
    public OutputStream openOutputStream(Target target) throws IOException {
        return null;
    }

    @Override
    public void rename(Target origin, Target destination) throws IOException {

    }

    @Override
    public List<Target> search(String target) throws IOException {
        return null;
    }


    public static Builder builder(String alias, Path rootDir, Node nodeConfig) {
        return new BaiduPanVolume.Builder(alias, rootDir, nodeConfig);
    }

    public static class Builder implements VolumeBuilder<BaiduPanVolume> {
        private final String alias;
        private final Path path;
        private final Node nodeConfig;

        public Builder(String alias, Path rootDir, Node nodeConfig) {
            this.alias = alias;
            this.nodeConfig = nodeConfig;
            this.path = rootDir;
        }

        @Override
        public BaiduPanVolume build() {
            return new BaiduPanVolume(this, path, nodeConfig);
        }
    }
}
