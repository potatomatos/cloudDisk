package cn.cxnxs.pan.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.security.MessageDigest;

import static cn.cxnxs.pan.core.core.impl.baidu.Constant.UNIT;


/**
 * 文件操作类
 */
@Slf4j
public class FileUtils {

    private final static String[] strHex = {"0", "1", "2", "3", "4", "5",
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
                buffer.append(strHex[d1]).append(strHex[d2]);
            }
            return buffer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param file 需要分片的文件
     * @param unit 分片大小
     */
    public static File[] separate(File file, Integer unit) throws IOException {

        //输入流用于读取文件数据
        InputStream bis = null;
        //输出流用于输出分片文件至磁盘
        OutputStream bos = null;
        try {
            String filePath = file.getAbsolutePath();
            File newFile = new File(filePath.substring(0, filePath.lastIndexOf(File.separator) + 1));
            //单片文件大小,MB
            long splitSize = unit * 1024 * 1024;
            if (file.length() < splitSize) {
                log.info("文件小于单个分片大小，无需分片{}", file.length());
                return new File[]{file};
            }

            //分片一
            bis = new BufferedInputStream(new FileInputStream(file));
            //已读取的字节数
            long writeByte = 0;
            int len;
            byte[] bt = new byte[1024];
            while (-1 != (len = bis.read(bt))) {
                if (writeByte % splitSize == 0) {
                    if (bos != null) {
                        bos.flush();
                        bos.close();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(filePath + "_" + (writeByte / splitSize + 1) + ".part"));
                }
                writeByte += len;
                bos.write(bt, 0, len);
            }
            log.info("文件分片完成！");

            //排除被分片的文件
            if (newFile.isDirectory()) {
                File[] files = newFile.listFiles();
                assert files != null;
                File[] resultFiles = new File[files.length - 1];
                int j = 0;
                for (File value : files) {
                    if (!value.equals(file)) {
                        resultFiles[j] = value;
                        j++;
                    }
                }
                return resultFiles;
            }
        } catch (Exception e) {
            log.error("文件分片失败！");
            e.printStackTrace();
        } finally {
            if (bos != null) {
                bos.flush();
                bos.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
        return new File[0];
    }

    /**
     * 合并文件
     *
     * @param separate 文件分片
     * @param output   输出路径
     * @return
     */
    public static void merge(File[] separate, String output) throws IOException {
        String filePath;
        if (StringUtils.isNotBlank(output)) {
            filePath = output;
        } else {
            filePath = separate[0].getAbsolutePath().substring(0, separate[0].getAbsolutePath().lastIndexOf("_") - 1);
        }
        File tempFile = new File(filePath);
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        RandomAccessFile reader;
        for (File file : separate) {
            //读取切片文件
            reader = new RandomAccessFile(file, "r");
            byte[] tempByteArray = new byte[1024];
            int n;
            while ((n = reader.read(tempByteArray)) != -1) {
                raf.write(tempByteArray, 0, n);
            }
            //解除暂存文件  占用  才能删除 temp文件
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        raf.close();
        log.info("合并文件MD5：{}",getMD5(tempFile));
    }

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\tmp\\test\\.m2.zip");
        log.info("文件路径：{}", file.getPath());
        File[] separate = FileUtils.separate(file, UNIT);
        long fileSizeSum = 0;
        for (File file1 : separate) {
            fileSizeSum += new FileInputStream(file1).available();
        }
        log.info("文件原始大小：{}，分片后总大小:{}，md5：{}", new FileInputStream(file).available(), fileSizeSum, FileUtils.getMD5(file));
        FileUtils.merge(separate, "C:\\tmp\\test\\data.zip");
    }
}


