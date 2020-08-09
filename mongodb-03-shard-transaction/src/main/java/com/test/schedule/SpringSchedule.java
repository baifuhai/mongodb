package com.test.schedule;

import com.test.service.MeasureInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SpringSchedule {

	@Autowired
	MeasureInfoService measureInfoService;

//	@Scheduled(cron = "0,15,30,45 * * * * ?")
	@Scheduled(cron = "0/5 * * * * ?")
	public void generateData() {
		measureInfoService.generateData();
	}

}
