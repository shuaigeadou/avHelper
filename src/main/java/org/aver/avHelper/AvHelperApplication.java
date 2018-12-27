package org.aver.avHelper;

import org.aver.avHelper.utils.BareBonesBrowserLaunch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.aver.avHelper"})
public class AvHelperApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(AvHelperApplication.class, args);
		BareBonesBrowserLaunch.browse("http://localhost:8080/avHelper");
	}
}
