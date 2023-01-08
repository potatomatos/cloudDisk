package cn.cxnxs.pan.web.entity.request;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * <p>基础vo</p>
 *
 * @author mengjinyuan
 * @date 2020-11-25 14:18
 **/
public abstract class BaseVo implements Serializable {


    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 转成json
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSON.toJSONString(this);
    }
}
