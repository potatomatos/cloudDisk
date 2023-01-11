package cn.cxnxs.pan.core.command;

import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 获得所有上级
 */
public class ParentsCommand extends AbstractJsonCommand implements ElfinderCommand {
    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception {
        final String target = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_TARGET);

        Map<String, VolumeHandler> files = new HashMap<>();
        VolumeHandler volumeHandler = findTarget(elfinderStorage, target);
        do {
            volumeHandler = volumeHandler.getParent();
            addSubFolders(files, volumeHandler);
        }while (!volumeHandler.isRoot());

        json.put(ElFinderConstants.ELFINDER_PARAMETER_TREE, buildJsonFilesArray(request, files.values()));
    }
}
