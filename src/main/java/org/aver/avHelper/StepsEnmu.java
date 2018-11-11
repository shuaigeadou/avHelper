package org.aver.avHelper;

/**
 * 步骤
 * @author Administrator
 *
 */
enum StepsEnmu {

	/** 根据规则生成改名配置 */
	GENERATERENAMECONFIG("根据规则生成改名配置", "1"),
	/** 根据生成的配置文件更名 */
	RENAME("根据生成的配置文件更名", "2"),
	/** 获取影片信息并更新配置文件 */
	GETMOVIEMSG("获取影片信息并更新配置文件", "3"),
	/** 下载图片并生成info信息 */
	DOWNLOADIMG("下载图片并生成info信息", "4"),
	/** 退出 */
	EXIT("退出", "5"),
	/** 文件夹下所有文件扩展名改为wmv */
	ADDWMVEXTENSION("文件夹下所有文件扩展名改为wmv", "6"),
	/** 下载指定影片地址的所有缩略图 */
	DOWNLOADALLPIC("下载指定影片地址的所有缩略图", "7"),
	/** 移动电影 */
	MOVEFILE("移动电影", "8"),
	/** 生成配置文件 */
	GENERATECONFIG("生成配置文件", "9"),
	/** 生成配置文件 */
	DELETEPREVIEW("从预览目录中删除观看目录已有的影片", "10");

	// 内部变量
	private String step;
	private String index;

	// 构造方法
	private StepsEnmu(String step, String index) {
		this.step = step;
		this.index = index;
	}

	/**
	 * 转换成枚举类型
	 * 
	 * @param step
	 * @return
	 */
	public static StepsEnmu parse(String step) {
		for (StepsEnmu s : StepsEnmu.values()) {
			if (s.index.equals(step)) {
				return s;
			}
		}
		return EXIT;
	}

	@Override
	public String toString() {
		return this.index + "." + this.step;
	}

}
