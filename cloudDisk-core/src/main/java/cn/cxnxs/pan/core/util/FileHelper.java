package cn.cxnxs.pan.core.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 文件操作类
 *
 * @author mengjinyuan
 */
@Slf4j
public class FileHelper {

    /**
     * @param inputFile 需要分片的文件
     * @param output    输出文件夹
     * @param unit      分片大小（MB）
     */
    public static File[] separate(File inputFile, String output, Integer unit) throws IOException {
        File outputDir = new File(output);
        if (!outputDir.exists()) {
            // 创建输出文件夹
            boolean flag = outputDir.mkdirs();
            if (flag) {
                log.info("输出文件夹创建成功");
            } else {
                log.error("输出文件夹创建失败！");
                return new File[0];
            }
        }
        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] buffer = new byte[unit * 1024 * 1024];
        int bytesRead;
        int count = 0;
        List<String> fileNames = new ArrayList<>();
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            String chunkFileName = output + File.separator + inputFile.getName() + ".part" + String.format("%03d", count++);
            fileNames.add(chunkFileName);
            FileOutputStream outputStream = new FileOutputStream(chunkFileName);
            outputStream.write(buffer, 0, bytesRead);
            outputStream.close();
        }
        log.info("文件分片完成");
        inputStream.close();
        //返回被分片的文件
        List<File> returnFiles = new ArrayList<>();
        if (outputDir.isDirectory()) {
            File[] files = outputDir.listFiles();
            assert files != null;
            for (String fileName : fileNames) {
                for (File file : files) {
                    if (fileName.equals(file.getAbsolutePath())) {
                        returnFiles.add(file);
                    }
                }
            }
            return returnFiles.toArray(new File[0]);
        }

        return new File[0];
    }

    /**
     * 合并文件
     *
     * @param inputFiles     文件分片
     * @param outputFileName 输出路径
     */
    public static void merge(String outputFileName, File... inputFiles) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(outputFileName);
        for (File inputFile : inputFiles) {
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        }
        outputStream.close();
        log.info("文件已成功合并至:{}", outputFileName);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 获取文件MD5
     *
     * @param file 待计算的文件
     * @param kb   要计算的大小（KB）
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getFileMD5(File file, int kb) throws IOException, NoSuchAlgorithmException {
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("Invalid file path");
        }
        if (kb < 0) {
            throw new InvalidParameterException("kb must greater than 0");
        }
        long fileSize = file.length();
        if (fileSize < kb * 1024L || kb == 0) {
            // 计算整个文件
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mdBytes = md.digest(FileUtils.readFileToByteArray(file));
            return bytesToHex(mdBytes);
        } else {
            // 计算前KB大小的MD5
            byte[] data = new byte[kb * 1024];
            FileInputStream fis = new FileInputStream(file);
            fis.read(data);
            fis.close();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mdBytes = md.digest(data);
            return bytesToHex(mdBytes);
        }
    }


    private static final Map<String, String> mimeMap = new HashMap<>();

    static {
        mimeMap.put("3gp", "video/3gpp");
        mimeMap.put("apk", "application/vnd.android.package-archive");
        mimeMap.put("asf", "video/x-ms-asf");
        mimeMap.put("avi", "video/x-msvideo");
        mimeMap.put("bin", "application/octet-stream");
        mimeMap.put("bmp", "image/bmp");
        mimeMap.put("c", "text/x-c");
        mimeMap.put("class", "application/octet-stream");
        mimeMap.put("conf", "text/plain");
        mimeMap.put("cpp", "text/x-c");
        mimeMap.put("doc", "application/msword");
        mimeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimeMap.put("xls", "application/vnd.ms-excel");
        mimeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimeMap.put("exe", "application/octet-stream");
        mimeMap.put("gif", "image/gif");
        mimeMap.put("gtar", "application/x-gtar");
        mimeMap.put("gz", "application/x-gzip");
        mimeMap.put("h", "text/x-c");
        mimeMap.put("htm", "text/html");
        mimeMap.put("html", "text/html");
        mimeMap.put("jar", "application/java-archive");
        mimeMap.put("java", "text/x-java-source");
        mimeMap.put("jpeg", "image/jpeg");
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("js", "application/x-javascript");
        mimeMap.put("log", "text/plain");
        mimeMap.put("m3u", "audio/x-mpegurl");
        mimeMap.put("m4a", "audio/mp4a-latm");
        mimeMap.put("m4b", "audio/mp4a-latm");
        mimeMap.put("m4p", "audio/mp4a-latm");
        mimeMap.put("m4u", "video/vnd.mpegurl");
        mimeMap.put("m4v", "video/x-m4v");
        mimeMap.put("mov", "video/quicktime");
        mimeMap.put("mp2", "audio/x-mpeg");
        mimeMap.put("mp3", "audio/x-mpeg");
        mimeMap.put("mp4", "video/mp4");
        mimeMap.put("mpc", "application/vnd.mpohun.certificate");
        mimeMap.put("mpe", "video/mpeg");
        mimeMap.put("mpeg", "video/mpeg");
        mimeMap.put("mpg", "video/mpeg");
        mimeMap.put("mpg4", "video/mp4");
        mimeMap.put("mpga", "audio/mpeg");
        mimeMap.put("msg", "application/vnd.ms-outlook");
        mimeMap.put("ogg", "audio/ogg");
        mimeMap.put("pdf", "application/pdf");
        mimeMap.put("png", "image/png");
        mimeMap.put("pps", "application/vnd.ms-powerpoint");
        mimeMap.put("ppt", "application/vnd.ms-powerpoint");
        mimeMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mimeMap.put("prop", "text/plain");
        mimeMap.put("rc", "text/plain");
        mimeMap.put("rmvb", "audio/x-pn-realaudio");
        mimeMap.put("rtf", "application/rtf");
        mimeMap.put("sh", "text/plain");
        mimeMap.put("tar", "application/x-tar");
        mimeMap.put("tgz", "application/x-compressed");
        mimeMap.put("txt", "text/plain");
        mimeMap.put("wav", "audio/x-wav");
        mimeMap.put("wma", "audio/x-ms-wma");
        mimeMap.put("wmv", "audio/x-ms-wmv");
        mimeMap.put("wps", "application/vnd.ms-works");
        mimeMap.put("xml", "text/xml");
        mimeMap.put("z", "application/x-compress");
        mimeMap.put("zip", "application/x-zip-compressed");
    }

    public static String getMime(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "";
        }
        String suffix = FileUtil.getSuffix(filename.toLowerCase());
        String mime = mimeMap.get(suffix);
        if (mime == null) {
            mime = "application/octet-stream";
        }
        return mime;
    }

}


