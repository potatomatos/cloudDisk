package cn.cxnxs.pan.web.controller;

import cn.cxnxs.common.api.auth.Oauth2Service;
import cn.cxnxs.common.core.entity.response.Result;
import cn.cxnxs.common.core.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2023-02-15 23:07
 **/
@RestController
@RequestMapping("system")
@Slf4j
public class SystemController {

    @Autowired
    private Oauth2Service oauth2Service;

    @Value("${oauth.clientSecret}")
    private String clientSecret;

    /**
     * 获取token
     */
    @GetMapping("/getAccessToken")
    public Result<Map<String, String>> getAccessToken(@RequestParam("code") String code,
                                                      @RequestParam("clientId")String clientId,
                                                      @RequestParam("redirectUri")String redirectUri) {
        log.info("code:{},clientId:{},redirectUri:{}", code, clientId, redirectUri);
        if (StringUtil.isEmpty(code)) {
            return Result.failure("code不能为空！");
        }
        log.info("------开始获取token------");
        Map<String, String> accessToken = oauth2Service.getAccessToken(
                "authorization_code",
                clientId,
                clientSecret,
                code,
                redirectUri);
        log.info("token信息：{}", JSON.toJSONString(accessToken));
        return Result.success(accessToken);
    }
}
