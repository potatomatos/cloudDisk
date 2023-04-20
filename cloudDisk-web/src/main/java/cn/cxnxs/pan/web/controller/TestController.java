package cn.cxnxs.pan.web.controller;

import cn.cxnxs.pan.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class TestController {

    @RequestMapping("testRequest")
    public String testRequest(HttpServletRequest request) {
        HttpUtil.setReq(request);
        new Thread(() -> {
            HttpServletRequest req = HttpUtil.getReq();
            log.info("access_token:{}", request.getHeader("access_token"));
        }).start();
        return "OK";
    }
}
