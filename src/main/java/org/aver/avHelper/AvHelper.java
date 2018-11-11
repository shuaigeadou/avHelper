package org.aver.avHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 主程序
 * 
 * @author Administrator
 *
 */
public class AvHelper {
	
	public static void main(String args[]) throws Exception {
		// 读取配置
		Config.readConfig();
		
		boolean flag = true;
		while (flag) {
			StepsEnmu nextStep = askNextStep();
			System.out.println(nextStep);

			switch (nextStep) {
			case GENERATERENAMECONFIG:
				generateRenameConfig();
				break;
			case RENAME:
				reName();
				break;
			case GETMOVIEMSG:
				getMovieMessage();
				break;
			case DOWNLOADIMG:
				downLoadImg(false);
				break;
			case ADDWMVEXTENSION:
				addWmvExtension();
				break;
			case DOWNLOADALLPIC:
				downloadAllPic();
				break;
			case MOVEFILE:
				downLoadImg(true);
				break;
			case GENERATECONFIG:
				generateConfig();
				break;
			case DELETEPREVIEW:
				deletePreview();
				break;
			default:
				flag = false;
				break;
			}
		}
	}

	private static void generateConfig() throws IOException {
		//所有文件名
		String[] names = new File(Config.localMoviePath).list();
		//用来保存可识别影片信息
		List<Movie> movieList = new ArrayList<Movie>();
		Movie movie = null;
		for (String name : names) {
			movie = new Movie();
			movie.setOriginalName(name);
			movie.setNewName(name);
			movie.setSorttitle(name.substring(0, name.lastIndexOf(".")));
			movie.setWebSite(Config.javBusSite + name.substring(0, name.lastIndexOf(".")));
			movieList.add(movie);
		}
		MovieXmlHandler.generateConfig(movieList, Config.movieRootPath, Config.moviesXmlName, true);
	}
	
	private static void generateRenameConfig() throws IOException {
		//所有文件名
		String[] names = new File(Config.localMoviePath).list();
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
					movie.setWebSite(Config.javBusSite + shortName);
					movie.setNewName(shortName + "." + movieExtName);
					movieList.add(movie);
				}else{
					String parentName = parentMovie.getNewName();
					if(parentMovie.getChildrenMovies() == null){
						parentMovie.setChildrenMovies(new ArrayList<Movie>());
						parentMovie.setNewName(parentName.substring(0, parentName.lastIndexOf("."))
								+ Config.rule[0] + parentName.substring(parentName.lastIndexOf(".")));
					}
					movie.setNewName(
							shortName + Config.rule[parentMovie.getChildrenMovies().size() + 1]
									+ "." + movieExtName);
					parentMovie.getChildrenMovies().add(movie);
				}
			}
			//影片不能识别
			else{
				errMovieList.add(movie);
			}
		}

		MovieXmlHandler.generateConfig(movieList, Config.movieRootPath, Config.moviesXmlName, true);
		MovieXmlHandler.generateConfig(errMovieList, Config.movieRootPath, Config.errorMoviesXmlName, false);
	}

	
	private static void reName() throws Exception {
		List<Movie> movieList = MovieXmlHandler.readConfig(new File(Config.movieRootPath + Config.moviesXmlName));
		if(movieList == null){
			System.out.println("请先生成配置文件");
			return ;
		}
		File originalFile = null;
		File newFile = null;
		for (Movie movie : movieList) {
			originalFile = new File(Config.localMoviePath + "/" + movie.getOriginalName());
			newFile = new File(Config.localMoviePath + "/" + movie.getNewName());
			if(originalFile.exists()){
				originalFile.renameTo(newFile);
			}
			if(movie.getChildrenMovies() != null && movie.getChildrenMovies().size() > 0){
				for (Movie childMovie : movie.getChildrenMovies()) {
					originalFile = new File(Config.localMoviePath + "/" + childMovie.getOriginalName());
					newFile = new File(Config.localMoviePath + "/" + childMovie.getNewName());
					if(originalFile.exists()){
						originalFile.renameTo(newFile);
					}
				}
			}
		}
	}
	
	private static void getMovieMessage() throws Exception {
		List<Movie> movieList = MovieXmlHandler.readConfig(new File(Config.movieRootPath + Config.moviesXmlName));
		if (movieList == null) {
			System.out.println("请先生成配置文件");
			return;
		}

		// 多线程代码
		ThreadPoolExecutor executor = new ThreadPoolExecutor(Config.threadSize, Config.threadSize, 60000,
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
				MovieXmlHandler.generateConfig(
						movieListForSite, Config.movieRootPath, Config.moviesXmlName, true);
				MovieXmlHandler.generateConfig(
						errorMovieListForSite, Config.movieRootPath, Config.errorMoviesXmlName, false);
				break;
			}
			Thread.sleep(1000);
		}
	}
	
	private static void downLoadImg(boolean isMovieFile) throws Exception {
		List<Movie> movieList = MovieXmlHandler.readConfig(new File(Config.movieRootPath + Config.moviesXmlName));
		if (movieList == null) {
			System.out.println("请先生成配置文件");
			return;
		}

		// 多线程代码
		ThreadPoolExecutor executor = new ThreadPoolExecutor(Config.threadSize, Config.threadSize, 60000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		DownLoadImgTask downLoadImgTask = null;
		for (Movie movie : movieList) {
			downLoadImgTask = new DownLoadImgTask(movie, isMovieFile);
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
	
	private static void addWmvExtension() throws IOException{
		File pathFile = new File(Config.localMoviePath);
		if (pathFile.exists() && pathFile.list().length > 0) {
			for (String name : pathFile.list()) {
				String mainName = name.substring(0, name.lastIndexOf("."));
				FileUtils.moveFile(new File(Config.localMoviePath + "/" + name),
						new File(Config.localMoviePath + "/" + mainName + ".wmv"));
			}
		}
	}
	
	private static void downloadAllPic() throws InterruptedException {
		if(StringUtils.isNoneBlank(Config.appointUrl)){
			// 多线程代码
			ThreadPoolExecutor executor = new ThreadPoolExecutor(Config.threadSize, Config.threadSize, 60000,
					TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
			String[] urls = Config.appointUrl.split(",");
			//请求参数
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			properties.put("Cookie", "existmag=all");
			for (String url : urls) {
				int i = 1;
				while (true) {
					Document document = HtmlDownload.getDocBySite(url + "/" + i, properties);
					Elements imgEles = document.select("#waterfall a.movie-box");
					if (imgEles.size() == 0) break;
					Iterator<Element> imgElesIt = imgEles.iterator();
					while (imgElesIt.hasNext()) {
						Element movieBoxEle = imgElesIt.next();
						final String imgUrl = movieBoxEle.select("img").attr("src");
						String extensionName = imgUrl.substring(imgUrl.lastIndexOf("."));
						final String picName = movieBoxEle.selectFirst("date").text() + extensionName;
						executor.execute(new Runnable() {
							@Override
							public void run() {
								ImageDownload.downloadByProxy(imgUrl, Config.localMoviePath + "/" + picName);
							}
						});
					}
					i++;
				}
			}
			
			executor.shutdown();
			while (true) {
				if (executor.isTerminated()) {
					break;
				}
				Thread.sleep(1000);
			}
			
		}
		
	}
	
	private static void deletePreview(){
		Map<String, File> actorNames = new HashMap<String, File>();
		File dirForWatch = new File(Config.serverMediaPath);
		for (File file : dirForWatch.listFiles()) {
			actorNames.put(file.getName(), file);
		}
		
		File dirForPreview = new File(Config.previewMediaPath);
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
	private static void delteDir(File dir){
		for (File file : dir.listFiles()) {
			if(file.isDirectory()){
				delteDir(file);
			} else {
				if (file.length() / 1024 / 1024 < 20) {
					if(!file.delete()){
						System.out.println(file.getAbsolutePath());
					}
				}else{
					System.out.println(file.getAbsolutePath());
				}
			}
		}
		dir.delete();
	}
	
	/**
	 * 询问用户下一步操作
	 * 
	 * @return
	 * @throws IOException
	 */
	private static StepsEnmu askNextStep() throws IOException {
		// 输出分割线
		System.out.println("---------------------------------");

		for (StepsEnmu step : StepsEnmu.values()) {
			System.out.println(step);
		}
		System.out.println("请选择：");

		// 读取输入
		// BufferedReader reader = new BufferedReader(new
		// InputStreamReader(System.in));
		// String content = reader.readLine();
		// reader.close();
		Scanner scan = new Scanner(System.in);
		String content = scan.nextLine();

		StepsEnmu step = StepsEnmu.parse(content);
		// 退出时关闭流
		if (step == StepsEnmu.EXIT) {
			scan.close();
		}

		return step;
	}

}
