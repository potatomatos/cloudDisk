package cn.cxnxs.pan.core.core.impl.baidu;

import cn.cxnxs.pan.core.exception.ElFinderException;
import cn.cxnxs.pan.core.util.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.HttpMethods;
import com.arronlong.httpclientutil.exception.HttpProcessException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author potatomato
 */
public class BaiduPanService {

    private final String apiUrl;

    public BaiduPanService(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public HttpConfig buildOption(String url) throws HttpProcessException {
        HttpConfig httpConfig = HttpUtil.buildOption(this.apiUrl + url);
        HttpHeader httpHeader = HttpHeader.custom()
                .host("pan.baidu.com")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36 SE 2.X MetaSr 1.0");
        httpConfig.headers(httpHeader.build());
        return httpConfig;
    }

    public HttpConfig buildOption(String url, HttpMethods methods) throws HttpProcessException {
        return buildOption(url,methods,null);
    }

    public HttpConfig buildOption(String url, HttpMethods methods, HttpHeader httpHeader) throws HttpProcessException {
        HttpConfig httpConfig = HttpUtil.buildOption(this.apiUrl + url, methods);
        if (httpHeader == null) {
            httpHeader = HttpHeader.custom()
                    .host("pan.baidu.com")
                    .userAgent("pan.baidu.com");
        }
        httpConfig.headers(httpHeader.build());
        return httpConfig;
    }

    /**
     * 预上传
     *
     * @param target
     * @return
     * @throws HttpProcessException
     */
    private JSONObject createFile(BaiduPanTarget target, Integer isdir) throws HttpProcessException, IOException, ServletException {
        //1.预上传
        HttpConfig httpConfig = this.buildOption("/rest/2.0/xpan/file",HttpMethods.POST);
        Map<String, Object> param = new HashMap<>();
        param.put("access_token", target.getAccessToken());
        param.put("method", "precreate");
        param.put("path", target.getPath());
        param.put("size", 0);
        param.put("isdir", isdir);
        param.put("autoinit", 1);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (isdir == 0) {
            InputStream inputStream = HttpUtil.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(HttpUtil.getMD5(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
            param.put("block_list", jsonArray.toJSONString());
            param.put("size", new ByteArrayInputStream(byteArrayOutputStream.toByteArray()).available());
            inputStream.close();
        }
        httpConfig.map(param);
        JSONObject preCreateResult = HttpUtil.request(httpConfig);
        if (preCreateResult.getInteger("errno") != 0) {
            throw new ElFinderException();
        }
        //2.分片上传
        httpConfig = this.buildOption("/rest/2.0/pcs/superfile2",HttpMethods.POST,
                HttpHeader.custom().host("d.pcs.baidu.com"));
        param = new HashMap<>();
        param.put("access_token", target.getAccessToken());
        param.put("method", "upload");
        param.put("type", "tmpfile");
        param.put("path", target.getPath());
        param.put("uploadid", preCreateResult.getString("uploadid"));
        param.put("partseq", 0);
        param.put("file", byteArrayOutputStream.toByteArray());
        return null;
    }

}
