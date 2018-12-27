package org.aver.avHelper.service;

import java.io.IOException;
import java.util.List;

import org.aver.avHelper.vo.Movie;
import org.springframework.stereotype.Service;

@Service
public interface MainService {

	/**
	 * 用原有文件名生成改名配置
	 * @return
	 * @throws IOException
	 */
	public List<Movie> generateConfig() throws IOException;
	
	/**
	 * 根据规则生成改名配置
	 * @return
	 * @throws IOException
	 */
	public List<Movie> generateRenameConfig() throws IOException;
	
	/**
	 * 将前台新文件名同步到后台
	 * @param movies
	 * @throws Exception 
	 */
	public void synchronizeNewName(List<Movie> movies) throws Exception;
	
	/**
	 * 下载影片信息
	 * @return
	 * @throws Exception 
	 */
	public List<Movie> getMovieMessage() throws Exception;
	
	/**
	 * 修改文件名
	 * @throws Exception
	 */
	public void reName() throws Exception;
	
	/**
	 * 下载电影信息到指定文件夹
	 * @param desDir
	 * @throws Exception
	 */
	public void downLoadImg(String desDir) throws Exception;
	
	/**
	 * 移动大文件到指定文件夹
	 * @param sourceDir
	 * @param desDir
	 */
	public void moveFiles(String sourceDir, String desDir);
	
	/**
	 * 从deleteDir中删除retainDir中已存在的电影
	 * @param retainDir
	 * @param deleteDir
	 */
	public void compareDir(String retainDir, String deleteDir);
	
	/**
	 * 生成伪装视频到临时目录
	 * @param urlName
	 * @throws IOException 
	 */
	public void generatePseudoVideo(String urlName) throws IOException;
	
}
