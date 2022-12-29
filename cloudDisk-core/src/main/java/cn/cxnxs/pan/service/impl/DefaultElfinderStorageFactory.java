package cn.cxnxs.pan.service.impl;


import cn.cxnxs.pan.service.ElfinderStorage;
import cn.cxnxs.pan.service.ElfinderStorageFactory;

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
