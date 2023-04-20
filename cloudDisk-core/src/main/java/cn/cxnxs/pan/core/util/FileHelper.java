package cn.cxnxs.pan.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


/**
 * 文件操作类
 * @author mengjinyuan
 */
@Slf4j
public class FileHelper {

    private final static String[] STR_HEX = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 获取文件MD5
     *
     * @param file 需要计算的文件
     */
    public static String getMD5(File file) {
        StringBuilder buffer = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(org.apache.commons.io.FileUtils.readFileToByteArray(file));
            for (int value : b) {
                int d = value;
                if (d < 0) {
                    d += 256;
                }
                int d1 = d / 16;
                int d2 = d % 16;
                buffer.append(STR_HEX[d1]).append(STR_HEX[d2]);
            }
            return buffer.toString();
        } catch (Exception e) {
            return null;
        }
    }

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


    public static void main(String[] args) throws IOException {
        File file = new File("C:\\temp\\test.iso");
        log.info("文件路径：{}", file.getPath());
        File[] separate = FileHelper.separate(file, "C:\\temp\\test.part", 32);
        FileHelper.merge("C:\\temp\\data.iso", separate);
    }
}


