package cn.cxnxs.pan.core.impl;

import cn.cxnxs.pan.core.Target;
import cn.cxnxs.pan.core.Volume;

import java.nio.file.Path;

public class NIO2FileSystemTarget implements Target {

    private final Path path;
    private final Volume volume;

    public NIO2FileSystemTarget(NIO2FileSystemVolume volume, Path path) {
        this.path = path;
        this.volume = volume;
    }

    @Override
    public Volume getVolume() {
        return volume;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path.toAbsolutePath().toString();
    }
}
