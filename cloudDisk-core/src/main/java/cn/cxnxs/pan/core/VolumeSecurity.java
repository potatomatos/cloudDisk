package cn.cxnxs.pan.core;


import cn.cxnxs.pan.core.impl.SecurityConstraint;

public interface VolumeSecurity {

    String getVolumePattern();

    SecurityConstraint getSecurityConstraint();

}
