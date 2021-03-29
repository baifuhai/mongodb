package com.test;

import com.test.domain.MongodbParam;
import com.test.util.MyUtil;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Tests {

	@Test
	public void test01() throws Exception {
//		String mongodbCluster = "D:/new/sjzt/mongodb-cluster";
//		String mongodbHome = "D:/new/sjzt/mongodb-win32-x86_64-2012plus-4.2.7";
//
//		String configBindIp = "127.0.0.1";
//		String configIp = "127.0.0.1";
//		int configPort = 27009;
//
//		String mongosBindIp = "127.0.0.1";
//		String mongosIp = "127.0.0.1";
//		int mongosPort = 27010;
//
//		String shardBindIp = "127.0.0.1";
//		String shardIp = "127.0.0.1";
//		int beginShardPort = mongosPort;
//
//		int beginShardNumber = 1;
//		int endShardNumber = 10;

//		String mongodbCluster = "E:/mongodb/mongodb-cluster";
//		String mongodbHome = "E:/mongodb/mongodb-win32-x86_64-2012plus-4.2.7";
//
//		String configBindIp = "0.0.0.0";
//		String configIp = "192.168.1.20";
//		int configPort = 26999;
//
//		String mongosBindIp = "0.0.0.0";
//		String mongosIp = "192.168.1.20";
//		int mongosPort = 27000;
//
//		String shardBindIp = "0.0.0.0";
//		String shardIp = "192.168.1.20";
//		int beginShardPort = mongosPort;
//
//		int beginShardNumber = 11;
//		int endShardNumber = 50;

		String mongodbCluster = "D:/dev/mongodb-cluster";
		String mongodbHome = "D:/dev/mongodb-win32-x86_64-2012plus-4.2.7";

		String configBindIp = "0.0.0.0";
		String configIp = "192.168.0.79";
		int configPort = 26999;

		String mongosBindIp = "0.0.0.0";
		String mongosIp = "192.168.0.79";
		int mongosPort = 27000;

		String shardBindIp = "0.0.0.0";
		String shardIp = "192.168.0.79";
		int beginShardPort = mongosPort;

		int beginShardNumber = 1;
		int endShardNumber = 5;

//		String mongodbCluster = "C:/new/sjzt/mongodb-cluster";
//		String mongodbHome = "C:/new/sjzt/mongodb-win32-x86_64-2012plus-4.2.7";
//
//		String configBindIp = "0.0.0.0";
//		String configIp = "192.168.0.177";
//		int configPort = 26999;
//
//		String mongosBindIp = "0.0.0.0";
//		String mongosIp = "192.168.0.177";
//		int mongosPort = 27000;
//
//		String shardBindIp = "0.0.0.0";
//		String shardIp = "192.168.0.177";
//		int beginShardPort = mongosPort;
//
//		int beginShardNumber = 6;
//		int endShardNumber = 10;

		File fromDir = new File(this.getClass().getClassLoader().getResource("install/mongodb-cluster").toURI());

		String newDirName = fromDir.getName() + "-" + beginShardNumber + "-" + endShardNumber;
		File newDir = new File(fromDir.getParent(), newDirName);

		MongodbParam mongodbParam = new MongodbParam();

		mongodbParam.setMongodbCluster(mongodbCluster);
		mongodbParam.setMongodbHome(mongodbHome);

		mongodbParam.setConfigBindIp(configBindIp);
		mongodbParam.setConfigIp(configIp);
		mongodbParam.setConfigPort(configPort);

		mongodbParam.setMongosBindIp(mongosBindIp);
		mongodbParam.setMongosIp(mongosIp);
		mongodbParam.setMongosPort(mongosPort);

		mongodbParam.setShardBindIp(shardBindIp);
		mongodbParam.setShardIp(shardIp);
		mongodbParam.setBeginShardPort(beginShardPort);

		mongodbParam.setBeginShardNumber(beginShardNumber);
		mongodbParam.setEndShardNumber(endShardNumber);

		MyUtil.generate(fromDir, newDir, mongodbParam);
	}

	@Test
	public void test02() throws Exception {
		String oldIp = "192.168.1.59";
		String newIp = "192.168.1.20";
		String oldMongodbHome = "D:/new/sjzt/mongodb-win32-x86_64-2012plus-4.2.7";
		String newMongodbHome = "E:/mongodb/mongodb-win32-x86_64-2012plus-4.2.7";

//		String oldIp = "192.168.0.79";
//		String newIp = "192.168.0.177";
//		String oldMongodbHome = "E:/dev/mongodb-win32-x86_64-2012plus-4.2.7";
//		String newMongodbHome = "C:/new/sjzt/mongodb-win32-x86_64-2012plus-4.2.7";

		// readme
		System.out.println("readme");
		{
			File file = new File(this.getClass().getClassLoader().getResource("readme.txt").toURI());
			File newFile = new File(file.getParentFile(), "readme-new.txt");

			Map<String, Object> map = new HashMap<>();
			map.put("oldIp", oldIp);
			map.put("newIp", newIp);
			map.put("oldMongodbHome", oldMongodbHome);
			map.put("newMongodbHome", newMongodbHome);

			MyUtil.replaceToFile(file, map, newFile);
		}
	}

}
