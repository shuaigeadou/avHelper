package org.aver.avHelper.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.aver.avHelper.utils.HtmlDownload;
import org.aver.avHelper.vo.ConfigStatic;
import org.aver.avHelper.vo.Movie;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetFanzaMovieMsgTask implements Runnable {
	private Movie movie;
	private CopyOnWriteArrayList<Movie> movieList;
	private CopyOnWriteArrayList<Movie> errorMovieList;

	public GetFanzaMovieMsgTask(Movie movie, CopyOnWriteArrayList<Movie> movieList,
			CopyOnWriteArrayList<Movie> errorMovieList) {
		this.movie = movie;
		this.movieList = movieList;
		this.errorMovieList = errorMovieList;
	}

	@Override
	public void run() {
		Document document = HtmlDownload.getDocBySite(movie.getWebSite(), null);

		//没有查到影片信息
		if(document.select(".page-detail").size() == 0){
			errorMovieList.add(movie);
			return;
		}
		
		//删除多余的div，获取主要div
		if (document.selectFirst("#recommend") != null) {
			document.selectFirst("#recommend").remove();
		}
		if (document.selectFirst("#recommend1") != null) {
			document.selectFirst("#recommend1").remove();
		}
		if (document.selectFirst("#review") != null) {
			document.selectFirst("#review").remove();
		}
		
		Elements pageDetail = document.select(".page-detail");
		
		//解析影片信息
		//标题
		Elements h1Ele = pageDetail.select(".hreview h1");
		h1Ele.select("span").remove();
		movie.setTitle(StringUtils.trimToNull(h1Ele.text()));
		
		//遍历内层table，刮削信息
		Iterator<Element> tableIt = pageDetail.select("table").iterator();
		while (tableIt.hasNext()) {
			Element table = (Element) tableIt.next();
			//查找最内层table
			if (table.children().select("table").size() == 0) {
				Iterator<Element> trIt = table.select("tr").iterator();
				while (trIt.hasNext()) {
					Element trEle = (Element) trIt.next();
					Elements tdEle = trEle.select("td");
					if (tdEle.size() < 2 || "----".equals(tdEle.get(1).text())) {
						continue;
					}
					//演员
					if (trEle.selectFirst("td").text().contains("出演者")
						|| trEle.selectFirst("td").text().contains("名前")) {
						Elements actorEles = trEle.select("td").get(1).select("a");
						List<String> actors = new ArrayList<String>();
						//有些没有超链接，只有个名字
						if (actorEles.size() == 0) {
							actors.add(StringUtils.trimToNull(trEle.select("td").get(1).text()));
						}else {
							Iterator<Element> aIt = actorEles.iterator();
							while (aIt.hasNext()) {
								actors.add(StringUtils.trimToNull(aIt.next().text()));
							}
						}
						movie.setActorName(actors);
					}
					//制作
					else if (trEle.selectFirst("td").text().contains("メーカー")) {
						movie.setStudio(StringUtils.trimToNull(trEle.select("td").get(1).text()));
					}
					//导演
					else if (trEle.selectFirst("td").text().contains("監督")) {
						movie.setDirector(StringUtils.trimToNull(trEle.select("td").get(1).text()));
					}
					//发布日期
					else if (trEle.selectFirst("td").text().contains("開始日")
							|| trEle.selectFirst("td").text().contains("発売日")) {
						String date = StringUtils.trimToNull(trEle.select("td").get(1).text());
						if ("//".equals(date.replaceAll("\\d", ""))) {
							movie.setReleasedate(date.replaceAll("/", "-"));
							movie.setYear(date.substring(0, 4));
						}
					}
					//系列
					else if (trEle.selectFirst("td").text().contains("シリーズ")) {
						String japanSer = StringUtils.trimToNull(trEle.select("td").get(1).text());
						String chineseSer = ConfigStatic.languageDict == null ? null
								: ConfigStatic.languageDict.get(japanSer);
						movie.setSeries(chineseSer == null ? japanSer : chineseSer);
					}
					//标签、类型
					else if (trEle.selectFirst("td").text().contains("レーベル")
							|| trEle.selectFirst("td").text().contains("ジャンル")) {
						Elements aEles = trEle.select("td").get(1).select("a");
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
		}
		
		Element photoDiv = document.selectFirst("#sample-video");
		//海报
		Element packageImg = photoDiv.selectFirst("[name=package-image]");
		if (packageImg != null) {
			movie.setPosterPicSite(packageImg.attr("href"));
			movie.setSmallPosterPicSite(packageImg.selectFirst("img").attr("src"));
			//海报是否需要裁剪
			movie.setPosterNeedCut(true);
		}else {
			movie.setPosterPicSite(photoDiv.selectFirst("img").attr("src"));
			movie.setSmallPosterPicSite(photoDiv.selectFirst("img").attr("src"));
		}
		
		//影片截图
		Element imgeBlock = document.selectFirst("#sample-image-block");
		if (imgeBlock != null) {
			Elements imgBlock = imgeBlock.select("[name=sample-image]");
			if (imgBlock.size() > 0) {
				Iterator<Element> picIt = imgBlock.iterator();
				List<String> samplePicList = new ArrayList<String>();
				while (picIt.hasNext()) {
					String picSite = picIt.next().selectFirst("img").attr("src");
					if (picSite.indexOf("js-") > 0) {
						samplePicList.add(picSite.replace("js-", "jp-"));
					}else {
						samplePicList.add(picSite.replace("-", "jp-"));
					}
				}
				movie.setFanartsPicSite(samplePicList);
			}
		}
		
		movieList.add(movie);
	}

}
