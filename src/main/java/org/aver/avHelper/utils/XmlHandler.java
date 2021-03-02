package org.aver.avHelper.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.aver.avHelper.vo.Movie;

import com.thoughtworks.xstream.XStream;

/**
 * 读写XML并处理Movie对象的集合
 * 
 * @author Administrator
 *
 */
public class XmlHandler {

	/**
	 * 将影片信息以XML格式保存到指定位置
	 * @param movieList
	 * @param directory
	 * @param fileName
	 * @param isBackup
	 * @throws IOException
	 */
	public static void generateMovies(List<Movie> movieList, String directory, String fileName, boolean isBackup)
			throws IOException {
		File directoryFile = new File(directory);
		if(!directoryFile.exists()){
			directoryFile.mkdirs();
		}
		// 备份已有的影片信息
		if (isBackup) {
			File moviesXmlFile = new File(directory + fileName);
			if (moviesXmlFile.exists()) {
				File movieRootDir = new File(directory);

				// 主文件名和扩展名
				String mainName = fileName.substring(0, fileName.lastIndexOf("."));
				// 备份文件名的正则
				String backupNameRegEx = mainName + "[0-9]+" + ".xml";
				// 忽略大小写的写法
				Pattern backupNamePattern = Pattern.compile(backupNameRegEx, Pattern.CASE_INSENSITIVE);
				// 最大编号
				int maxNo = 0;

				for (String name : movieRootDir.list()) {
					Matcher backupNameMat = backupNamePattern.matcher(name);
					if (backupNameMat.find()) {
						String curBackupName = backupNameMat.group();
						String curNumStr = curBackupName.replace(mainName, "").replace(".xml", "");
						Integer curNum = Integer.valueOf(curNumStr);
						if (curNum >= maxNo) {
							maxNo = curNum;
						}
					}
				}

				// 备份
				System.gc();
				FileUtils.moveFile(moviesXmlFile, new File(directory + mainName + (maxNo + 1) + ".xml"));
			}
		}
		
		if(movieList.size() == 0) return;
		
		XStream xStream = new XStream();
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(directory + fileName), "utf-8");
		xStream.toXML(movieList, osw);
		osw.flush();
		osw.close();
//		// 创建XML文档及根节点
//		Document document = DocumentHelper.createDocument();
//		Element movies = document.addElement("movies");
//		// 创建循环用到的临时变量
//		Element movie = null;
//		Element originalName = null;
//		Element newName = null;
//		Element webSite = null;
//
//		// 设置所有影片信息节点
//		for (Movie m : movieList) {
//			movie = movies.addElement("movie");
//			originalName = movie.addElement("originalName");
//			newName = movie.addElement("newName");
//			originalName.setText(m.getOriginalName() == null ? "" : m.getOriginalName());
//			newName.setText(m.getNewName() == null ? "" : m.getNewName());
//			webSite = movie.addElement("webSite");
//			webSite.setText(m.getWebSite() == null ? "" : m.getWebSite());
//		}
//
//		// 保存
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		XMLWriter writer = new XMLWriter(new FileOutputStream(directory + fileName), format);
//		// 设置是否转义。默认true，代表转义
//		writer.setEscapeText(false);
//		writer.write(document);
//		writer.close();
	}

	public static List<Movie> readMovies(String directory, String fileName) throws Exception{
		File file = new File(directory + fileName);
		return readMovies(file);
		
	}
	
	/**
	 * 从配置文件中读取影片内容
	 * @param sourceFile
	 * @return
	 * @throws Exception 
	 */
	public static List<Movie> readMovies(File sourceFile) throws Exception {
		if(!sourceFile.exists()) return null;
		
		XStream xStream = new XStream();
		xStream.setClassLoader(Movie.class.getClassLoader());
		InputStreamReader isr = new InputStreamReader(new FileInputStream(sourceFile), "utf-8");
		List<Movie> movieList = (List<Movie>) xStream.fromXML(isr);
		isr.close();
		return movieList;
//		// 从文件中读取
//		SAXReader reader = new SAXReader();
//		Document document = reader.read(sourceFile);
//		Element moviesEle = document.getRootElement();
//
//		// 循环所有节点，并装入到List中
//		Iterator<Element> rootIt = moviesEle.elementIterator();
//		List<Movie> movieList = new ArrayList<Movie>();
//
//		// 循环中使用的临时变量
//		Movie curMovie = null;
//		Element curMovieEle = null;
//		Iterator<Element> curMovieIt = null;
//		Element curMovieMsgEle = null;
//		while (rootIt.hasNext()) {
//			curMovie = new Movie();
//			curMovieEle = rootIt.next();
//			curMovieIt = curMovieEle.elementIterator();
//			while (curMovieIt.hasNext()) {
//				curMovieMsgEle = curMovieIt.next();
//				if ("originalName".equals(curMovieMsgEle.getName())) {
//					curMovie.setOriginalName(curMovieMsgEle.getStringValue());
//				} else if ("newName".equals(curMovieMsgEle.getName())) {
//					curMovie.setNewName(curMovieMsgEle.getStringValue());
//				} else if ("webSite".equals(curMovieMsgEle.getName())) {
//					curMovie.setWebSite(curMovieMsgEle.getStringValue());
//				}
//			}
//			movieList.add(curMovie);
//		}
//		return movieList;
	}
	
}
