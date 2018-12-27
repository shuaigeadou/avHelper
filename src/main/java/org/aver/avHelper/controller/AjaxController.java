package org.aver.avHelper.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aver.avHelper.service.MainService;
import org.aver.avHelper.vo.Address;
import org.aver.avHelper.vo.Config;
import org.aver.avHelper.vo.ConfigStatic;
import org.aver.avHelper.vo.Movie;
import org.aver.avHelper.vo.RestrictConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajax")
public class AjaxController {
	
	@Autowired
	private MainService mainService;

	@PostMapping("/getConfig")
	public Config getConfig() {
		return ConfigStatic.config;
	}
	
	@GetMapping("/getJavBusSite")
	public String getJavBusSite() {
		return ConfigStatic.javBusSite;
	}
	
	@PostMapping("/lockTempDir")
	public void lockTempDir(@RequestBody String tempDir) {
		ConfigStatic.config.setTempMediaDir(tempDir);
		ConfigStatic.saveConfig();
	}

	@PostMapping("/addMedirDir")
	public void addMedirDir(@RequestBody Address medirDir) {
		ConfigStatic.config.getServerMediaDir().add(medirDir);
		ConfigStatic.saveConfig();
	}
	
	@PostMapping("/deleteMedirDir")
	public void deleteMedirDir(@RequestBody String medirDir) {
		List<Address> serverMediaDir = ConfigStatic.config.getServerMediaDir();
		for (int i = 0; i < serverMediaDir.size(); i++) {
			if(StringUtils.equals(serverMediaDir.get(i).getName(), medirDir)){
				serverMediaDir.remove(i);
				break;
			}
		}
		ConfigStatic.saveConfig();
	}

	@PostMapping("/addUrl")
	public void addUrl(@RequestBody Address url) {
		ConfigStatic.config.getCategoryUrl().add(url);
		ConfigStatic.saveConfig();
	}
	
	@PostMapping("/deleteUrl")
	public void deleteUrl(@RequestBody String url) {
		List<Address> categoryUrl = ConfigStatic.config.getCategoryUrl();
		for (int i = 0; i < categoryUrl.size(); i++) {
			if(StringUtils.equals(categoryUrl.get(i).getName(), url)){
				categoryUrl.remove(i);
				break;
			}
		}
		ConfigStatic.saveConfig();
	}
	
	@PostMapping("/lockConfig")
	public void lockConfig(@RequestBody RestrictConfig restrictConfig) {
		BeanUtils.copyProperties(restrictConfig, ConfigStatic.config);
		ConfigStatic.saveConfig();
	}
	
	@PostMapping("/addType")
	public void addType(@RequestBody String type) {
		ConfigStatic.config.getGenreExclude().add(type);
		ConfigStatic.saveConfig();
	}
	
	@PostMapping("/deleteType")
	public void deleteType(@RequestBody String type) {
		ConfigStatic.config.getGenreExclude().remove(type);
		ConfigStatic.saveConfig();
	}
	
	@PostMapping("/generateConfig")
	public List<Movie> generateConfig(@RequestBody String usedRules) throws IOException {
		if(StringUtils.equalsIgnoreCase("true", usedRules)){
			return mainService.generateRenameConfig();
		}else{
			return mainService.generateConfig();
		}
	}
	
	@PostMapping("/synchronizeNewName")
	public void synchronizeNewName(@RequestBody List<Movie> movies) throws Exception {
		mainService.synchronizeNewName(movies);
	}
	
	@PostMapping("/reName")
	public void reName() throws Exception{
		mainService.reName();
	}
	
	@PostMapping("/getMovieMessage")
	public List<Movie> getMovieMessage() throws Exception{
		return mainService.getMovieMessage();
	}
	
	@PostMapping("/moveToThis")
	public void moveToThis(@RequestBody String dirName) throws Exception{
		List<Address> serverMediaDir = ConfigStatic.config.getServerMediaDir();
		for (Address address : serverMediaDir) {
			if(StringUtils.equals(dirName, address.getName())){
				mainService.downLoadImg(address.getUrl());
				break;
			}
		}
	}
	
	@PostMapping("/moveToTemp")
	public void moveToTemp(@RequestBody String dirName) throws Exception{
		List<Address> serverMediaDir = ConfigStatic.config.getServerMediaDir();
		for (Address address : serverMediaDir) {
			if(StringUtils.equals(dirName, address.getName())){
				mainService.moveFiles(address.getUrl(), ConfigStatic.config.getTempMediaDir());
				break;
			}
		}
	}
	
	@PostMapping("/compareDir")
	public void compareDir(String retainDirName, String deleteDirName){
		String retainDir = null;
		String deleteDir = null;
		List<Address> serverMediaDir = ConfigStatic.config.getServerMediaDir();
		for (Address address : serverMediaDir) {
			if(retainDir == null && StringUtils.equals(retainDirName, address.getName())){
				retainDir = address.getUrl();
			}
			if(deleteDir == null && StringUtils.equals(deleteDirName, address.getName())){
				deleteDir = address.getUrl();
			}
		}
		if(retainDir == null || deleteDir == null){
			throw new RuntimeException("指定的目录名不存在，请重新添加");
		}
		if(StringUtils.equalsIgnoreCase(retainDir, deleteDir)){
			throw new RuntimeException("两个目录名不能是一样的");
		}
		mainService.compareDir(retainDir, deleteDir);
	}
	
	@PostMapping("/generatePseudoVideo")
	public void generatePseudoVideo(@RequestBody String urlName) throws IOException{
		List<Address> categoryUrl = ConfigStatic.config.getCategoryUrl();
		for (Address address : categoryUrl) {
			if(StringUtils.equals(urlName, address.getName())){
				mainService.generatePseudoVideo(address.getUrl());
			}
		}
	}
	
}
