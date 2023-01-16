package cn.cxnxs.pan.core.command;

import cn.cxnxs.pan.core.ElFinderConstants;
import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.VolumeHandler;
import com.alibaba.fastjson.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;

public class DimCommand extends AbstractJsonCommand implements ElfinderCommand {
    public static final String SEPARATOR = "x";

    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception {
        final String target = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_TARGET);

        BufferedImage image;
        VolumeHandler volumeHandler = findTarget(elfinderStorage, target);
        image = ImageIO.read(volumeHandler.openInputStream());

        json.put(ElFinderConstants.ELFINDER_JSON_RESPONSE_DIM, image.getWidth() + SEPARATOR + image.getHeight());
    }
}
