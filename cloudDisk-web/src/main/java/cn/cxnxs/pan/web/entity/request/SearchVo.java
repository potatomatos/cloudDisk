package cn.cxnxs.pan.web.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-04-01 00:31
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchVo extends BaseVo{
    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 排序
     */
    private Integer sort;

    public static final Integer SORT_DEFAULT=0;
    public static final Integer SORT_TITLE=1;
    public static final Integer SORT_DOMAIN=2;
}
