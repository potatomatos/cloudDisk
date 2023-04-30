package cn.cxnxs.pan.core.core.impl.filesystem;

import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.core.VolumeBuilder;
import cn.cxnxs.pan.core.param.Node;
import cn.cxnxs.pan.core.support.detect.Detector;
import cn.cxnxs.pan.core.support.detect.NIO2FileTypeDetector;
import cn.cxnxs.pan.core.support.nio.NioHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * NIO Filesystem Volume Implementation.
 *
 * @author Thiago Gutenberg
 */
public class NIO2FileSystemVolume implements Volume {

    private final String alias;
    private final Path rootDir;
    private final Detector detector;
    private final String source;
    private final String icon;
    private final Properties extInfo;
    private static final String CMD_TMB_TARGET = "?cmd=tmb&target=%s";

    private NIO2FileSystemVolume(Builder builder) {
        this.alias = builder.alias;
        this.rootDir = builder.rootDir;
        this.source = builder.nodeConfig.getSource();
        this.detector = new NIO2FileTypeDetector();
        this.extInfo = builder.nodeConfig.getExtInfo();
        this.icon = builder.nodeConfig.getIcon();
        createRootDir();
    }

    private void createRootDir() {
        try {
            Target target = fromPath(rootDir);
            if (!exists(target)) {
                createFolder(target);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create root dir folder", e);
        }
    }

    public static Path fromTarget(Target target) {
        return ((NIO2FileSystemTarget) target).getPath();
    }

    private Target fromPath(Path path) {
        return fromPath(this, path);
    }

    public static Target fromPath(NIO2FileSystemVolume volume, Path path) {
        return new NIO2FileSystemTarget(volume, path);
    }

    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void createFile(Target target) throws IOException {
        NioHelper.createFile(fromTarget(target));
    }

    @Override
    public void createFolder(Target target) throws IOException {
        NioHelper.createFolder(fromTarget(target));
    }

    @Override
    public void deleteFile(Target target) throws IOException {
        NioHelper.deleteFile(fromTarget(target));
    }

    @Override
    public void deleteFolder(Target target) throws IOException {
        NioHelper.deleteFolder(fromTarget(target));
    }

    @Override
    public boolean exists(Target target) {
        return NioHelper.exists(fromTarget(target));
    }

    @Override
    public Target fromPath(String relativePath) {
        String rootDir = getRootDir().toString();

        Path path;
        if (relativePath.startsWith(rootDir)) {
            path = Paths.get(relativePath);
        } else {
            path = Paths.get(rootDir, relativePath);
        }
        return fromPath(path);
    }

    @Override
    public long getLastModified(Target target) throws IOException {
        return NioHelper.getLastModifiedTimeInMillis(fromTarget(target));
    }

    @Override
    public String getMimeType(Target target) throws IOException {
        Path path = fromTarget(target);
        return detector.detect(path);
    }

    @Override
    public String getName(Target target) {
        return NioHelper.getName(fromTarget(target));
    }

    @Override
    public Target getParent(Target target) {
        Path path = NioHelper.getParent(fromTarget(target));
        return fromPath(path);
    }

//    @Override
//    public String getPath(Target target) throws IOException {
//        String relativePath = "";
//        if (!isRoot(target)) {
//            Path path = fromTarget(target);
//            relativePath = path.subpath(getRootDir().getNameCount(), path.getNameCount()).toString();
//        }
//        return relativePath;
//    }

    @Override
    public String getPath(Target target) throws IOException {
        return NioHelper.getRelativePath(getRootDir(), fromTarget(target));
    }

    @Override
    public Target getRoot() {
        return fromPath(getRootDir());
    }

    @Override
    public long getSize(Target target) throws IOException {
        Path path = fromTarget(target);
        boolean recursiveSize = NioHelper.isFolder(path);
        return NioHelper.getTotalSizeInBytes(path, recursiveSize);
    }

    @Override
    public boolean isFolder(Target target) {
        return NioHelper.isFolder(fromTarget(target));
    }

    @Override
    public boolean isRoot(Target target) throws IOException {
        return NioHelper.isSame(getRootDir(), fromTarget(target));
    }

    @Override
    public boolean hasChildFolder(Target target) throws IOException {
        return NioHelper.hasChildFolder(fromTarget(target));
    }

    @Override
    public Target[] listChildren(Target target) throws IOException {
        List<Path> childrenResultList = NioHelper.listChildrenNotHidden(fromTarget(target));
        List<Target> targets = new ArrayList<>(childrenResultList.size());
        for (Path path : childrenResultList) {
            targets.add(fromPath(path));
        }
        return targets.toArray(new Target[targets.size()]);
    }

    @Override
    public InputStream openInputStream(Target target) throws IOException {
        return NioHelper.openInputStream(fromTarget(target));
    }

    @Override
    public OutputStream openOutputStream(Target target) throws IOException {
        return NioHelper.openOutputStream(fromTarget(target));
    }

    @Override
    public void rename(Target origin, Target destination) throws IOException {
        NioHelper.rename(fromTarget(origin), fromTarget(destination));
    }

    @Override
    public List<Target> search(String target) throws IOException {
        List<Path> searchResultList = NioHelper.search(getRootDir(), target);
        List<Target> targets = new ArrayList<>(searchResultList.size());
        for (Path path : searchResultList) {
            targets.add(fromPath(path));
        }
        return Collections.unmodifiableList(targets);
    }

    @Override
    public String getIcon() {
        return this.icon;
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
    public String getTmb(Target target, String baseURL,String hash) {
        return String.format(baseURL+CMD_TMB_TARGET, hash);
    }

    /**
     * Gets a Builder for creating a new NIO2FileSystemVolume instance.
     *
     * @return a new Builder for NIO2FileSystemVolume.
     */
    public static Builder builder(String alias, Path rootDir, Node nodeConfig) {
        return new Builder(alias, rootDir,nodeConfig);
    }

    /**
     * Builder NIO2FileSystemVolume Inner Class
     */
    public static class Builder implements VolumeBuilder<NIO2FileSystemVolume> {
        // required fields
        private final String alias;
        private final Path rootDir;
        private final Node nodeConfig;

        public Builder(String alias, Path rootDir,Node nodeConfig) {
            this.alias = alias;
            this.rootDir = rootDir;
            this.nodeConfig = nodeConfig;
        }

        @Override
        public NIO2FileSystemVolume build() {
            return new NIO2FileSystemVolume(this);
        }
    }

}
