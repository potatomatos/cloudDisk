package cn.cxnxs.pan.core.command;

import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class OpenCommand extends AbstractJsonCommand implements ElfinderCommand {


    @Override
    public void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json)
            throws Exception {

        boolean init = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_INIT) != null;
        boolean tree = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_TREE) != null;
        String target = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_TARGET);

        Map<String, VolumeHandler> files = new LinkedHashMap<>();
        if (init) {
            json.put(ElFinderConstants.ELFINDER_PARAMETER_API, ElFinderConstants.ELFINDER_VERSION_API);
            json.put(ElFinderConstants.ELFINDER_PARAMETER_NETDRIVERS, new Object[0]);
            for (Volume volume : elfinderStorage.getVolumes()) {
                VolumeHandler root = new VolumeHandler(volume.getRoot(), elfinderStorage);
                files.put(root.getHash(), root);
            }
            this.buildResult(null,files,json,request);
            return;
        }

        if (tree) {
            for (Volume volume : elfinderStorage.getVolumes()) {
                VolumeHandler root = new VolumeHandler(volume.getRoot(), elfinderStorage);
                files.put(root.getHash(), root);
                addSubFolders(files, root);
            }
        }

        VolumeHandler cwd = findCwd(elfinderStorage, target);
        files.put(cwd.getHash(), cwd);
        addChildren(files, cwd);
        this.buildResult(cwd,files,json,request);
    }


    private void buildResult(VolumeHandler cwd,Map<String, VolumeHandler> files,JSONObject json,HttpServletRequest request) throws Exception{
        Object[] objects = buildJsonFilesArray(request, files.values());
        List<Object> filesList = new ArrayList<>(Arrays.asList(objects));
        json.put(ElFinderConstants.ELFINDER_PARAMETER_FILES, filesList);
        if (cwd!=null) {
            String hash = cwd.getHash();
            for(Object obj : filesList){
                HashMap<String,Object> map = (HashMap<String, Object>) obj;
                String strHash = map.get("hash").toString();
                if(Objects.equals(hash, strHash)){
                    json.put(ElFinderConstants.ELFINDER_PARAMETER_CWD,map);
                    filesList.remove(map);
                    break;
                }
            }
            json.put(ElFinderConstants.ELFINDER_PARAMETER_OPTIONS, getOptions(cwd));
        }
    }
}
