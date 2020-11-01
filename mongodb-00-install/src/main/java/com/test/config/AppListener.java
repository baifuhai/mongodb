package com.test.config;

import com.test.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class AppListener implements ApplicationListener<ApplicationEvent> {

	@Autowired
	MongoService mongoService;

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof ContextRefreshedEvent) {
//			mongoService.logRotateAndCompressLogFile();
		}
	}

}
