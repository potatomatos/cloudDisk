package cn.cxnxs.pan.service;

import cn.cxnxs.pan.configuration.ElfinderConfigurationUtils;
import cn.cxnxs.pan.core.Volume;
import cn.cxnxs.pan.core.VolumeBuilder;
import cn.cxnxs.pan.core.impl.NIO2FileSystemVolume;
import cn.cxnxs.pan.exception.VolumeSourceException;

import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;

/**
 * Volume Sources supported.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public enum VolumeSources {

    FILESYSTEM {
        @Override
        public VolumeBuilder<?> getVolumeBuilder(String alias, String path) {
            return NIO2FileSystemVolume.builder(alias, Paths.get(ElfinderConfigurationUtils.toURI(path)));
        }
    },


//    ,DROPBOX, GOOGLEDRIVE, ONEDRIVE, ICLOUD
    ;

    public static VolumeSources of(String source) {
        if (source != null) {

            final String notLetterRegex = "[^\\p{L}]";
            final String whitespaceRegex = "[\\p{Z}]";
            final String notAsciiCharactersRegex = "[^\\p{ASCII}]";
            final String emptyString = "";

            source = Normalizer.normalize(source, Normalizer.Form.NFD);
            source = source.replaceAll(notLetterRegex, emptyString);
            source = source.replaceAll(whitespaceRegex, emptyString);
            source = source.replaceAll(notAsciiCharactersRegex, emptyString);
            source = source.trim().toUpperCase();

            for (VolumeSources volumesource : values()) {
                if (volumesource.name().equalsIgnoreCase(source)) {
                    return volumesource;
                }
                throw new VolumeSourceException("Volume source not supported! The supported volumes sources are: " + Arrays.deepToString(values()).toLowerCase());
            }
        }
        throw new VolumeSourceException("Volume source not informed in elfinder configuration xml!");
    }

    public Volume newInstance(String alias, String path) {
        if (path == null || path.trim().isEmpty())
            throw new VolumeSourceException("Volume source path not informed");
        return getVolumeBuilder(alias, path).build();
    }

    public abstract VolumeBuilder<?> getVolumeBuilder(String alias, String path);
}
