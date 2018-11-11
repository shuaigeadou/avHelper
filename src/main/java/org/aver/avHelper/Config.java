package org.aver.avHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class Config {
	/** 配置文件配置 */
	public static String localMoviePath;
	/** 配置文件配置 */
	public static String serverMediaPath;
	/** 配置文件配置 */
	public static String previewMediaPath;
	/** 配置文件配置 */
	public static Integer maxActorNum;
	/** 配置文件配置 */
	public static String genreExclude;
	/** 配置文件配置 */
	public static String appointUrl;
	/** 配置文件配置 */
	public static int threadSize;
	/** 配置文件配置 */
	public static boolean downloadFanart;
	/** 配置文件配置 */
	public static Date startDate;
	/** 配置文件配置 */
	public static Date endDate;
	
	/** 影片信息下载地址 */
	public static String javBusSite = "https://www.javbus.com/";
	/** 保存影片信息的目录 */
	public static String movieRootPath = "./";
	/** 影片信息XML文件 */
	public static String moviesXmlName = "movies.xml";
	/** 没有正确处理的影片信息XML文件 */
	public static String errorMoviesXmlName = "errorMovies.xml";
	/** 多个媒体文件的后缀规则 */
	public static char[] rule = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	/** 日期格式 */
	public static final String dateFormatString = "yyyy-MM-dd";
	
	/**
	 * 读取配置
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public static void readConfig() throws FileNotFoundException, IOException, ParseException {
		Properties properties = new Properties();
		// 使用InPutStream流读取properties文件
		File file = new File("./config.properties");
		//日期格式
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
		properties.load(new InputStreamReader(new FileInputStream(file), "utf-8"));
		// 获取key对应的value值
		localMoviePath = properties.getProperty("localMoviePath");
		serverMediaPath = properties.getProperty("serverMediaPath");
		previewMediaPath = properties.getProperty("previewMediaPath");
		appointUrl = properties.getProperty("appointUrl");
		threadSize = Integer.valueOf(properties.getProperty("threadSize"));
		String maxNum = properties.getProperty("maxActorNum");
		maxActorNum = StringUtils.isBlank(maxNum) ? null : Integer.valueOf(maxNum);
		genreExclude = properties.getProperty("genreExclude");
		downloadFanart = StringUtils.equals("yes", properties.getProperty("downloadFanart"));
		String startDateString = properties.getProperty("startDate");
		String endDateString = properties.getProperty("endDate");
		if(StringUtils.isNotBlank(startDateString)){
			startDate = sdf.parse(startDateString);
		}
		if(StringUtils.isNotBlank(endDateString)){
			endDate = sdf.parse(endDateString);
		}
		
		String proxyHost = properties.getProperty("proxyHost");
		int proxyPort = Integer.valueOf(properties.getProperty("proxyPort"));
		String proxyName = properties.getProperty("proxyName");
		String proxyPwd = properties.getProperty("proxyPwd");
		HttpClientUtil.setVariables(proxyHost, proxyPort, proxyName, proxyPwd);
	}
	
}
