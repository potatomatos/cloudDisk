package cn.cxnxs.pan.core.service.impl;

import cn.cxnxs.pan.core.service.ThumbnailWidth;

public class DefaultThumbnailWidth implements ThumbnailWidth {

    private int thumbnailWidth;

    @Override
    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }
}
