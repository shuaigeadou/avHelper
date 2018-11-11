package org.aver.avHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetMovieMsgTask implements Runnable {
	private Movie movie;
	private CopyOnWriteArrayList<Movie> movieList;
	private CopyOnWriteArrayList<Movie> errorMovieList;

	public GetMovieMsgTask(Movie movie, CopyOnWriteArrayList<Movie> movieList,
			CopyOnWriteArrayList<Movie> errorMovieList) {
		this.movie = movie;
		this.movieList = movieList;
		this.errorMovieList = errorMovieList;
	}

	@Override
	public void run() {
		// 没有网址的不处理
		if (StringUtils.isBlank(movie.getWebSite())) {
			errorMovieList.add(movie);
			return;
		}

		Document document = HtmlDownload.getDocBySite(movie.getWebSite(), null);
		Elements containerEles = document.select(".container");
		
		//没有查到影片信息
		if(containerEles.size() == 0){
			errorMovieList.add(movie);
			return;
		}
		
		//解析影片信息
		//标题
		movie.setTitle(containerEles.select("h3").text());
		
		//演员
		Elements actorEles = containerEles.select(".info .star-name");
		if(actorEles.size() > 0){
			Iterator<Element> actorElesIt = actorEles.iterator();
			List<String> actors = new ArrayList<String>();
			while(actorElesIt.hasNext()){
				actors.add(actorElesIt.next().select("a").text());
			}
			movie.setActorName(actors);
		}
		
		Elements infoEles = containerEles.select(".info>p");
		Element currentEle;
		Iterator<Element> infoEleIt = infoEles.iterator();
		while(infoEleIt.hasNext()){
			currentEle = infoEleIt.next();
			if(currentEle.text().contains("導演:")){
				movie.setDirector(currentEle.select("a").text());
			}else if(currentEle.text().contains("發行日期:")){
				String date = currentEle.text().replace("發行日期:", "").replaceAll(" ", "");
				movie.setReleasedate(date);
				movie.setYear(date.substring(0, date.indexOf("-")));
			}else if(currentEle.text().contains("類別:")){
				Elements genreEles = currentEle.nextElementSibling().select("a");
				List<String> genreList = new ArrayList<String>();
				Iterator<Element> genreElesIt = genreEles.iterator();
				while(genreElesIt.hasNext()){
					genreList.add(genreElesIt.next().text());
				}
				movie.setGenre(genreList);
			}else if(currentEle.text().contains("製作商:")){
				movie.setStudio(currentEle.select("a").text());
			}else if(currentEle.text().contains("系列:")){
				movie.setSeries(currentEle.select("a").text());
			}
		}
		
		//海报
		String posterPicSite = containerEles.select(".screencap a").attr("href");
		movie.setPosterPicSite(posterPicSite);
		movie.setSmallPosterPicSite(posterPicSite.replace("cover", "thumb").replace("_b", ""));
		
		//影片截图
		Elements samplePicsEles = containerEles.select("#sample-waterfall a");
		if(samplePicsEles.size() > 0){
			Iterator<Element> samplePicsElesIt = samplePicsEles.iterator();
			List<String> samplePicList = new ArrayList<String>();
			while(samplePicsElesIt.hasNext()){
				samplePicList.add(samplePicsElesIt.next().attr("href"));
			}
			movie.setFanartsPicSite(samplePicList);
		}
		
		movieList.add(movie);
	}

}
