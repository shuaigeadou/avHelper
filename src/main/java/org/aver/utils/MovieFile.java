package org.aver.utils;

import java.io.File;

public class MovieFile {
	private static String mediaPath = "G:/aduitVideo/allmedia/";
	private static String mediaForPreview = "G:/aduitVideo/embyServerMed";
	public static void main(String[] args) {
		File serverDir = new File(mediaForPreview);
		movieFile(serverDir);
	}
	
	/**
	 * 移动所有电影
	 * @param file
	 */
	private static void movieFile(File file){
		File desFile = null;
		if(file.isDirectory()){
			for (File childFile : file.listFiles()) {
				movieFile(childFile);
			}
		}else{
			if (file.length() / 1024 / 1024 > 20) {
				System.out.println(file.getName());
				desFile = new File(mediaPath + file.getName());
				file.renameTo(desFile);
			}
		}
		
	}
}
