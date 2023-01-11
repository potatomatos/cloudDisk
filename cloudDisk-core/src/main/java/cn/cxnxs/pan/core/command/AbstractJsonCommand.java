package cn.cxnxs.pan.core.command;


import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.entity.response.Result;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public abstract class AbstractJsonCommand extends AbstractCommand {

    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";

    protected abstract void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception;

    @Override
    final public void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, HttpServletResponse response) throws Exception {

        JSONObject json = new JSONObject();

        PrintWriter writer = response.getWriter();
        try {
            execute(elfinderStorage, request, json);
            response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
            writer.write(JSON.toJSONString(Result.success(json),SerializerFeature.DisableCircularReferenceDetect));
            writer.flush();
        } catch (Exception e) {
            logger.error("Unable to execute abstract json command", e);
            json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_ERROR, e.getMessage());
            Result<Object> failure = Result.failure(e.getMessage());
            failure.setData(json);
            writer.write(JSON.toJSONString(failure));
            writer.flush();
        } finally {
            writer.close();
        }
    }

}
