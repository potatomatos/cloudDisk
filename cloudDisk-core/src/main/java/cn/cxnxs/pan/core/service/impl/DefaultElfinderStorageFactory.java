package cn.cxnxs.pan.core.service.impl;


import cn.cxnxs.pan.core.service.ElfinderStorage;
import cn.cxnxs.pan.core.service.ElfinderStorageFactory;

public class DefaultElfinderStorageFactory implements ElfinderStorageFactory {

    private ElfinderStorage elfinderStorage;

    @Override
    public ElfinderStorage getVolumeSource() {
        return elfinderStorage;
    }

    public void setElfinderStorage(ElfinderStorage elfinderStorage) {
        this.elfinderStorage = elfinderStorage;
    }
}
