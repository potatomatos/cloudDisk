package cn.cxnxs.pan.web.entity.request;

import lombok.Data;

import java.util.Map;

/**
 * <p>分页包装类</p>
 *
 * @author mengjinyuan
 * @date 2022-02-18 10:49
 **/
@Data
public class PageWrapper<T> {

    /**
     * 页数
     */
    private Integer page;
    /**
     * 每页大小
     */
    private Integer limit;


    /**
     * 排序字段
     */
    private Map<String, String> sort;

    /**
     * 参数
     */
    private T param;

    public PageWrapper() {
        this.page = 1;
        this.limit = 20;
    }
}
