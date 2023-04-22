package cn.cxnxs.pan.core.util;

import org.apache.commons.codec.binary.Base64;

/**
 * 字符串hash操作帮助类
 * @author wayn
 *
 */
public class HashesUtil {
	private static final String[][] ESCAPES = {{"+", "_P"}, {"-", "_M"}, {"/", "_S"}, {".", "_D"}, {"=", "_E"}};

	public static String encode(String source) {
		String hash = new String(Base64.encodeBase64(source.getBytes()));
        for (String[] pair : ESCAPES) {
        	hash = hash.replace(pair[0], pair[1]);
        }
		return hash;
	}

	public static String decode(String hash) {
		for (String[] pair : ESCAPES) {
			hash = hash.replace(pair[1], pair[0]);
        }
		return new String(Base64.decodeBase64(hash));
	}

	public static String getParentFolderPath(String folderPath) {
		String[] folders = folderPath.split("/");
		// 根目录没有上级文件夹
		if (folders.length <= 1) {
			return null;
		}
		if (folders.length==2){
			return "/";
		}
		StringBuilder parentPath = new StringBuilder();
		for (int i = 0; i < folders.length - 1; i++) {
			parentPath.append(folders[i]);
			if (i < folders.length - 2) {
				parentPath.append("/");
			}
		}
		return parentPath.toString();
	}
	public static void main(String[] args) {
		System.out.println(getParentFolderPath("/A"));
	}
}
