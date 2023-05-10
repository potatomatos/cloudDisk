package cn.cxnxs.pan.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.*;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * @author potatomato
 */
public class HttpUtil {

	private static final ThreadLocal<HttpServletRequest> requests = new InheritableThreadLocal<>();
	private static final ThreadLocal<HttpServletResponse> responses = new InheritableThreadLocal<>();
	private static final ThreadLocal<HttpSession> sessions = new InheritableThreadLocal<>();

	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public static String getAttachmentFileName(String fileName, String userAgent) throws UnsupportedEncodingException {
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();

			if (userAgent.contains("msie")) {
				return "filename=\"" + URLEncoder.encode(fileName, "UTF8") + "\"";
			}

			if (userAgent.contains("opera")) {
				return "filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF8");
			}
			if (userAgent.contains("safari")) {
				return "filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + "\"";
			}
			if (userAgent.contains("mozilla")) {
				return "filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF8");
			}
		}

		return "filename=\"" + URLEncoder.encode(fileName, "UTF8") + "\"";
	}

	public static Request buildOption(String url, HttpMethods method){

	}



	public static HttpConfig buildOption(String url, HttpMethods method) {
		HttpHeader httpHeader = HttpHeader.custom().userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36 SE 2.X MetaSr 1.0");
		Header[] headers = httpHeader.build();
		//重试5次
		HCB hcb = HCB.custom().retry(5);
		//是否绕过ssl
		try {
			hcb.ssl();
		} catch (HttpProcessException e) {
			e.printStackTrace();
		}
		HttpClient client = hcb.build();
		//获取返回的cookie
		HttpCookies cookies = HttpCookies.custom();
		//插件式配置请求参数（网址、请求参数、编码、client）
		return HttpConfig.custom()
				.url(url)
				.headers(headers)
				.client(client)
				.encoding("utf-8")
				.timeout(20000)
				.context(cookies.getContext())
				.method(method);
	}

	public static HttpConfig buildOption(String url) {
		return buildOption(url,HttpMethods.GET);
	}

	/**
	 * 发送请求
	 * @param httpConfig
	 * @return
	 * @throws HttpProcessException
	 */
	public static JSONObject request(HttpConfig httpConfig) throws HttpProcessException {
		HttpResult httpResult = sendAndGet(httpConfig);
		String result = httpResult.getResult();
		logger.info("result：{}", result);
		logger.info("-----------------------------");
		return JSONObject.parseObject(result);
	}

	/**
	 * 文件下载
	 * @param httpConfig
	 * @return
	 * @throws HttpProcessException
	 * @throws IOException
	 */
	public static InputStream downloadFile(HttpConfig httpConfig) throws HttpProcessException, IOException {
		OutputStream outputStream = new ByteArrayOutputStream();
		httpConfig.out(outputStream);
		HttpClientUtil.down(httpConfig);
		return convertOutputStreamToInputStream(outputStream);
	}

	public static HttpResult sendAndGet(HttpConfig httpConfig) throws HttpProcessException {
		logger.info("-----------请求参数-----------");
		logger.info("url:{}", httpConfig.url());
//		logger.info("parameter:{}", httpConfig.map());
		logger.info("headers:{}", JSON.toJSONString(httpConfig.headers()));
		logger.info("-----------------------------");
		HttpResult httpResult = HttpClientUtil.sendAndGetResp(httpConfig);
		logger.info("-----------------------------");
		logger.info("statusLine：{}", httpResult.getStatusLine());
		logger.info("statusCode：{}", httpResult.getStatusCode());
		logger.info("resp-header：{}", Arrays.toString(httpResult.getRespHeaders()));
		return httpResult;
	}

	public static Part getFile() throws IOException, ServletException {
		return getReq().getPart("file");
	}

	public static String asUrlParams(Map<String, Object> source){
		Map<String, String> tmp = Maps.newHashMap();
		// java8 语法
		source.forEach((k, v) -> {
			if (k != null){
				try {
					tmp.put(k, URLEncoder.encode(v.toString(), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
		return Joiner.on("&").useForNull("").withKeyValueSeparator("=").join(tmp);
	}

	/**
	 * 构建请求参数
	 * @param url
	 * @param params
	 * @return
	 */
	public static String buildUrl(String url, Map<String,Object> params) {
		String[] arrSplit = url.split("[?]");
		if (arrSplit.length > 1) {
			return arrSplit[0]+"?"+arrSplit[1]+"&"+asUrlParams(params);
		} else {
			return url+"?"+asUrlParams(params);
		}
	}
	public static InputStream convertOutputStreamToInputStream(OutputStream outputStream) {
		return new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
	}

	public static OutputStream convertInputStreamToOutputStream(InputStream inputStream) throws IOException {
		if (inputStream != null) {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			int ch;
			while ((ch = inputStream.read()) != -1) {
				swapStream.write(ch);
			}
			return swapStream;
		}
		return null;
	}

	/**
	 * 将OutputStream转成InputStream
	 *
	 * @param outputStream 输出流
	 * @return 输入流
	 * @throws IOException IO异常
	 */
	public static InputStream toInputStream(OutputStream outputStream) throws IOException {
		return new InputStream() {
			byte[] buf = new byte[1024];
			int pos = 0;
			int len = 0;
			boolean isClosed = false;

			@Override
			public int read() throws IOException {
				if (isClosed) {
					return -1;
				}
				if (pos >= len) {
					pos = 0;
					outputStream.write(buf, 0, buf.length);
				}
				return buf[pos++] & 0xff;
			}

			@Override
			public void close() throws IOException {
				super.close();
				isClosed = true;
				outputStream.close();
			}
		};
	}

	public static void setReq(HttpServletRequest value){
		requests.set(value);
	}

	public static void setRes(HttpServletResponse value){
		responses.set(value);
	}
	public static HttpServletResponse getRes(){
		return responses.get();
	}
	public static void setSession(HttpSession value){
		sessions.set(value);
	}

	public static HttpSession getSession(){
		return sessions.get();
	}

	public static HttpServletRequest getReq(){
		return requests.get();
	}
}
