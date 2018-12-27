package org.aver.avHelper.vo;


public class RestrictConfig {
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
	
}
