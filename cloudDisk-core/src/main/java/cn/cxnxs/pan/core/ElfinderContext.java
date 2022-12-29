package cn.cxnxs.pan.core;


import cn.cxnxs.pan.service.ElfinderStorageFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ElfinderContext {

    ElfinderStorageFactory getVolumeSourceFactory();

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

}
