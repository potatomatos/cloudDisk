package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.core.VolumeBuilder;
import cn.cxnxs.pan.core.param.Node;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.cxnxs.pan.core.service.VolumeSources.BAIDU;


@Slf4j
public class BaiduPanVolume implements Volume {

    private final String alias;
    private final String source;
    private final Target rootTarget;
    private final String rootDir;

    private final BaiduPanService baiduPanService;

    public BaiduPanVolume(Builder builder, String rootDir, Node nodeConfig) {
        this.alias = builder.alias;
        this.source = BAIDU.name();
        this.rootDir = rootDir;
        this.rootTarget = new BaiduPanTarget(this, rootDir);
        this.baiduPanService = new BaiduPanService();
    }

    @SneakyThrows
    @Override
    public void createFile(Target target) throws IOException {
        this.baiduPanService.createFile((BaiduPanTarget) target);
    }

    @SneakyThrows
    @Override
    public void createFolder(Target target) throws IOException {
        this.baiduPanService.createFolder((BaiduPanTarget) target);
    }

    @SneakyThrows
    @Override
    public void deleteFile(Target target) throws IOException {
        this.baiduPanService.deleteFile((BaiduPanTarget) target);
    }

    @SneakyThrows
    @Override
    public void deleteFolder(Target target) throws IOException {
        this.baiduPanService.deleteFile((BaiduPanTarget) target);
    }

    @SneakyThrows
    @Override
    public boolean exists(Target target) {
        JSONObject fileInfo = this.baiduPanService.getFileInfo((BaiduPanTarget) target);
        return !fileInfo.isEmpty();
    }

    @Override
    public Target fromPath(String path) {
        return new BaiduPanTarget(this, path);
    }

    @SneakyThrows
    @Override
    public long getLastModified(Target target) throws IOException {
        JSONObject fileInfo = this.baiduPanService.getFileInfo((BaiduPanTarget) target);
        return fileInfo.getLong("server_mtime") * 1000;
    }

    @SneakyThrows
    @Override
    public String getMimeType(Target target) throws IOException {
        JSONObject fileInfo = this.baiduPanService.getFileInfo((BaiduPanTarget) target);
        return Objects.requireNonNull(FileType.getType(fileInfo.getInteger("category"))).name().toLowerCase();
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    public String getSource() {
        return this.source;
    }

    @SneakyThrows
    @Override
    public String getName(Target target) {
        JSONObject fileInfo = this.baiduPanService.getFileInfo((BaiduPanTarget) target);
        return fileInfo.getString("filename");
    }

    @Override
    public Target getParent(Target target) {
        BaiduPanTarget baiduPanTarget = (BaiduPanTarget) target;
        String path = baiduPanTarget.getPath().substring(baiduPanTarget.getPath().lastIndexOf("/"));
        return new BaiduPanTarget(this, path);
    }

    @Override
    public String getPath(Target target) {
        return ((BaiduPanTarget) target).getPath();
    }

    @Override
    public Target getRoot() {
        return this.rootTarget;
    }

    @SneakyThrows
    @Override
    public long getSize(Target target) throws IOException {
        JSONObject fileInfo = this.baiduPanService.getFileInfo((BaiduPanTarget) target);
        return fileInfo.getLong("size");
    }

    @SneakyThrows
    @Override
    public boolean hasChildFolder(Target target) throws IOException {
        JSONObject fileList = this.baiduPanService.getFileList((BaiduPanTarget) target, 1);
        return fileList.getInteger("errno") == 0 && fileList.getJSONArray("list").size() > 0;
    }

    @SneakyThrows
    @Override
    public boolean isFolder(Target target) {
        JSONObject fileInfo = this.baiduPanService.getFileInfo((BaiduPanTarget) target);
        return fileInfo.getInteger("isdir") == 1;
    }

    @SneakyThrows
    @Override
    public boolean isRoot(Target target) {
        return this.rootDir.equals(((BaiduPanTarget) target).getPath());
    }

    @SneakyThrows
    @Override
    public Target[] listChildren(Target target) throws IOException {
        JSONObject result = this.baiduPanService.getFileList((BaiduPanTarget) target, 0);
        if (result.getInteger("errno") == 0) {
            JSONArray list = result.getJSONArray("list");
            BaiduPanTarget[] targets = new BaiduPanTarget[list.size()];
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.getJSONObject(i);
                targets[i] = new BaiduPanTarget(this, jsonObject.getString("path"));
            }
            return targets;
        }

        return new Target[0];
    }

    @SneakyThrows
    @Override
    public InputStream openInputStream(Target target) {
        JSONObject fileInfo = this.baiduPanService.getFileInfo((BaiduPanTarget) target);
        if (StringUtils.isNoneBlank(fileInfo.getString("dlink"))) {
            return this.baiduPanService.downloadFile((BaiduPanTarget) target, fileInfo.getString("dlink"));
        }
        return null;
    }

    @SneakyThrows
    @Override
    public OutputStream openOutputStream(Target target) throws IOException {
        InputStream inputStream = this.openInputStream(target);
        if (inputStream != null) {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            int ch;
            while ((ch = inputStream.read()) != -1) {
                swapStream.write(ch);
            }
            return swapStream;
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void rename(Target origin, Target destination) {
        this.baiduPanService.renameFile((BaiduPanTarget) origin, (BaiduPanTarget) destination);
    }

    @SneakyThrows
    @Override
    public List<Target> search(String target) {
        JSONObject result = this.baiduPanService.search(target);
        List<Target> targets = new ArrayList<>();
        if (result.getInteger("errno") == 0) {
            JSONArray list = result.getJSONArray("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.getJSONObject(i);
                targets.add(new BaiduPanTarget(this, jsonObject.getString("path")));
            }
        }
        return targets;
    }

    public static Builder builder(String alias, String rootDir, Node nodeConfig) {
        return new BaiduPanVolume.Builder(alias, rootDir, nodeConfig);
    }

    public static class Builder implements VolumeBuilder<BaiduPanVolume> {
        private final String alias;
        private final String path;
        private final Node nodeConfig;

        public Builder(String alias, String rootDir, Node nodeConfig) {
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
