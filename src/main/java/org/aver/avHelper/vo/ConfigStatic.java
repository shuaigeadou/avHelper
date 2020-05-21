package org.aver.avHelper.vo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

public class ConfigStatic {
	
	/** javbus影片信息下载地址 */
	public static final String javBusSite = "https://www.javbus.com/";
	/** mgstage影片信息下载地址 */
	public static final String mgstageSite = "https://www.mgstage.com/product/product_detail/";
	/** 保存影片信息的目录 */
	public static final String tempRootPath = "./temp/";
	/** 影片信息XML文件 */
	public static final String moviesXmlName = "movies.xml";
	/** 多个媒体文件的后缀规则 */
	public static final char[] rule = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	/** 日期格式 */
	public static final String dateFormatString = "yyyy-MM-dd";
	/** 可变配置路径 */
	public static final String configPath = "./config.xml";
	/** javbus类别对照*/
	public static Map<String, String> languageDict;
	public static final String languageDictPath = "./javbus类别对照.xml";
	/** 可变配置 */
	public static Config config;
	
	static{
		config = readConfig();
		readLanguageDict();
	}
	
	/**
	 * 保存配置到xml
	 */
	public static void saveConfig(){
		XStream xStream = new XStream();
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(configPath), "utf-8");
			xStream.toXML(config, osw);
			osw.flush();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取配置
	 * @return
	 */
	public static Config readConfig(){
		File file = new File(configPath);
		if(!file.exists()) return new Config();
		
		XStream xStream = new XStream();
		InputStreamReader isr = null;
		Config config = new Config();
		try {
			isr = new InputStreamReader(new FileInputStream(file), "utf-8");
			config = (Config) xStream.fromXML(isr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}
	
	/**
	 * 读取对照
	 * @return
	 */
	public static void readLanguageDict(){
		File file = new File(languageDictPath);
		if(!file.exists()) {
			languageDict = null;
			return;
		}
		
		XStream xStream = new XStream();
		try {
			languageDict = (Map<String, String>) xStream.fromXML(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Config c = readConfig();
		System.out.println(c);
	}
}
