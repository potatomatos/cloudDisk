package cn.cxnxs.pan.core.support.http;

import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpCookies;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.HttpMethods;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;

public class HttpOption {

    public static HttpConfig buildOption(String url, HttpMethods method) throws HttpProcessException {
        HttpHeader httpHeader = HttpHeader.custom().userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36 SE 2.X MetaSr 1.0");
        Header[] headers = httpHeader.build();
        //重试5次
        HCB hcb = HCB.custom().retry(5);
        //是否绕过ssl
        hcb.ssl();
        HttpClient client = hcb.build();
        //获取返回的cookie
        HttpCookies cookies = HttpCookies.custom();
        //插件式配置请求参数（网址、请求参数、编码、client）
        return HttpConfig.custom()
                .url(url)
                .headers(headers)
                .client(client)
                .encoding("utf-8")
                .timeout(5000)
                .context(cookies.getContext())
                .method(method);
    }
}
