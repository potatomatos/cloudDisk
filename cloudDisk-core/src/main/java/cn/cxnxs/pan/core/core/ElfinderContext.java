package cn.cxnxs.pan.core.core;


import cn.cxnxs.pan.core.service.ElfinderStorageFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ElfinderContext {

    ElfinderStorageFactory getVolumeSourceFactory();

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

}
