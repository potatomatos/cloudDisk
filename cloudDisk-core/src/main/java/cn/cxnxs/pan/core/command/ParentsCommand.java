package cn.cxnxs.pan.core.command;

import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * 获得所有上级
 */
public class ParentsCommand extends AbstractJsonCommand implements ElfinderCommand {
    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception {
        final String target = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_TARGET);
        Object[] objects = new Object[]{};
        List<VolumeHandler> files = new ArrayList<>();
        VolumeHandler volumeHandler = findTarget(elfinderStorage, target);
        if (volumeHandler.isRoot()) {
            json.put(ElFinderConstants.ELFINDER_PARAMETER_TREE, objects);
            return;
        }
        while (!volumeHandler.isRoot()) {
            files.add(volumeHandler);
            volumeHandler = volumeHandler.getParent();
        }
        Collections.reverse(files);
        objects = files.stream().map(handler -> {
            try {
                return getTargetInfo(request, handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).toArray();

        json.put(ElFinderConstants.ELFINDER_PARAMETER_TREE, objects);
    }
}
