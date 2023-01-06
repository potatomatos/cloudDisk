package cn.cxnxs.pan.core.service;


import cn.cxnxs.pan.core.core.Target;
import cn.cxnxs.pan.core.core.Volume;
import cn.cxnxs.pan.core.core.VolumeSecurity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public interface ElfinderStorage {

    Target fromHash(String hash);

    String getHash(Target target) throws IOException;

    String getVolumeId(Volume volume);

    Locale getVolumeLocale(Volume volume);

    VolumeSecurity getVolumeSecurity(Target target);

    List<Volume> getVolumes();

    List<VolumeSecurity> getVolumeSecurities();

    ThumbnailWidth getThumbnailWidth();
}
