package org.aver.avHelper.vo;

import java.util.List;

/**
 * 需要组织的电影信息
 */
public class Movie {
	/** 原文件名 */
	String originalName;

	/** 新文件名（番号） */
	String newName;

	/** 影片在avbus的网址 */
	String webSite;

	/** 标题 */
	String title;

	/** 短标题，emby中是类标题，程序里还作短标题用，生成nfo时赋值给原文件名节点 */
	String sorttitle;

	/** 导演 */
	String director;

	/** 年份 */
	String year;

	/** 发行日期 */
	String releasedate;

	/** 风格 */
	List<String> genre;

	/** 演员名 */
	List<String> actorName;

	/** 工作室 */
	String studio;

	/** 系列 */
	String series;

	/** 海报图片地址 */
	String posterPicSite;

	/** 下载的海报是否需要裁剪 */
	Boolean posterNeedCut;
	
	/** 小海报图片地址 */
	String smallPosterPicSite;

	/** 影片截图地址 */
	List<String> fanartsPicSite;

	List<Movie> childrenMovies;

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSorttitle() {
		return sorttitle;
	}

	public void setSorttitle(String sorttitle) {
		this.sorttitle = sorttitle;
	}

	public String getReleasedate() {
		return releasedate;
	}

	public void setReleasedate(String releasedate) {
		this.releasedate = releasedate;
	}

	public List<String> getGenre() {
		return genre;
	}

	public void setGenre(List<String> genre) {
		this.genre = genre;
	}

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	public List<String> getActorName() {
		return actorName;
	}

	public void setActorName(List<String> actorName) {
		this.actorName = actorName;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getPosterPicSite() {
		return posterPicSite;
	}

	public void setPosterPicSite(String posterPicSite) {
		this.posterPicSite = posterPicSite;
	}

	public String getSmallPosterPicSite() {
		return smallPosterPicSite;
	}

	public void setSmallPosterPicSite(String smallPosterPicSite) {
		this.smallPosterPicSite = smallPosterPicSite;
	}

	public List<String> getFanartsPicSite() {
		return fanartsPicSite;
	}

	public void setFanartsPicSite(List<String> fanartsPicSite) {
		this.fanartsPicSite = fanartsPicSite;
	}

	public List<Movie> getChildrenMovies() {
		return childrenMovies;
	}

	public void setChildrenMovies(List<Movie> childrenMovies) {
		this.childrenMovies = childrenMovies;
	}

	public Boolean getPosterNeedCut() {
		return posterNeedCut;
	}

	public void setPosterNeedCut(Boolean posterNeedCut) {
		this.posterNeedCut = posterNeedCut;
	}
	
}
