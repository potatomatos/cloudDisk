package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.exception.ElFinderException;
import cn.cxnxs.pan.core.util.FileUtils;
import cn.cxnxs.pan.core.util.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.HttpMethods;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static cn.cxnxs.pan.core.core.impl.baidu.Constant.*;

/**
 * @author potatomato
 */
public class BaiduPanService {

    private static final Logger logger = LoggerFactory.getLogger(BaiduPanService.class);

    public HttpConfig buildOption(String url) {
        HttpConfig httpConfig = HttpUtil.buildOption(url);
        HttpHeader httpHeader = HttpHeader.custom()
                .host("pan.baidu.com")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36 SE 2.X MetaSr 1.0");
        httpConfig.headers(httpHeader.build());
        return httpConfig;
    }

    public HttpConfig buildOption(String url, HttpMethods methods) {
        return buildOption(url, methods, null);
    }

    public HttpConfig buildOption(String url, HttpMethods methods, HttpHeader httpHeader) {
        HttpConfig httpConfig = HttpUtil.buildOption(url, methods);
        if (httpHeader == null) {
            httpHeader = HttpHeader.custom()
                    .host("pan.baidu.com")
                    .userAgent("pan.baidu.com");
        }
        httpConfig.headers(httpHeader.build());
        return httpConfig;
    }

    /**
     * 创建文件
     */
    public void createFile(BaiduPanTarget target) throws HttpProcessException, IOException, ServletException {
        //0.创建临时文件夹
        String tmpDirPath = SEPARATE_PATH + target.getAccessToken() + File.separator;
        File tmpDir = new File(tmpDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        // 创建文件
        Part part = HttpUtil.getFile();
        // 将文件写入临时文件夹
        String filename = part.getName();
        part.write(tmpDirPath + filename);
        File uploadFile = new File(tmpDirPath + filename);
        //将文件分片
        File[] separate = FileUtils.separate(uploadFile, UNIT);
        // 计算每个分片的MD5
        JSONArray blocklist = new JSONArray();
        for (File file : separate) {
            blocklist.add(FileUtils.getMD5(file));
        }

        //1.预上传
        HttpConfig preCreateConfig = this.buildOption(FILE_MANAGER_URL, HttpMethods.POST);
        Map<String, Object> param = new HashMap<>();
        param.put("access_token", target.getAccessToken());
        param.put("method", "precreate");
        param.put("path", target.getPath());
        param.put("isdir", 0);
        param.put("autoinit", 1);
        param.put("block_list", blocklist.toJSONString());
        int fileSize = new FileInputStream(uploadFile).available();
        param.put("size", fileSize);
        preCreateConfig.map(param);
        logger.info("------预上传");
        JSONObject preCreateResult = HttpUtil.request(preCreateConfig);
        if (preCreateResult.getInteger("errno") != 0) {
            throw new ElFinderException();
        }

        //2.分片上传
        logger.info("------分片上传，文件总大小：{}，分片数：{}", fileSize, separate.length);
        for (int i = 0; i < separate.length; i++) {
            HttpConfig separateConfig = this.buildOption(SLICING_UPLOAD_FILE_URL, HttpMethods.POST, HttpHeader.custom().host("d.pcs.baidu.com"));
            param = new HashMap<>();
            param.put("access_token", target.getAccessToken());
            param.put("method", "upload");
            param.put("type", "tmpfile");
            param.put("path", target.getPath());
            param.put("uploadid", preCreateResult.getString("uploadid"));
            param.put("partseq", i);
            separateConfig.files(new String[]{separate[i].getPath()});
            separateConfig.map(param);
            logger.info("------开始分片上传:{}", separate[i].getPath());
            HttpUtil.request(separateConfig);
            // 删除分片文件
            separate[i].deleteOnExit();
        }

        // 3.创建文件
        HttpConfig createConfig = this.buildOption(FILE_MANAGER_URL, HttpMethods.POST);
        param = new HashMap<>();
        param.put("method","create");
        param.put("access_token",target.getAccessToken());
        param.put("path",target.getPath());
        param.put("size",fileSize);
        param.put("isdir","0");
        param.put("block_list",blocklist);
        param.put("uploadid",preCreateResult.getString("uploadid"));
        createConfig.map(param);
        HttpUtil.request(createConfig);
        //删除临时文件
        uploadFile.deleteOnExit();
    }


    /**
     * 创建文件夹
     * @param target
     * @throws HttpProcessException
     */
    public void createFolder(BaiduPanTarget target) throws HttpProcessException {
        HttpConfig createConfig = this.buildOption(FILE_MANAGER_URL, HttpMethods.POST);
        Map<String,Object> param = new HashMap<>();
        param.put("method","create");
        param.put("access_token",target.getAccessToken());
        param.put("path",target.getPath());
        param.put("isdir",1);
        createConfig.map(param);
        HttpUtil.request(createConfig);
    }

    /**
     * 删除文件/文件夹
     * @param target
     * @throws HttpProcessException
     */
    public void deleteFile(BaiduPanTarget target) throws HttpProcessException {
        HttpConfig config = this.buildOption(FILE_MANAGER_URL, HttpMethods.POST);
        Map<String,Object> param = new HashMap<>();
        param.put("method","filemanager");
        param.put("access_token",target.getAccessToken());
        param.put("opera","delete");
        param.put("async",1);
        JSONArray fileList = new JSONArray();
        fileList.add(target.getPath());
        param.put("filelist",fileList.toJSONString());
        config.map(param);
        HttpUtil.request(config);
    }


    /**
     * 获取文件信息
     * @return
     */
    public JSONObject getFileInfo(BaiduPanTarget target) {
        HttpConfig config = this.buildOption(FILE_MANAGER_URL, HttpMethods.POST);
        Map<String,Object> param = new HashMap<>();
        param.put("method","filemanager");
        param.put("access_token",target.getAccessToken());
        param.put("opera","delete");
        param.put("async",1);
        JSONArray fileList = new JSONArray();
        fileList.add(target.getPath());
        param.put("filelist",fileList.toJSONString());
        config.map(param);
        HttpUtil.request(config);
        return null;
    }

}
