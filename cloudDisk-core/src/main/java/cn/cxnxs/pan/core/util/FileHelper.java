package cn.cxnxs.pan.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


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
}


