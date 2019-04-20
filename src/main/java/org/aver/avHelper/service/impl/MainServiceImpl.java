package org.aver.avHelper.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.aver.avHelper.service.MainService;
import org.aver.avHelper.utils.HtmlDownload;
import org.aver.avHelper.utils.XmlHandler;
import org.aver.avHelper.vo.Config;
import org.aver.avHelper.vo.ConfigStatic;
import org.aver.avHelper.vo.Movie;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class MainServiceImpl implements MainService {
	
	@Override
	public List<Movie> generateConfig() throws IOException {
		//所有文件名
		String[] names = new File(ConfigStatic.config.getTempMediaDir()).list();
		//用来保存可识别影片信息
		List<Movie> movieList = new ArrayList<Movie>();
		Movie movie = null;
		for (String name : names) {
			movie = new Movie();
			movie.setOriginalName(name);
			movie.setNewName(name);
			movie.setSorttitle(name.substring(0, name.lastIndexOf(".")));
			movie.setWebSite(ConfigStatic.javBusSite + name.substring(0, name.lastIndexOf(".")));
			movieList.add(movie);
		}
		XmlHandler.generateMovies(movieList, ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName, true);
		return movieList;
	}
	
	@Override
	public List<Movie> generateRenameConfig() throws IOException {
		//所有文件名
		String[] names = new File(ConfigStatic.config.getTempMediaDir()).list();
		//用来保存可识别影片信息
		List<Movie> movieList = new ArrayList<Movie>();
		//用来保存不可识别影片信息
		List<Movie> errMovieList = new ArrayList<Movie>();
		//key为番号，值为影片，用于判断一个电影是否拆分多个文件
		Map<String, Movie> map = new HashMap<String, Movie>();

		// 番号正则
		String nameRegEx = "[a-z]+[0-9]+";
		String numRegEx = "[0-9]+";

		// 忽略大小写的写法
		Pattern namePattern = Pattern.compile(nameRegEx, Pattern.CASE_INSENSITIVE);
		Pattern numPattern = Pattern.compile(numRegEx, Pattern.CASE_INSENSITIVE);
		
		//循环使用的临时变量
		Matcher nameMat = null;
		Matcher numMat = null;
		Movie movie = null;
		String movieExtName = null;
		// 根据正则生成新文件名
		for (String name : names) {
			movie = new Movie();
			movie.setOriginalName(name);

			// 影片扩展名
			movieExtName = name.substring(name.lastIndexOf(".") + 1);
			nameMat = namePattern.matcher(name.replaceAll("-", ""));
			// 影片名可以识别
			if (nameMat.find()) {
				// 排除掉番号数字前面的0
				String matString = nameMat.group();
				numMat = numPattern.matcher(matString);
				//必然成立，group前必须执行，否则报错
				numMat.find();
				String num = numMat.group();
				String actualNum = num;
				if (num.length() >= 5) {
					if (actualNum.indexOf("0") == 0) {
						actualNum = actualNum.substring(1);
					}
					if (actualNum.indexOf("0") == 0) {
						actualNum = actualNum.substring(1);
					}
				}
				// 没有0或数字个数不为5的，不做特殊处理
				String shortName = (matString.replace(num, "") + "-" + actualNum).toUpperCase();
				
				//处理一个电影多个文件的情况
				Movie parentMovie = map.get(shortName);
				if(parentMovie == null){
					map.put(shortName, movie);
					movie.setSorttitle(shortName);
					movie.setWebSite(ConfigStatic.javBusSite + shortName);
					movie.setNewName(shortName + "." + movieExtName);
					movieList.add(movie);
				}else{
					String parentName = parentMovie.getNewName();
					if(parentMovie.getChildrenMovies() == null){
						parentMovie.setChildrenMovies(new ArrayList<Movie>());
						parentMovie.setNewName(parentName.substring(0, parentName.lastIndexOf("."))
								+ ConfigStatic.rule[0] + parentName.substring(parentName.lastIndexOf(".")));
					}
					movie.setNewName(
							shortName + ConfigStatic.rule[parentMovie.getChildrenMovies().size() + 1]
									+ "." + movieExtName);
					parentMovie.getChildrenMovies().add(movie);
				}
			}
			//影片不能识别
			else{
				errMovieList.add(movie);
			}
		}

		movieList.addAll(errMovieList);
		XmlHandler.generateMovies(movieList, ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName, true);
		return movieList;
	}

	@Override
	public void synchronizeNewName(List<Movie> movies) throws Exception {
		List<Movie> movieList = XmlHandler.readMovies(ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName);
		for (int i = 0; i < movieList.size(); i++) {
			if(!StringUtils.equals(movieList.get(i).getOriginalName(), movies.get(i).getOriginalName())){
				throw new RuntimeException("数据对不上，请重新生成改名配置");
			}
			String newName = movies.get(i).getNewName();
			String shortName = newName.substring(0, newName.lastIndexOf(".")).toUpperCase();
			String extendName = newName.substring(newName.lastIndexOf(".") + 1).toLowerCase();
			movieList.get(i).setNewName(shortName + "." + extendName);
			movieList.get(i).setSorttitle(shortName);
			movieList.get(i).setWebSite(ConfigStatic.javBusSite + shortName);
		}
		XmlHandler.generateMovies(movieList, ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName, false);
	}

	@Override
	public List<Movie> getMovieMessage() throws Exception {
		List<Movie> movieList = XmlHandler.readMovies(ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName);
		if (movieList == null) {
			throw new RuntimeException("请先生成配置文件");
		}

		// 多线程代码
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				ConfigStatic.config.getThreadSize(), ConfigStatic.config.getThreadSize(), 60000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		CopyOnWriteArrayList<Movie> movieListForSite = new CopyOnWriteArrayList<Movie>();
		CopyOnWriteArrayList<Movie> errorMovieListForSite = new CopyOnWriteArrayList<Movie>();
		GetMovieMsgTask getMovieMsgTask = null;
		for (Movie movie : movieList) {
			getMovieMsgTask = new GetMovieMsgTask(movie, movieListForSite, errorMovieListForSite);
			executor.execute(getMovieMsgTask);
		}
		executor.shutdown();

		while (true) {
			if (executor.isTerminated()) {
				movieListForSite.addAll(errorMovieListForSite);
				XmlHandler.generateMovies(
						movieListForSite, ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName, true);
				return movieListForSite;
			}
			Thread.sleep(1000);
		}
	}

	@Override
	public void reName() throws Exception {
		List<Movie> movieList = XmlHandler.readMovies(ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName);
		if (movieList == null) {
			throw new RuntimeException("请先生成配置文件");
		}
		
		File originalFile = null;
		File newFile = null;
		String tempMediaDir = ConfigStatic.config.getTempMediaDir();
		for (Movie movie : movieList) {
			originalFile = new File(tempMediaDir + "/" + movie.getOriginalName());
			newFile = new File(tempMediaDir + "/" + movie.getNewName());
			if (originalFile.exists()) {
				originalFile.renameTo(newFile);
			}
			if (movie.getChildrenMovies() != null && movie.getChildrenMovies().size() > 0) {
				for (Movie childMovie : movie.getChildrenMovies()) {
					originalFile = new File(tempMediaDir + "/" + childMovie.getOriginalName());
					newFile = new File(tempMediaDir + "/" + childMovie.getNewName());
					if (originalFile.exists()) {
						originalFile.renameTo(newFile);
					}
				}
			}
		}
	}

	@Override
	public void downLoadImg(String desDir) throws Exception {
		List<Movie> movieList = XmlHandler.readMovies(ConfigStatic.tempRootPath, ConfigStatic.moviesXmlName);
		if (movieList == null) {
			throw new RuntimeException("请先生成配置文件");
		}

		// 多线程代码
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				ConfigStatic.config.getThreadSize(),ConfigStatic.config.getThreadSize(), 60000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		DownLoadImgTask downLoadImgTask = null;
		for (Movie movie : movieList) {
			downLoadImgTask = new DownLoadImgTask(movie, ConfigStatic.config.getThreadSize(), desDir);
			executor.execute(downLoadImgTask);
		}
		executor.shutdown();

		while (true) {
			if (executor.isTerminated()) {
				break;
			}
			Thread.sleep(1000);
		}

	}

	@Override
	public void moveFiles(String sourceDir, String desDir) {
		File sourceDirFile = new File(sourceDir);
		movieFile(sourceDirFile, desDir + "/");		
	}
	/**
	 * 移动所有大于20M的文件到指定文件夹
	 * @param sourceDir
	 * @param desDir
	 */
	private static void movieFile(File sourceDir, String desDir){
		File desFile = null;
		if(sourceDir.isDirectory()){
			for (File childFile : sourceDir.listFiles()) {
				movieFile(childFile, desDir);
			}
		}else{
			if (sourceDir.length() / 1024 / 1024 > 20) {
				desFile = new File(desDir + sourceDir.getName());
				sourceDir.renameTo(desFile);
			}
		}
		
	}

	@Override
	public void compareDir(String retainDir, String deleteDir) {
		Map<String, File> actorNames = new HashMap<String, File>();
		File dirForWatch = new File(retainDir);
		for (File file : dirForWatch.listFiles()) {
			actorNames.put(file.getName(), file);
		}
		
		File dirForPreview = new File(deleteDir);
		File watActorDir = null;
		for (File preActorDir : dirForPreview.listFiles()) {
			watActorDir = actorNames.get(preActorDir.getName());
			if(watActorDir != null){
				for (File preShortNameDir : preActorDir.listFiles()) {
					for (File watShortNameDir : watActorDir.listFiles()) {
						if(preShortNameDir.getName().equals(watShortNameDir.getName())){
							delteDir(preShortNameDir);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param dir
	 */
	private static void delteDir(File dir){
		for (File file : dir.listFiles()) {
			if(file.isDirectory()){
				delteDir(file);
			} else {
				if (file.length() / 1024 / 1024 < 20) {
					if(!file.delete()){
						throw new RuntimeException("删除失败：" + file.getAbsolutePath());
					}
				}else{
					throw new RuntimeException("存在电影文件：" + file.getAbsolutePath());
				}
			}
		}
		dir.delete();
	}

	@Override
	public void generatePseudoVideo(String urlName) throws IOException{
		Config config = ConfigStatic.config;
		
		//请求参数
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		properties.put("Cookie", "existmag=all");
		int i = 1;
		while (true) {
			if (config.getMaxDownloadNum() !=null && i > config.getMaxDownloadNum()) break;
			Document document = HtmlDownload.getDocBySite(urlName + "/" + i, properties);
			Elements imgEles = document.select("#waterfall a.movie-box");
			if (imgEles.size() == 0) break;
			Iterator<Element> imgElesIt = imgEles.iterator();
			while (imgElesIt.hasNext()) {
				Element movieBoxEle = imgElesIt.next();
				String picName = movieBoxEle.selectFirst("date").text() + ".wmv";
				File picFile = new File(config.getTempMediaDir() + "/" + picName);
				picFile.createNewFile();
			}
			i++;
		}
	}
	
}
