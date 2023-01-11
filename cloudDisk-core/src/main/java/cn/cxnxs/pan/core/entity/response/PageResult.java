package cn.cxnxs.pan.core.entity.response;

import java.util.List;

/**
 * <p>分页结果对象</p>
 *
 * @author mengjinyuan
 * @date 2020-12-09 14:03
 **/
public class PageResult<T> extends Result<T> {
    /**
     * 数据总数
     */
    private Long count;

    private Long pageSize;

    private Long pages;

    private Long current;

    private List<T> rows;

    public PageResult(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "count=" + count +
                ", pageSize=" + pageSize +
                ", pages=" + pages +
                ", current=" + current +
                ", rows=" + rows +
                '}';
    }
}
