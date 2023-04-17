package cn.cxnxs.pan.core.command;


import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import cn.cxnxs.pan.core.util.HttpUtil;
import org.apache.tika.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCommand extends AbstractCommand implements ElfinderCommand {

    public static final String STREAM = "1";

    @Override
    public void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String target = request.getParameter("target");
        boolean download = STREAM.equals(request.getParameter("download"));
        VolumeHandler fsi = super.findTarget(elfinderStorage, target);
        String mime = fsi.getMimeType();

        response.setCharacterEncoding("utf-8");
        response.setContentType(mime);
        String fileName = fsi.getName();
        if (download) {
            response.setHeader("Content-Disposition",
                    "attachments; " + HttpUtil.getAttachmentFileName(fileName, request.getHeader("USER-AGENT")));
            response.setHeader("Content-Transfer-Encoding", "binary");
        }

        OutputStream out = response.getOutputStream();
        response.setContentLength((int) fsi.getSize());

        try (InputStream is = fsi.openInputStream()) {
            IOUtils.copy(is, out);
            out.flush();
            out.close();
        }
    }
}
