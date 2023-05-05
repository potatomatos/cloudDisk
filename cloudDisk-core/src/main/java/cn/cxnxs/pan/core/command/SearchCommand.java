package cn.cxnxs.pan.core.command;

import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.core.VolumeSecurity;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchCommand extends AbstractJsonCommand implements ElfinderCommand {

    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) {

        final String query = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_SEARCH_QUERY);

        try {
            List<Object> objects = new ArrayList<>();
            List<Volume> volumes = elfinderStorage.getVolumes();
            ExecutorService executor = Executors.newCachedThreadPool();

            for (Volume volume : volumes) {

                // checks volume security
                Target volumeRoot = volume.getRoot();
                VolumeSecurity volumeSecurity = elfinderStorage.getVolumeSecurity(volumeRoot);

                // search only in volumes that are readable
                if (volumeSecurity.getSecurityConstraint().isReadable()) {

                    // search for targets
                    List<Target> targets = volume.search(query);

                    if (targets != null) {
                        CountDownLatch latch = new CountDownLatch(targets.size());
                        for (Target target : targets) {
                            executor.submit(() -> {
                                try {
                                    objects.add(getTargetInfo(request, new VolumeHandler(target, elfinderStorage)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    latch.countDown();
                                }
                            });
                        }
                        latch.await();
                    }
                }
            }
            executor.shutdown();

            Object[] objectArray = objects.toArray();
            json.put(ElFinderConstants.ELFINDER_PARAMETER_FILES, objectArray);

        } catch (Exception e) {
            json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_ERROR, "Unable to search! Error: " + e);
        }
    }
}
