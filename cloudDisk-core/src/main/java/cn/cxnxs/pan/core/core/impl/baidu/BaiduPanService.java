package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.exception.ElFinderException;
import cn.cxnxs.pan.core.exception.ElfinderConfigurationException;
import cn.cxnxs.pan.core.util.FileHelper;
import cn.cxnxs.pan.core.util.HttpUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.HttpMethods;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static cn.cxnxs.pan.core.core.impl.baidu.Constant.*;

/**
 * @author potatomato
 */
public class BaiduPanService {

    private static final Logger logger = LoggerFactory.getLogger(BaiduPanService.class);

    private final String tokenKey;

    public BaiduPanService(String tokenKey) {
        if (StringUtils.isBlank(tokenKey)) {
            throw new ElfinderConfigurationException("baidu tokenKey can not be empty");
        }
        this.tokenKey = tokenKey;
    }

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

    public void createFile(BaiduPanTarget target, InputStream is) throws IOException, NoSuchAlgorithmException, HttpProcessException {
        //0.创建临时文件夹
        String tmpDirPath = SEPARATE_PATH + getAccessToken(tokenKey) + File.separator;
        FileUtils.forceMkdir(new File(tmpDirPath));
        String path = target.getPath();
        String fixedPath = path.replaceAll("/{2,}", "/");

        String filename = FileUtil.getName(path);
        // 创建文件
        // 将文件写入临时文件夹
        OutputStream os = new FileOutputStream(tmpDirPath+filename);
        IOUtils.copy(is, os);
        os.close();
        is.close();
        File uploadFile = new File(tmpDirPath + filename);

        long fileSize = uploadFile.length();
        //将文件分片
        File[] separate = FileHelper.separate(uploadFile,uploadFile.getAbsolutePath()+".part", UNIT);
        // 计算每个分片的MD5
        JSONArray blockList = new JSONArray();
        for (File file : separate) {
            blockList.add(FileHelper.getFileMD5(file,0));
        }

        //1.预上传
        Map<String, Object> param = new HashMap<>();
        param.put("access_token", getAccessToken(tokenKey));
        param.put("method", "precreate");
        param.put("path", fixedPath);
        param.put("isdir", 0);
        param.put("autoinit", 1);
        param.put("block_list", blockList.toJSONString());
        param.put("size", fileSize);
        param.put("slice-md5", FileHelper.getFileMD5(uploadFile,256));
        HttpConfig preCreateConfig = this.buildOption(HttpUtil.buildUrl(FILE_MANAGER_URL,param), HttpMethods.POST);
        logger.info("------预上传");
        JSONObject preCreateResult = HttpUtil.request(preCreateConfig);
        if (preCreateResult.getInteger("errno") != 0) {
            throw new ElFinderException();
        }

        //2.分片上传
        logger.info("------分片上传，文件总大小：{}，分片数：{}", uploadFile.length(), separate.length);
        for (int i = 0; i < separate.length; i++) {
            param = new HashMap<>();
            param.put("access_token", getAccessToken(tokenKey));
            param.put("method", "upload");
            param.put("type", "tmpfile");
            param.put("path", fixedPath);
            param.put("uploadid", preCreateResult.getString("uploadid"));
            param.put("partseq", i);
            HttpConfig separateConfig = this.buildOption(HttpUtil.buildUrl(SLICING_UPLOAD_FILE_URL,param), HttpMethods.POST, HttpHeader.custom().host("d.pcs.baidu.com"));
            separateConfig.files(new String[]{separate[i].getPath()});
            logger.info("------开始分片上传:{}", separate[i].getPath());
            HttpUtil.request(separateConfig);
            // 删除分片文件
            separate[i].deleteOnExit();
        }

        // 3.创建文件
        param = new HashMap<>();
        param.put("method","create");
        param.put("access_token",getAccessToken(tokenKey));
        HashMap<String,Object> body = new HashMap<>();
        body.put("path",fixedPath);
        body.put("size",fileSize+"");
        body.put("isdir","0");
        body.put("block_list",blockList.toJSONString());
        body.put("uploadid",preCreateResult.getString("uploadid"));
        HttpConfig createConfig = this.buildOption(HttpUtil.buildUrl(FILE_MANAGER_URL,param), HttpMethods.POST);
        createConfig.map(body);
        HttpUtil.request(createConfig);
        //删除临时文件
        uploadFile.deleteOnExit();
    }


    /**
     * 创建文件
     */
    public void createFile(BaiduPanTarget target) throws HttpProcessException, IOException, ServletException, NoSuchAlgorithmException {
        Part part = HttpUtil.getFile();
        this.createFile(target,part.getInputStream());
    }


    /**
     * 创建文件夹
     * @param target
     * @throws HttpProcessException
     */
    public void createFolder(BaiduPanTarget target) throws HttpProcessException {
        Map<String,Object> param = new HashMap<>();
        param.put("method","create");
        param.put("access_token",getAccessToken(tokenKey));
        param.put("path",target.getPath());
        param.put("isdir",1);
        HttpConfig createConfig = this.buildOption(HttpUtil.buildUrl(FILE_MANAGER_URL,param), HttpMethods.POST);
        HttpUtil.request(createConfig);
    }

    /**
     * 删除文件/文件夹
     * @param target
     * @throws HttpProcessException
     */
    public void deleteFile(BaiduPanTarget target) throws HttpProcessException {
        Map<String,Object> param = new HashMap<>();
        param.put("method","filemanager");
        param.put("access_token",getAccessToken(tokenKey));
        param.put("opera","delete");
        param.put("async",1);
        JSONArray fileList = new JSONArray();
        fileList.add(target.getPath());
        param.put("filelist",fileList.toJSONString());
        HttpConfig config = this.buildOption(HttpUtil.buildUrl(FILE_MANAGER_URL,param), HttpMethods.POST);
        HttpUtil.request(config);
    }

    public void renameFile(BaiduPanTarget origin, BaiduPanTarget destination) throws HttpProcessException {
        Map<String,Object> param = new HashMap<>();
        param.put("method","filemanager");
        param.put("access_token",getAccessToken(tokenKey));
        param.put("opera","rename");
        param.put("async",1);
        JSONArray fileList = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path",origin.getPath());
        jsonObject.put("newname",destination.getPath().substring(destination.getPath().lastIndexOf("/")));
        fileList.add(jsonObject);
        param.put("filelist",fileList.toJSONString());
        HttpConfig config = this.buildOption(HttpUtil.buildUrl(FILE_MANAGER_URL,param), HttpMethods.POST);
        HttpUtil.request(config);
    }


    /**
     * 获取文件信息
     * @return
     */
    public JSONObject getFileInfo(BaiduPanTarget target) throws HttpProcessException {
        Map<String,Object> param = new HashMap<>();
        param.put("method","filemetas");
        param.put("access_token",getAccessToken(tokenKey));
        JSONArray fsids = new JSONArray();
        fsids.add(target.getFsId());
        param.put("fsids",fsids.toJSONString());
        param.put("dlink",1);
        param.put("thumb",1);
        param.put("extra",1);
        param.put("needmedia",1);
        HttpConfig config = this.buildOption(HttpUtil.buildUrl(FILE_INFO_URL,param));
        JSONObject request = HttpUtil.request(config);
        if (request.getInteger("errno")==0) {
            JSONArray list = request.getJSONArray("list");
            if (list.size()>0) {
                return list.getJSONObject(0);
            }
        }
        return new JSONObject();
    }

    public JSONObject getFileList(BaiduPanTarget target,Integer folder) throws HttpProcessException {
        Map<String,Object> param = new HashMap<>();
        param.put("method","list");
        param.put("access_token",getAccessToken(tokenKey));
        param.put("dir",target.getPath());
        param.put("folder",folder);
        param.put("showempty",1);
        HttpConfig config = this.buildOption(HttpUtil.buildUrl(FILE_MANAGER_URL,param));
        return HttpUtil.request(config);
    }

    public InputStream downloadFile(BaiduPanTarget target,String url) throws HttpProcessException, IOException {
        Map<String,Object> param = new HashMap<>(1);
        param.put("access_token",getAccessToken(tokenKey));
        HttpConfig config = this.buildOption(HttpUtil.buildUrl(url,param), HttpMethods.GET, HttpHeader.custom());
        return HttpUtil.downloadFile(config);
    }

    public JSONObject search(Integer recursion,String dir,String key) throws HttpProcessException {
        Map<String,Object> param = new HashMap<>();
        param.put("method","search");
        param.put("access_token",getAccessToken(tokenKey));
        if (StringUtils.isNotEmpty(dir)) {
            param.put("dir",dir);
        }
        param.put("key",key);
        if (recursion!=null) {
            // 递归搜索
            param.put("recursion",recursion);
        }
        param.put("web",1);
        HttpConfig config = this.buildOption(HttpUtil.buildUrl(FILE_MANAGER_URL,param), HttpMethods.GET);
        return HttpUtil.request(config);
    }

    public String getAccessToken(String tokenKey) {
        HttpServletRequest request = HttpUtil.getReq();
        String token = request.getHeader(tokenKey);
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter(tokenKey);
        }
        return token;
    }

}
