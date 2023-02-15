package cn.cxnxs.pan.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author potatomato
 */
@SpringBootApplication(scanBasePackages = {"cn.cxnxs"})
@EnableFeignClients(basePackages = {"cn.cxnxs.common.api"})
@EnableDiscoveryClient
@RefreshScope
@CrossOrigin(origins = "*", maxAge = 3600)
public class CloudDiskWebApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CloudDiskWebApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CloudDiskWebApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  网盘服务启动成功   ლ(´ڡ`ლ)ﾞ  \n");
    }

}
