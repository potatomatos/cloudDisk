package cn.cxnxs.pan.core.web.clouddiskweb;

import org.junit.Test;

public class DemoTest {

    @Test
    public void test() {
        String path = "279:\\/PC/新建文件夹";
        int index = path.indexOf(":\\");
        System.out.println(index);
        System.out.println(path.substring(0,index));
        System.out.println(path.substring(index+2));
    }
}
