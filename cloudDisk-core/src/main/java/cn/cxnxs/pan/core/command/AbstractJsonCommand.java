package cn.cxnxs.pan.core.command;


import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import com.alibaba.fastjson.JSONObject;

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
            writer.write(json.toJSONString());
            writer.flush();
        } catch (Exception e) {
            logger.error("Unable to execute abstract json command", e);
            json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_ERROR, e.getMessage());
            writer.write(json.toJSONString());
            writer.flush();
        } finally {
            writer.close();
        }
    }

}
