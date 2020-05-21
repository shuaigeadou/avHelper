package org.aver.avHelper.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.aver.avHelper.utils.HtmlDownload;
import org.aver.avHelper.vo.ConfigStatic;
import org.aver.avHelper.vo.Movie;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetMgstageMovieMsgTask implements Runnable {
	private Movie movie;
	private CopyOnWriteArrayList<Movie> movieList;
	private CopyOnWriteArrayList<Movie> errorMovieList;

	public GetMgstageMovieMsgTask(Movie movie, CopyOnWriteArrayList<Movie> movieList,
			CopyOnWriteArrayList<Movie> errorMovieList) {
		this.movie = movie;
		this.movieList = movieList;
		this.errorMovieList = errorMovieList;
	}

	@Override
	public void run() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("cookie", "adc=1");
		Document document = HtmlDownload.getDocBySite(movie.getWebSite(), properties);
		Elements containerEles = document.select(".common_detail_cover");
		
		//没有查到影片信息
		if(containerEles.size() == 0){
			errorMovieList.add(movie);
			return;
		}
		
		//解析影片信息
		//标题
		movie.setTitle(StringUtils.trimToNull(containerEles.select(".tag").text()));
		
		Element detailDataEle = containerEles.select(".detail_left .detail_data").get(0);
		
		//遍历table，刮削信息
		Iterator<Element> tableIt = detailDataEle.select("table").iterator();
		while (tableIt.hasNext()) {
			Element table = (Element) tableIt.next();
			Iterator<Element> trIt = table.select("tr").iterator();
			while (trIt.hasNext()) {
				Element trEle = (Element) trIt.next();
				if (trEle.selectFirst("th") == null ) {
					continue;
				}
				//演员
				if (trEle.selectFirst("th").text().contains("出演")) {
					Elements actorEles = trEle.selectFirst("td").select("a");
					List<String> actors = new ArrayList<String>();
					//有些没有超链接，只有个名字
					if (actorEles.size() == 0) {
						actors.add(StringUtils.trimToNull(trEle.selectFirst("td").text()));
					}else {
						Iterator<Element> aIt = actorEles.iterator();
						while (aIt.hasNext()) {
							actors.add(StringUtils.trimToNull(aIt.next().text()));
						}
					}
					movie.setActorName(actors);
				}
				//制作
				else if (trEle.selectFirst("th").text().contains("メーカー")) {
					movie.setStudio(StringUtils.trimToNull(trEle.selectFirst("td").text()));
				}
				//发布日期
				else if (trEle.selectFirst("th").text().contains("配信開始日")) {
					String date = StringUtils.trimToNull(trEle.selectFirst("td").text());
					movie.setReleasedate(date.replaceAll("/", "-"));
					movie.setYear(date.substring(0, 4));
				}
				//系列
				else if (trEle.selectFirst("th").text().contains("シリーズ")) {
					String japanSer = StringUtils.trimToNull(trEle.selectFirst("td").text());
					String chineseSer = ConfigStatic.languageDict == null ? null
							: ConfigStatic.languageDict.get(japanSer);
					movie.setSeries(chineseSer == null ? japanSer : chineseSer);
				}
				//标签、类型
				else if (trEle.selectFirst("th").text().contains("レーベル")
						|| trEle.selectFirst("th").text().contains("ジャンル")) {
					Elements aEles = trEle.selectFirst("td").select("a");
					if (aEles.size() > 0) {
						Iterator<Element> aIt = aEles.iterator();
						List<String> genreList = new ArrayList<String>();
						while (aIt.hasNext()) {
							String japanGen = StringUtils.trimToNull(aIt.next().text());
							String chineseGen = ConfigStatic.languageDict == null ? null
									: ConfigStatic.languageDict.get(japanGen);
							genreList.add(chineseGen == null ? japanGen : chineseGen);
						}
						movie.setGenre(genreList);
					}
				}
			}
		}
		
		Element photoDiv = detailDataEle.selectFirst(">div");
		//海报
		movie.setPosterPicSite(photoDiv.selectFirst("a").attr("href"));
		movie.setSmallPosterPicSite(photoDiv.selectFirst("img").attr("src"));
		//海报是否需要裁剪
		if (photoDiv.hasClass("detail_photo")) {
			movie.setPosterNeedCut(true);
		}
		
		//影片截图
		Iterator<Element> picIt = document.selectFirst("#sample-photo").select("li a").iterator();
		List<String> samplePicList = new ArrayList<String>();
		while (picIt.hasNext()) {
			samplePicList.add(picIt.next().attr("href"));
		}
		movie.setFanartsPicSite(samplePicList);
		
		movieList.add(movie);
	}

}
