package cn.cxnxs.pan.core.command;

import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MkdirCommand extends AbstractJsonCommand implements ElfinderCommand {
    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception {
        String target = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_TARGET);
        String dirName = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_NAME);

        if(dirName == null){
            String[] paramMap = request.getParameterMap().get(ElFinderConstants.ELFINDER_PARAMETER_DIRS);
            List<Map> list = new ArrayList<>();
            for(String dir : paramMap){
                VolumeHandler volumeHandler = findTarget(elfinderStorage, target);
                VolumeHandler directory = new VolumeHandler(volumeHandler, dir);
                directory.createFolder();
                list.add(getTargetInfo(request, directory));
            }

            json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_ADDED,list);
        }else{
            VolumeHandler volumeHandler = findTarget(elfinderStorage, target);
            VolumeHandler directory = new VolumeHandler(volumeHandler, dirName);
            directory.createFolder();

            json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_ADDED, new Object[]{getTargetInfo(request, directory)});
        }


    }
}
