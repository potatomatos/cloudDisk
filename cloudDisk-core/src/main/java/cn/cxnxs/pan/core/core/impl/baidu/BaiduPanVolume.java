package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.core.VolumeBuilder;
import cn.cxnxs.pan.core.param.Node;
import cn.cxnxs.pan.core.util.FileHelper;
import cn.cxnxs.pan.core.util.HashesUtil;
import cn.cxnxs.pan.core.util.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


@Slf4j
public class BaiduPanVolume implements Volume {

    private final String alias;
    private final String source;
    private final Target rootTarget;
    private final String rootDir;
    private final String icon;
    private final Properties extInfo;

    private final BaiduPanService baiduPanService;

    public BaiduPanVolume(Builder builder) {
        this.alias = builder.alias;
        this.source = builder.nodeConfig.getSource();
        this.rootDir = builder.rootDir;
        this.rootTarget = new BaiduPanTarget(this, rootDir);
        this.baiduPanService = new BaiduPanService(builder.nodeConfig.getExtInfo().getProperty("tokenKey"));
        this.icon = builder.nodeConfig.getIcon();
        this.extInfo = builder.nodeConfig.getExtInfo();
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
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        return baiduPanTarget.getFileInfo()!=null&&!baiduPanTarget.getFileInfo().isEmpty();
    }

    @Override
    public Target fromPath(String path) {
        return new BaiduPanTarget(this,path);
    }

    @SneakyThrows
    @Override
    public long getLastModified(Target target) throws IOException {
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        JSONObject fileInfo = baiduPanTarget.getFileInfo();
        if (fileInfo!=null) {
            return fileInfo.getLong("server_mtime") * 1000;
        }
        return 0;
    }

    @SneakyThrows
    @Override
    public String getMimeType(Target target) throws IOException {
        if (isRoot(target)) {
            return "directory";
        }
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        JSONObject fileInfo = baiduPanTarget.getFileInfo();
        if (fileInfo==null) {
            return "";
        }
        if (1==fileInfo.getInteger("isdir")) {
            return "directory";
        }
        return FileHelper.getMime(fileInfo.getString("filename"));
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public Properties getExtInfo() {
        return this.extInfo;
    }

    @Override
    public String getTmb(Target target, String baseURL, String hash) throws HttpProcessException {
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        JSONObject fileInfo = baiduPanTarget.getFileInfo();
        if (fileInfo!=null) {
            return (String) JSONPath.eval(fileInfo,"$['thumbs']['url3']");
        }
        return "";
    }

    @SneakyThrows
    @Override
    public String getName(Target target) {
        if (isRoot(target)) {
            return this.alias;
        }
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        JSONObject fileInfo = baiduPanTarget.getFileInfo();
        if (fileInfo!=null) {
            return fileInfo.getString("filename");
        }
        return "";
    }

    @Override
    public Target getParent(Target target) {
        BaiduPanTarget baiduPanTarget = (BaiduPanTarget) target;
        if (baiduPanTarget.getParent()!=null){
            return baiduPanTarget.getParent();
        }
        String parentPath = HashesUtil.getParentFolderPath(baiduPanTarget.getFilePath());
        if (Objects.equals(rootDir,parentPath)) {
            return rootTarget;
        }
        String dir = HashesUtil.getParentFolderPath(parentPath);
        String key = FileHelper.getFilename(parentPath);
        try {
            JSONObject result = this.baiduPanService.search(null,dir,key);
            if (result.getInteger("errno") == 0) {
                JSONArray list = result.getJSONArray("list");
                for (int i = 0; i < list.size(); i++) {
                    JSONObject fileInfo = list.getJSONObject(i);
                    if (Objects.equals(key,fileInfo.getString("server_filename"))) {
                        fileInfo.put("filename",fileInfo.getString("server_filename"));
                        BaiduPanTarget parent = new BaiduPanTarget(this, this.buildPath(fileInfo.getLong("fs_id"),parentPath));
                        parent.setFileInfo(fileInfo);
                        baiduPanTarget.setParent(parent);
                        return parent;
                    }
                }
            }
        } catch (HttpProcessException e) {
            e.printStackTrace();
        }
        return new BaiduPanTarget(this, parentPath);
    }

    @Override
    public String getPath(Target target) {
        BaiduPanTarget baiduPanTarget = (BaiduPanTarget) target;
        return baiduPanTarget.getPath();
    }

    @Override
    public Target getRoot() {
        return this.rootTarget;
    }

    @SneakyThrows
    @Override
    public long getSize(Target target) throws IOException {
        if (isRoot(target)) {
            return 0;
        }
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        JSONObject fileInfo = baiduPanTarget.getFileInfo();
        if (fileInfo!=null) {
            return fileInfo.getLong("size");
        }
        return 0;
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
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        JSONObject fileInfo = baiduPanTarget.getFileInfo();
        if (fileInfo!=null) {
            return fileInfo.getInteger("isdir") == 1;
        }
        return false;

    }

    @SneakyThrows
    @Override
    public boolean isRoot(Target target) {
        return this.rootDir.equals(((BaiduPanTarget) target).getFilePath());
    }

    @SneakyThrows
    @Override
    public Target[] listChildren(Target target) throws IOException {
        BaiduPanTarget baiduPanTarget = (BaiduPanTarget) target;
        JSONObject result = this.baiduPanService.getFileList(baiduPanTarget, 0);
        if (result.getInteger("errno") == 0) {
            JSONArray list = result.getJSONArray("list");
            BaiduPanTarget[] targets = new BaiduPanTarget[list.size()];
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.getJSONObject(i);
                targets[i] = new BaiduPanTarget(baiduPanTarget, this, this.buildPath(jsonObject.getLong("fs_id"), jsonObject.getString("path")));
            }
            return targets;
        }

        return new Target[0];
    }

    private String buildPath(Long fsId, String path) {
        return fsId+":\\"+path;
    }

    @SneakyThrows
    @Override
    public InputStream openInputStream(Target target) {
        BaiduPanTarget baiduPanTarget = this.getFileInfo((BaiduPanTarget) target);
        JSONObject fileInfo = baiduPanTarget.getFileInfo();
        if (fileInfo!=null&&StringUtils.isNoneBlank(fileInfo.getString("dlink"))) {
            return this.baiduPanService.downloadFile((BaiduPanTarget) target, fileInfo.getString("dlink"));
        }
        return null;
    }

    private BaiduPanTarget getFileInfo(BaiduPanTarget target) throws HttpProcessException {
        if (target.getFileInfo()!=null&&!target.getFileInfo().isEmpty()) {
            return target;
        } else {
            if (target.getFsId()!=null) {
                JSONObject fileInfo = this.baiduPanService.getFileInfo(target);
                target.setFileInfo(fileInfo);
            }
        }
        return target;
    }

    @SneakyThrows
    @Override
    public OutputStream openOutputStream(Target target) throws IOException {
        InputStream inputStream = this.openInputStream(target);
        return HttpUtil.convertInputStreamToOutputStream(inputStream);
    }

    @SneakyThrows
    @Override
    public void rename(Target origin, Target destination) {
        this.baiduPanService.renameFile((BaiduPanTarget) origin, (BaiduPanTarget) destination);
    }

    @SneakyThrows
    @Override
    public List<Target> search(String target) {
        JSONObject result = this.baiduPanService.search(1,null,target);
        List<Target> targets = new ArrayList<>();
        if (result.getInteger("errno") == 0) {
            JSONArray list = result.getJSONArray("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.getJSONObject(i);
                targets.add(new BaiduPanTarget(this, this.buildPath(jsonObject.getLong("fs_id"),jsonObject.getString("path"))));
            }
        }
        return targets;
    }


    @Override
    public void putFile(Target target, InputStream inputStream) throws IOException, NoSuchAlgorithmException, HttpProcessException {
        this.baiduPanService.createFile((BaiduPanTarget) target,inputStream);
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public static Builder builder(String alias, String rootDir, Node nodeConfig) {
        return new BaiduPanVolume.Builder(alias, rootDir, nodeConfig);
    }

    public static class Builder implements VolumeBuilder<BaiduPanVolume> {
        private final String alias;
        private final String rootDir;
        private final Node nodeConfig;

        public Builder(String alias, String rootDir, Node nodeConfig) {
            this.alias = alias;
            this.nodeConfig = nodeConfig;
            this.rootDir = rootDir;
        }

        @Override
        public BaiduPanVolume build() {
            return new BaiduPanVolume(this);
        }
    }
}
