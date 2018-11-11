package org.aver.avHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class DownLoadImgTask implements Runnable {
	private Movie movie;
	private boolean isMovieFile;
	private int threadSize = 30;
	
	/**
	 * 下载图片并移动文件
	 * @param movie
	 * @param isMovieFile	是否只移动文件
	 */
	public DownLoadImgTask(Movie movie, boolean isMovieFile) {
		this.movie = movie;
		this.isMovieFile = isMovieFile;
	}

	@Override
	public void run() {
		if(StringUtils.isBlank(movie.getTitle())){
			return;
		}
		
		// 排除指定类别
		if (StringUtils.isNoneBlank(Config.genreExclude) && movie.getGenre() != null) {
			String[] genreExclude = Config.genreExclude.split(",");
			for (String s : genreExclude) {
				if (movie.getGenre().contains(s)) {
					return;
				}
			}
		}
		// 判断影片人数
		if (Config.maxActorNum != null && movie.getActorName() != null
				&& movie.getActorName().size() > Config.maxActorNum) {
			return;
		}
		// 判断发行起止日期
		if(StringUtils.isNotBlank(movie.getReleasedate())){
			Date releaseDate = null;
			SimpleDateFormat sdf = new SimpleDateFormat(Config.dateFormatString);
			try {
				releaseDate = sdf.parse(movie.getReleasedate());
			} catch (ParseException e) {
				System.out.println(movie.getNewName() + "日期转换错误。发行日期条件失效。");
			}
			if(releaseDate != null){
				if(Config.startDate != null && releaseDate.getTime() < Config.startDate.getTime()){
					return;
				}
				if(Config.endDate != null && Config.endDate.getTime() < releaseDate.getTime()){
					return;
				}
			}
		}
		
		//以未知、多人、演员名进行分类，首先创建演员文件夹
		String actorDirName = null;
		if(movie.getActorName() == null || movie.getActorName().size() == 0){
			actorDirName = "未知";
		}else if(movie.getActorName().size() == 1){
			actorDirName = movie.getActorName().get(0);
		}else if(movie.getActorName().size() <= 4){
			actorDirName = movie.getActorName().size() + "人";
		}else if(movie.getActorName().size() <= 8){
			actorDirName = "5-8人";
		}else{
			actorDirName = "多人";
		}
		File actorDir = new File(Config.serverMediaPath + "/" + actorDirName);
		if(!actorDir.exists()){
			this.makeDir(actorDir);
		}
		
		String movieDirPath = Config.serverMediaPath + "/" + actorDirName + "/" + movie.getSorttitle();
		//创建番号文件夹
		new File(movieDirPath).mkdir();
		
		if(!isMovieFile){
			//创建info文件
			Element tempEle = null;
			Document document = DocumentHelper.createDocument();
			Element movieEle = document.addElement("movie");
			tempEle = movieEle.addElement("lockdata");
			tempEle.setText("true");
			tempEle = movieEle.addElement("title");
			tempEle.setText(movie.getTitle());
			tempEle = movieEle.addElement("sorttitle");
			tempEle.setText(movie.getSorttitle());
			if(StringUtils.isNotBlank(movie.getDirector())){
				tempEle = movieEle.addElement("director");
				tempEle.setText(movie.getDirector());
			}
			if(StringUtils.isNotBlank(movie.getYear())){
				tempEle = movieEle.addElement("year");
				tempEle.setText(movie.getYear());
			}
			if(StringUtils.isNotBlank(movie.getReleasedate())){
				tempEle = movieEle.addElement("releasedate");
				tempEle.setText(movie.getReleasedate());
			}
			if(StringUtils.isNotBlank(movie.getStudio())){
				tempEle = movieEle.addElement("studio");
				tempEle.setText(movie.getStudio());
			}
			if(StringUtils.isNotBlank(movie.getSeries())){
				tempEle = movieEle.addElement("set");
				tempEle.setText(movie.getSeries());
				tempEle = movieEle.addElement("genre");
				tempEle.setText(movie.getSeries());
			}
			if(movie.getGenre() != null && movie.getGenre().size() > 0){
				for (String genre : movie.getGenre()) {
					tempEle = movieEle.addElement("genre");
					tempEle.setText(genre);
				}
			}
			if(movie.getActorName() != null && movie.getActorName().size() > 0){
				for (String name : movie.getActorName()) {
					tempEle = movieEle.addElement("actor");
					Element nameEle = tempEle.addElement("name");
					nameEle.setText(name);
					Element typeEle = tempEle.addElement("type");
					typeEle.setText("Actor");
				}
			}
			tempEle = movieEle.addElement("website");
			tempEle.setText(movie.getWebSite());
			
			// 保存
			XMLWriter writer = null;
			try {
				OutputFormat format = OutputFormat.createPrettyPrint();
				String infoName = movie.getNewName().substring(0, movie.getNewName().lastIndexOf("."));
				String infoPath = movieDirPath + "/" + infoName + ".nfo";
				writer = new XMLWriter(new FileOutputStream(infoPath), format);
				// 设置是否转义。默认true，代表转义
				writer.setEscapeText(false);
				writer.write(document);
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//视频文件
		File movieFile = new File(Config.localMoviePath + "/" + movie.getNewName());
		//移动视频文件
		if(movieFile.exists()){
			File movieDirFile = new File(movieDirPath);
			boolean couldMoveFile = true;
			for (String s : movieDirFile.list()) {
				if(s.lastIndexOf(".") == -1) continue;
				String ext = s.substring(s.lastIndexOf(".") + 1);
				String name = s.substring(0, s.lastIndexOf("."));
				if (!StringUtils.equalsIgnoreCase(ext, "nfo") 
						&& (StringUtils.equalsIgnoreCase(name, movie.getSorttitle())
								|| StringUtils.equalsIgnoreCase(name, movie.getSorttitle() + Config.rule[0]))) {
					File serverMovieFile = new File(movieDirPath + "/" + s);
					if (serverMovieFile.length() / 1024 / 1024 < 50) {
						try {
							FileUtils.forceDelete(serverMovieFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						couldMoveFile = false;
					}
				}
			}
			if(couldMoveFile){
				try {
					FileUtils.moveFile(movieFile, new File(movieDirPath + "/" + movie.getNewName()));
					if(movie.getChildrenMovies() != null && movie.getChildrenMovies().size() > 0){
						File childFile = null;
						File childNewFile = null;
						for (Movie child : movie.getChildrenMovies()) {
							childFile = new File(Config.localMoviePath + "/" + child.getNewName());
							childNewFile = new File(movieDirPath + "/" + child.getNewName());
							FileUtils.moveFile(childFile, childNewFile);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(!isMovieFile){
			//下载图片到对应的文件夹
			// 图片扩展名
			String tempPicSite = movie.getPosterPicSite();
			String tempExtName = tempPicSite.substring(tempPicSite.lastIndexOf("."));
			String tempLocalPath = movieDirPath + "/fanart" + tempExtName;
			File fanartFile = new File(tempLocalPath);
			if(neededToDownload(fanartFile)){
				ImageDownload.downloadByProxy(tempPicSite, tempLocalPath);
				int[] fanartSize = ImageUtil.getImageSize(fanartFile);
				
				tempPicSite = movie.getSmallPosterPicSite();
				tempExtName = tempPicSite.substring(tempPicSite.lastIndexOf("."));
				tempLocalPath = movieDirPath + "/poster" + tempExtName;
				ImageDownload.downloadByProxy(tempPicSite, tempLocalPath);
				File posterFile = new File(tempLocalPath);
				int[] posterSize = ImageUtil.getImageSize(posterFile);
				
				// 切割fanart图片，生成poster图片
				if (fanartSize != null && posterSize != null) {
					//小图又白边，宽度减6或减7
					int posterWidth = fanartSize[1] * (posterSize[0] - 6) / posterSize[1];
					ImageUtil.cutImage(fanartFile.getAbsolutePath(), posterFile.getAbsolutePath(),
							fanartSize[0] - posterWidth, 0, fanartSize[0], fanartSize[1]);
				}
			}
			
			if (Config.downloadFanart && movie.getFanartsPicSite() != null && movie.getFanartsPicSite().size() > 0) {
				// 多线程代码
				ThreadPoolExecutor executor = new ThreadPoolExecutor(threadSize, threadSize, 60000, 
						TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
				new File(movieDirPath + "/extrafanart").mkdir();
				String tempPicName = null;
				String[] array = null;
				for (String site : movie.getFanartsPicSite()) {
					// 修改图片名，以便图片正确排序
					tempPicName = site.substring(site.lastIndexOf("/") + 1);
					tempExtName = site.substring(site.lastIndexOf("."));
					array = tempPicName.replace(tempExtName, "").split("-");
					if (array.length == 2 && array[1].length() == 1) {
						tempLocalPath = movieDirPath + "/extrafanart/" + array[0] + "-0" + array[1] + tempExtName;
					} else {
						tempLocalPath = movieDirPath + "/extrafanart/" + tempPicName;
					}
					
					if(neededToDownload(new File(tempLocalPath))){
						final String finalSite = site;
						final String finalPath = tempLocalPath;
						executor.execute(new Runnable() {
							@Override
							public void run() {
								ImageDownload.downloadByProxy(finalSite, finalPath);
							}
						});
					}
					
				}
				
				executor.shutdown();
				while (true) {
					if (executor.isTerminated()) {
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}

	/**
	 * 创建文件夹
	 * @param file
	 */
	private synchronized void makeDir(File file){
		if(!file.exists()){
			file.mkdir();
		}
	}
	
	/**
	 * 图片已经下载并且大于10k，或者大小等于now_printing.jpg的大小则不用重新下载
	 * @param pic
	 * @return
	 */
	private boolean neededToDownload(File pic) {
		if (pic.exists() && (pic.length() / 1024 >= 10 || pic.length() == 2732)) {
			return false;
		}
		return true;
	}
}
