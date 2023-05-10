package cn.cxnxs.pan.core.core;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

public interface Volume {

    void createFile(Target target) throws IOException;

    void createFolder(Target target) throws IOException;

    void deleteFile(Target target) throws IOException;

    void deleteFolder(Target target) throws IOException;

    boolean exists(Target target);

    Target fromPath(String path);

    long getLastModified(Target target) throws IOException;

    String getMimeType(Target target) throws IOException;

    String getAlias();

    String getName(Target target);

    Target getParent(Target target);

    String getPath(Target target) throws IOException;

    Target getRoot();

    long getSize(Target target) throws IOException;

    boolean hasChildFolder(Target target) throws IOException;

    boolean isFolder(Target target);

    boolean isRoot(Target target) throws IOException;

    Target[] listChildren(Target target) throws IOException;

    InputStream openInputStream(Target target) throws IOException;

    OutputStream openOutputStream(Target target) throws IOException;

    void rename(Target origin, Target destination) throws IOException;

    List<Target> search(String target) throws IOException;


    void putFile(Target target, InputStream inputStream) throws IOException, NoSuchAlgorithmException, HttpProcessException;

    default String getIcon(){
        return "";
    }

    String getSource();

    Properties getExtInfo();

    String getTmb(Target target, String baseURL,String hash) throws Exception;
}
