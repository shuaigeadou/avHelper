package org.aver.avHelper.service.impl;

import java.io.IOException;

import org.aver.avHelper.service.MainService;

public class GeneratePseudoVideoTask implements Runnable {

	private MainService mainService;
	private String urlName;
	
	public GeneratePseudoVideoTask(MainService mainService, String urlName) {
		super();
		this.mainService = mainService;
		this.urlName = urlName;
	}

	@Override
	public void run() {
		try {
			mainService.generatePseudoVideo(urlName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
