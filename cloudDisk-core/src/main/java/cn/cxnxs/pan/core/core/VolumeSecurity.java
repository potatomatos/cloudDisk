package cn.cxnxs.pan.core.core;


import cn.cxnxs.pan.core.core.impl.SecurityConstraint;

public interface VolumeSecurity {

    String getVolumePattern();

    SecurityConstraint getSecurityConstraint();

}
