package cn.cxnxs.pan.core.support.detect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface Detector {

    String detect(InputStream inputStream) throws IOException;

    String detect(Path path) throws IOException;
}
