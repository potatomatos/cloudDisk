package cn.cxnxs.pan.core.command;


import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.core.ElfinderContext;
import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractCommand implements ElfinderCommand {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Object> options = new HashMap<>();

    protected void addChildren(Map<String, VolumeHandler> map, VolumeHandler target) throws IOException {
        List<VolumeHandler> volumeHandlers = target.listChildren();
        for (VolumeHandler f : volumeHandlers) {
            map.put(f.getHash(), f);
        }
    }

    protected void addSubFolders(Map<String, VolumeHandler> map, VolumeHandler target) throws IOException {
        List<VolumeHandler> volumeHandlers = target.listChildren();
        for (VolumeHandler f : volumeHandlers) {
            if (f.isFolder()) {
                map.put(f.getHash(), f);
//                addSubFolders(map, f);
            }
        }
    }

    protected void createAndCopy(VolumeHandler src, VolumeHandler dst) throws IOException {
        if (src.isFolder()) {
            createAndCopyFolder(src, dst);
        } else {
            createAndCopyFile(src, dst);
        }
    }

    private void createAndCopyFile(VolumeHandler src, VolumeHandler dst) throws IOException {
        dst.createFile();
        InputStream is = src.openInputStream();
        OutputStream os = dst.openOutputStream();
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }

    private void createAndCopyFolder(VolumeHandler src, VolumeHandler dst) throws IOException {
        dst.createFolder();

        for (VolumeHandler c : src.listChildren()) {
            if (c.isFolder()) {
                createAndCopyFolder(c, new VolumeHandler(dst, c.getName()));
            } else {
                createAndCopyFile(c, new VolumeHandler(dst, c.getName()));
            }
        }
    }

    @Override
    public void execute(ElfinderContext context) throws Exception {
        ElfinderStorage elfinderStorage = context.getVolumeSourceFactory().getVolumeSource();
        execute(elfinderStorage, context.getRequest(), context.getResponse());
    }

    public abstract void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, HttpServletResponse response) throws Exception;

    protected Object[] buildJsonFilesArray(HttpServletRequest request, Collection<VolumeHandler> list) throws IOException {

        ExecutorService executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(list.size());
        List<Map<String, Object>> jsonFileList = new ArrayList<>();

        for (VolumeHandler itemHandler : list) {
            executor.execute(new Task(jsonFileList,request,itemHandler,latch));
        }

        try {
            latch.await();
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jsonFileList.toArray();
    }

    protected VolumeHandler findCwd(ElfinderStorage elfinderStorage, String target) throws IOException {
        VolumeHandler cwd = null;
        if (target != null) {
            cwd = findTarget(elfinderStorage, target);
        }

        if (cwd == null) {
            cwd = new VolumeHandler(elfinderStorage.getVolumes().get(0).getRoot(), elfinderStorage);
        }

        return cwd;
    }

    protected VolumeHandler findTarget(ElfinderStorage elfinderStorage, String hash) throws IOException {
        Target target = elfinderStorage.fromHash(hash);
        if (target == null) {
            return null;
        }
        return new VolumeHandler(target, elfinderStorage);
    }

    protected List<Target> findTargets(ElfinderStorage elfinderStorage, String[] targetHashes) throws IOException {
        if (elfinderStorage != null && targetHashes != null) {
            List<Target> targets = new ArrayList<>(targetHashes.length);
            for (String targetHash : targetHashes) {
                Target target = elfinderStorage.fromHash(targetHash);
                if (target != null) {
                    targets.add(target);
                }
            }
            return targets;
        }
        return Collections.emptyList();
    }

    protected Map<String, Object> getTargetInfo(final HttpServletRequest request, final VolumeHandler target) throws Exception {
        Map<String, Object> info = new HashMap<>();
        info.put(ElFinderConstants.ELFINDER_PARAMETER_HASH, target.getHash());
        info.put(ElFinderConstants.ELFINDER_PARAMETER_MIME, target.getMimeType());
        info.put(ElFinderConstants.ELFINDER_PARAMETER_TIMESTAMP, target.getLastModified());
        info.put(ElFinderConstants.ELFINDER_PARAMETER_SIZE, target.getSize());
        info.put(ElFinderConstants.ELFINDER_PARAMETER_READ, target.isReadable() ? ElFinderConstants.ELFINDER_TRUE_RESPONSE : ElFinderConstants.ELFINDER_FALSE_RESPONSE);
        info.put(ElFinderConstants.ELFINDER_PARAMETER_WRITE, target.isWritable() ? ElFinderConstants.ELFINDER_TRUE_RESPONSE : ElFinderConstants.ELFINDER_FALSE_RESPONSE);
        info.put(ElFinderConstants.ELFINDER_PARAMETER_LOCKED, target.isLocked() ? ElFinderConstants.ELFINDER_TRUE_RESPONSE : ElFinderConstants.ELFINDER_FALSE_RESPONSE);

        if (target.getMimeType() != null && target.getMimeType().startsWith("image")) {
            info.put(ElFinderConstants.ELFINDER_PARAMETER_THUMBNAIL, target.getTmb(request.getRequestURI()));
        }

        if (target.isRoot()) {
            info.put(ElFinderConstants.ELFINDER_PARAMETER_DIRECTORY_FILE_NAME, target.getVolumeAlias());
            info.put(ElFinderConstants.ELFINDER_PARAMETER_VOLUME_ID, target.getVolumeId());
            info.put(ElFinderConstants.ELFINDER_PARAMETER_ICON, target.getIcon());
            info.put(ElFinderConstants.ELFINDER_PARAMETER_SOURCE, target.getSource());
            info.put(ElFinderConstants.ELFINDER_PARAMETER_EXTINFO, target.getExtInfo());
        } else {
            info.put(ElFinderConstants.ELFINDER_PARAMETER_DIRECTORY_FILE_NAME, target.getName());
            info.put(ElFinderConstants.ELFINDER_PARAMETER_PARENTHASH, target.getParent().getHash());
        }
        return info;
    }

    protected Map<String, Object> getOptions(VolumeHandler cwd) {
        String[] emptyArray = {};
        options.put(ElFinderConstants.ELFINDER_PARAMETER_PATH, cwd.getName());
        options.put(ElFinderConstants.ELFINDER_PARAMETER_COMMAND_DISABLED, emptyArray);
        options.put(ElFinderConstants.ELFINDER_PARAMETER_FILE_SEPARATOR, ElFinderConstants.ELFINDER_PARAMETER_FILE_SEPARATOR);
        options.put(ElFinderConstants.ELFINDER_PARAMETER_OVERWRITE_FILE, ElFinderConstants.ELFINDER_TRUE_RESPONSE);
        options.put(ElFinderConstants.ELFINDER_VOLUME_ALIAS, cwd.getVolumeAlias());
        options.put(ElFinderConstants.ELFINDER_PARAMETER_VOLUME_ID, cwd.getVolumeId());
//        options.put(ElFinderConstants.ELFINDER_PARAMETER_ARCHIVERS, ArchiverOption.JSON_INSTANCE());
        return options;
    }

    class Task implements Runnable{

        private final HttpServletRequest request;

        private final VolumeHandler itemHandler;

        private final List<Map<String, Object>> jsonFileList;

        private final CountDownLatch downLatch;

        public Task (List<Map<String, Object>> jsonFileList,HttpServletRequest request,VolumeHandler itemHandler,CountDownLatch downLatch){
            this.jsonFileList = jsonFileList;
            this.request = request;
            this.itemHandler = itemHandler;
            this.downLatch = downLatch;
        }

        @Override
        public void run()  {
            try {
                jsonFileList.add(getTargetInfo(request, itemHandler));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                downLatch.countDown();
            }
        }
    }
}
