package com.test.service;

import com.test.util.MongoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

@Slf4j
@Service
public class MongoService implements InitializingBean {

	@Value("${restart.enabled}")
	boolean enabled;

	@Value("${restart.beginHour}")
	int beginHour;

	@Value("${restart.endHour}")
	int endHour;

	@Value("${restart.interval}")
	String interval;

	@Value("${mongodb.cluster}")
	String cluster;

	@Value("${mongodb.home}")
	String home;

	@Value("${mongodb.host}")
	String host;

	@Value("${mongodb.beginShardPort}")
	int beginShardPort;

	@Value("${mongodb.beginShardNumber}")
	int beginShardNumber;

	@Value("${mongodb.endShardNumber}")
	int endShardNumber;

	int currentShardNumber;

	@Override
	public void afterPropertiesSet() throws Exception {
		currentShardNumber = beginShardNumber;
	}

	public void restartService() {
		if (enabled) {
			LocalDateTime now = LocalDateTime.now();
			int hour = now.getHour();
			if (hour >= beginHour && hour <= endHour) {
				try {
					if (currentShardNumber <= endShardNumber) {
						int port = beginShardPort + currentShardNumber;

						String serviceName = "MongoDBShard" + currentShardNumber;

						MongoUtil.restartService(host, port, serviceName);

						currentShardNumber++;
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			} else {
				currentShardNumber = beginShardNumber;
			}
		}
	}

	public void logRotateAndCompressLogFile() throws Exception {
		File clusterDir = new File(cluster);
		MongoUtil.logRotateAndCompressLogFile(clusterDir, beginShardNumber, endShardNumber);
	}

}
