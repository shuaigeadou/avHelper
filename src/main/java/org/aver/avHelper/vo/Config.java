package org.aver.avHelper.vo;

import java.util.LinkedList;
import java.util.List;

public class Config {
	/** 临时存放电影目录 */
	private String tempMediaDir;
	/** 影片管理目录 */
	private List<Address> serverMediaDir = new LinkedList<Address>();
	/** 分类的地址 */
	private List<Address> categoryUrl = new LinkedList<Address>();
	/** 线程数 */
	private int threadSize;
	/** 是否下载预览图 */
	private boolean downloadFanart;
	/** socks代理iP */
	private String socksHost;
	/** socks代理端口 */
	private int socksPort;
	/** socks代理用户名 */
	private String socksName;
	/** socks代理密码 */
	private String socksPwd;
	/** 最大演员数 */
	private Integer maxActorNum;
	/** 上映开始日期 */
	private String startDate;
	/** 上映结束日期 */
	private String endDate;
	/** 分类排除 */
	private List<String> genreExclude = new LinkedList<String>();

	public String getTempMediaDir() {
		return tempMediaDir;
	}

	public void setTempMediaDir(String tempMediaDir) {
		this.tempMediaDir = tempMediaDir;
	}

	public List<Address> getServerMediaDir() {
		return serverMediaDir;
	}

	public void setServerMediaDir(List<Address> serverMediaDir) {
		this.serverMediaDir = serverMediaDir;
	}

	public List<Address> getCategoryUrl() {
		return categoryUrl;
	}

	public void setCategoryUrl(List<Address> categoryUrl) {
		this.categoryUrl = categoryUrl;
	}

	public int getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
	}

	public boolean isDownloadFanart() {
		return downloadFanart;
	}

	public void setDownloadFanart(boolean downloadFanart) {
		this.downloadFanart = downloadFanart;
	}

	public String getSocksHost() {
		return socksHost;
	}

	public void setSocksHost(String socksHost) {
		this.socksHost = socksHost;
	}

	public int getSocksPort() {
		return socksPort;
	}

	public void setSocksPort(int socksPort) {
		this.socksPort = socksPort;
	}

	public String getSocksName() {
		return socksName;
	}

	public void setSocksName(String socksName) {
		this.socksName = socksName;
	}

	public String getSocksPwd() {
		return socksPwd;
	}

	public void setSocksPwd(String socksPwd) {
		this.socksPwd = socksPwd;
	}

	public Integer getMaxActorNum() {
		return maxActorNum;
	}

	public void setMaxActorNum(Integer maxActorNum) {
		this.maxActorNum = maxActorNum;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<String> getGenreExclude() {
		return genreExclude;
	}

	public void setGenreExclude(List<String> genreExclude) {
		this.genreExclude = genreExclude;
	}

}
