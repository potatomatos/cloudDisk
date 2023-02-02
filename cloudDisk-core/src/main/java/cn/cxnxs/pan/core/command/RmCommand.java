package cn.cxnxs.pan.core.command;

import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.DirectoryNotEmptyException;
import java.util.ArrayList;
import java.util.List;

public class RmCommand extends AbstractJsonCommand implements ElfinderCommand {

    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception {
        String[] targets = request.getParameterValues(ElFinderConstants.ELFINDER_PARAMETER_TARGETS);
        List<String> removed = new ArrayList<>();

        for (String target : targets) {
            VolumeHandler vh = findTarget(elfinderStorage, target);
            try {
                vh.delete();
                removed.add(vh.getHash());
            } catch (DirectoryNotEmptyException dne) {
                json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_ERROR, "Directory not empty!");
            }


        }

        json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_REMOVED, removed.toArray());
    }
}
