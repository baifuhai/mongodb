package com.test.schedule;

import com.test.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringSchedule {

	@Autowired
	MongoService mongoService;

//	@Scheduled(fixedDelayString = "${restart.interval}")
	public void restartService() {
		mongoService.restartService();
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void logRotateAndCompressLogFile() {
		mongoService.logRotateAndCompressLogFile();
	}

}
