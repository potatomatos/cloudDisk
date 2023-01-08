package cn.cxnxs.pan.web.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2020-10-13 23:57
 **/
@Data
@AllArgsConstructor
public class ErrorResult {
    private Integer code;

    private String msg;

    private String errors;
}
