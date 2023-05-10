package cn.cxnxs.pan.core.web.clouddiskweb;

import org.junit.Test;

public class DemoTest {

    @Test
    public void test() {
        String path = "//test.json";

        String fixedPath = path.replaceAll("/{2,}", "/");
        System.out.println(fixedPath);
    }
}
