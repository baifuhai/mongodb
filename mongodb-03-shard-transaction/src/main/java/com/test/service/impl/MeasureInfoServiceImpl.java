package com.test.service.impl;

import com.test.domain.entity.MeasureBusbarInfoEntity;
import com.test.domain.entity.MeasureLineInfoEntity;
import com.test.domain.entity.MeasureLoadInfoEntity;
import com.test.domain.entity.MeasureSubInfoEntity;
import com.test.domain.entity.MeasureTranInfoEntity;
import com.test.domain.entity.MeasureWindingInfoEntity;
import com.test.service.MeasureInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MeasureInfoServiceImpl implements MeasureInfoService {

	@Autowired
	MongoTemplate mongoTemplate;

	public void generateData() {
		log.info("=============== 开始生成数据 ===============");

		LocalDateTime now = LocalDateTime.now();
		String nowFormat = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		try {
			log.info("busbar");
			List<MeasureBusbarInfoEntity> measureBusbarInfoEntityList = new ArrayList<>();
			for (int i = 0; i < 3000; i++) {
				MeasureBusbarInfoEntity measureBusbarInfoEntity = new MeasureBusbarInfoEntity();
				measureBusbarInfoEntity.setBusbarId(getRandomString());
				measureBusbarInfoEntity.setVUp(getRandomDouble());
				measureBusbarInfoEntity.setVDown(getRandomDouble());
				measureBusbarInfoEntity.setRVm(getRandomDouble());
				measureBusbarInfoEntity.setRVa(getRandomDouble());
				measureBusbarInfoEntity.setOverLimitRate(getRandomDouble());
				measureBusbarInfoEntity.setState(getRandomInt());
				measureBusbarInfoEntity.setState2(getRandomInt());
				measureBusbarInfoEntity.setDurationSeconds(getRandomLong());
				measureBusbarInfoEntity.setDuration(getRandomString());
				measureBusbarInfoEntity.setOccurTime(nowFormat);
				measureBusbarInfoEntity.setOccurTimeYear(getRandomInt());
				measureBusbarInfoEntity.setOccurTimeMonth(getRandomInt());
				measureBusbarInfoEntity.setOccurTimeWeek(getRandomString());
				measureBusbarInfoEntity.setOccurTimeDay(getRandomInt());
				measureBusbarInfoEntity.setOccurTimeHourMinute(nowFormat);
				measureBusbarInfoEntityList.add(measureBusbarInfoEntity);
			}
			mongoTemplate.insert(measureBusbarInfoEntityList, MeasureBusbarInfoEntity.class);

			log.info("line");
			List<MeasureLineInfoEntity> measureLineInfoEntityList = new ArrayList<>();
			for (int i = 0; i < 3000; i++) {
				MeasureLineInfoEntity measureLineInfoEntity = new MeasureLineInfoEntity();
				measureLineInfoEntity.setLineId(getRandomString());
				measureLineInfoEntity.setImax(getRandomDouble());
				measureLineInfoEntity.setRPij(getRandomDouble());
				measureLineInfoEntity.setRPji(getRandomDouble());
				measureLineInfoEntity.setRQij(getRandomDouble());
				measureLineInfoEntity.setRQji(getRandomDouble());
				measureLineInfoEntity.setRIij(getRandomDouble());
				measureLineInfoEntity.setRIji(getRandomDouble());
				measureLineInfoEntity.setLoad(getRandomDouble());
				measureLineInfoEntity.setI(getRandomDouble());
				measureLineInfoEntity.setLoadRate(getRandomDouble());
				measureLineInfoEntity.setState(getRandomInt());
				measureLineInfoEntity.setState2(getRandomInt());
				measureLineInfoEntity.setDurationSeconds(getRandomLong());
				measureLineInfoEntity.setDuration(getRandomString());
				measureLineInfoEntity.setOccurTime(nowFormat);
				measureLineInfoEntity.setOccurTimeYear(getRandomInt());
				measureLineInfoEntity.setOccurTimeMonth(getRandomInt());
				measureLineInfoEntity.setOccurTimeWeek(getRandomString());
				measureLineInfoEntity.setOccurTimeDay(getRandomInt());
				measureLineInfoEntity.setOccurTimeHourMinute(nowFormat);
				measureLineInfoEntityList.add(measureLineInfoEntity);
			}
			mongoTemplate.insert(measureLineInfoEntityList, MeasureLineInfoEntity.class);

			log.info("load");
			List<MeasureLoadInfoEntity> measureLoadInfoEntityList = new ArrayList<>();
			for (int i = 0; i < 3000; i++) {
				MeasureLoadInfoEntity measureLoadInfoEntity = new MeasureLoadInfoEntity();
				measureLoadInfoEntity.setLoadId(getRandomString());
				measureLoadInfoEntity.setImax(getRandomDouble());
				measureLoadInfoEntity.setRPl(getRandomDouble());
				measureLoadInfoEntity.setRQl(getRandomDouble());
				measureLoadInfoEntity.setRI(getRandomDouble());
				measureLoadInfoEntity.setLoadRate(getRandomDouble());
				measureLoadInfoEntity.setState(getRandomInt());
				measureLoadInfoEntity.setState2(getRandomInt());
				measureLoadInfoEntity.setDurationSeconds(getRandomLong());
				measureLoadInfoEntity.setDuration(getRandomString());
				measureLoadInfoEntity.setOccurTime(nowFormat);
				measureLoadInfoEntity.setOccurTimeYear(getRandomInt());
				measureLoadInfoEntity.setOccurTimeMonth(getRandomInt());
				measureLoadInfoEntity.setOccurTimeWeek(getRandomString());
				measureLoadInfoEntity.setOccurTimeDay(getRandomInt());
				measureLoadInfoEntity.setOccurTimeHourMinute(nowFormat);
				measureLoadInfoEntityList.add(measureLoadInfoEntity);
			}
			mongoTemplate.insert(measureLoadInfoEntityList, MeasureLoadInfoEntity.class);

			log.info("sub");
			List<MeasureSubInfoEntity> measureSubInfoEntityList = new ArrayList<>();
			for (int i = 0; i < 3000; i++) {
				MeasureSubInfoEntity measureSubInfoEntity = new MeasureSubInfoEntity();
				measureSubInfoEntity.setSubId(getRandomString());
				measureSubInfoEntity.setSubName(getRandomString());
				measureSubInfoEntity.setVoltLevel(getRandomString());
				measureSubInfoEntity.setCap(getRandomDouble());
				measureSubInfoEntity.setLoad(getRandomDouble());
				measureSubInfoEntity.setLoadRate(getRandomDouble());
				measureSubInfoEntity.setState(getRandomInt());
				measureSubInfoEntity.setState2(getRandomInt());
				measureSubInfoEntity.setDurationSeconds(getRandomLong());
				measureSubInfoEntity.setDuration(getRandomString());
				measureSubInfoEntity.setOccurTime(nowFormat);
				measureSubInfoEntity.setOccurTimeYear(getRandomInt());
				measureSubInfoEntity.setOccurTimeMonth(getRandomInt());
				measureSubInfoEntity.setOccurTimeWeek(getRandomString());
				measureSubInfoEntity.setOccurTimeDay(getRandomInt());
				measureSubInfoEntity.setOccurTimeHourMinute(nowFormat);
				measureSubInfoEntityList.add(measureSubInfoEntity);
			}
			mongoTemplate.insert(measureSubInfoEntityList, MeasureSubInfoEntity.class);

			log.info("tran");
			List<MeasureTranInfoEntity> measureTranInfoEntityList = new ArrayList<>();
			for (int i = 0; i < 3000; i++) {
				MeasureTranInfoEntity measureTranInfoEntity = new MeasureTranInfoEntity();
				measureTranInfoEntity.setTranId(getRandomString());
				measureTranInfoEntity.setImax(getRandomDouble());
				measureTranInfoEntity.setRPij(getRandomDouble());
				measureTranInfoEntity.setRPji(getRandomDouble());
				measureTranInfoEntity.setRQij(getRandomDouble());
				measureTranInfoEntity.setRQji(getRandomDouble());
				measureTranInfoEntity.setRIij(getRandomDouble());
				measureTranInfoEntity.setRIji(getRandomDouble());
				measureTranInfoEntity.setLoadRate(getRandomDouble());
				measureTranInfoEntity.setLowVoltageSideI(getRandomDouble());
				measureTranInfoEntity.setLowVoltageSideImax(getRandomDouble());
				measureTranInfoEntity.setLowVoltageSideLoadRate(getRandomDouble());
				measureTranInfoEntity.setState(getRandomInt());
				measureTranInfoEntity.setState2(getRandomInt());
				measureTranInfoEntity.setDurationSeconds(getRandomLong());
				measureTranInfoEntity.setDuration(getRandomString());
				measureTranInfoEntity.setOccurTime(nowFormat);
				measureTranInfoEntity.setOccurTimeYear(getRandomInt());
				measureTranInfoEntity.setOccurTimeMonth(getRandomInt());
				measureTranInfoEntity.setOccurTimeWeek(getRandomString());
				measureTranInfoEntity.setOccurTimeDay(getRandomInt());
				measureTranInfoEntity.setOccurTimeHourMinute(nowFormat);
				measureTranInfoEntityList.add(measureTranInfoEntity);
			}
			mongoTemplate.insert(measureTranInfoEntityList, MeasureTranInfoEntity.class);

			log.info("winding");
			List<MeasureWindingInfoEntity> measureWindingInfoEntityList = new ArrayList<>();
			for (int i = 0; i < 3000; i++) {
				MeasureWindingInfoEntity measureWindingInfoEntity = new MeasureWindingInfoEntity();
				measureWindingInfoEntity.setWindingId(getRandomString());
				measureWindingInfoEntity.setTranId(getRandomString());
				measureWindingInfoEntity.setTranName(getRandomString());
				measureWindingInfoEntity.setSubId(getRandomString());
				measureWindingInfoEntity.setSubName(getRandomString());
				measureWindingInfoEntity.setZoneName(getRandomString());
				measureWindingInfoEntity.setVoltLevel(getRandomString());
				measureWindingInfoEntity.setCap(getRandomDouble());
				measureWindingInfoEntity.setWindingType(getRandomInt());
				measureWindingInfoEntity.setImax(getRandomDouble());
				measureWindingInfoEntity.setRPij(getRandomDouble());
				measureWindingInfoEntity.setRPji(getRandomDouble());
				measureWindingInfoEntity.setRQij(getRandomDouble());
				measureWindingInfoEntity.setRQji(getRandomDouble());
				measureWindingInfoEntity.setRIij(getRandomDouble());
				measureWindingInfoEntity.setRIji(getRandomDouble());
				measureWindingInfoEntity.setI(getRandomDouble());
				measureWindingInfoEntity.setLoadRate(getRandomDouble());
				measureWindingInfoEntity.setOccurTime(nowFormat);
				measureWindingInfoEntity.setOccurTimeYear(getRandomInt());
				measureWindingInfoEntity.setOccurTimeMonth(getRandomInt());
				measureWindingInfoEntity.setOccurTimeWeek(getRandomString());
				measureWindingInfoEntity.setOccurTimeDay(getRandomInt());
				measureWindingInfoEntity.setOccurTimeHourMinute(nowFormat);
				measureWindingInfoEntityList.add(measureWindingInfoEntity);
			}
			mongoTemplate.insert(measureWindingInfoEntityList, MeasureWindingInfoEntity.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info("=============== 结束生成数据 ===============");
		}
	}

	private static final List<String> stringList = new ArrayList<>();

	static {
		for (int i = 0; i < 26; i++) {
			stringList.add(String.valueOf('A' + i));
		}
		for (int i = 0; i < 26; i++) {
			stringList.add(String.valueOf('a' + i));
		}
	}

	private String getRandomString() {
		Collections.shuffle(stringList);
		StringBuilder sb = new StringBuilder();
		int len = (int) (Math.random() * 52);
		for (int i = 0; i < len; i++) {
			sb.append(stringList.get(i));
		}
		return sb.toString();
	}

	private double getRandomDouble() {
		return Math.random() * 50 + 50;
	}

	private int getRandomInt() {
		return (int) (Math.random() * 50 + 50);
	}

	private long getRandomLong() {
		return (long) (Math.random() * 50 + 50);
	}

}
