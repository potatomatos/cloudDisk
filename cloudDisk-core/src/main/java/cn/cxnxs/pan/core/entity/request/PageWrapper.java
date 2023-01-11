package cn.cxnxs.pan.core.entity.request;


import java.util.Map;

/**
 * <p>分页包装类</p>
 *
 * @author mengjinyuan
 * @date 2022-02-18 10:49
 **/
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Map<String, String> getSort() {
        return sort;
    }

    public void setSort(Map<String, String> sort) {
        this.sort = sort;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "PageWrapper{" +
                "page=" + page +
                ", limit=" + limit +
                ", sort=" + sort +
                ", param=" + param +
                '}';
    }
}
