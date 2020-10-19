package com.test;

import com.test.util.MyUtil;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Tests {

	@Test
	public void test01() throws Exception {
		String baseDir = "D:/new/sjzt";
		String mongodbDirName = "mongodb-win32-x86_64-2012plus-4.2.7";

		String configBindIp = "127.0.0.1";
		String configIp = "127.0.0.1";
		int configPort = 27009;

		String mongosBindIp = "127.0.0.1";
		String mongosIp = "127.0.0.1";
		int mongosPort = 27010;

		String shardBindIp = "127.0.0.1";
		String shardIp = "127.0.0.1";
		int beginShardPort = mongosPort;

		int beginShardNumber = 1;
		int endShardNumber = 10;

//		String baseDir = "D:/new/sjzt";
//		String mongodbDirName = "mongodb-win32-x86_64-2012plus-4.2.7";
//
//		String configBindIp = "0.0.0.0";
//		String configIp = "192.168.2.104";
//		int configPort = 26999;
//
//		String mongosBindIp = "0.0.0.0";
//		String mongosIp = "192.168.2.104";
//		int mongosPort = 27000;
//
//		String shardBindIp = "0.0.0.0";
//		String shardIp = "192.168.2.104";
//		int beginShardPort = mongosPort;
//
//		int beginShardNumber = 11;
//		int endShardNumber = 50;

		// copy dir
		System.out.println("copy dir");
		File fromDir = new File(this.getClass().getClassLoader().getResource("install/mongodb-cluster").toURI());
		String newDirName = fromDir.getName() + "-" + beginShardNumber + "-" + endShardNumber;
		File newDir = new File(fromDir.getParent(), newDirName);
		MyUtil.deleteDir(newDir);
		MyUtil.copyDir(fromDir, fromDir.getParentFile(), newDirName);

		// delete .gitignore
		MyUtil.recurseDir(newDir, file -> {
			if (file.isFile()) {
				if (file.getName().equals(".gitignore")) {
					file.delete();
				}
			}
		});

		// config
		System.out.println("config");
		{
			File file = new File(newDir, "config/mongo.cfg");

			Map<String, Object> map = new HashMap<>();
			map.put("configBindIp", configBindIp);
			map.put("configPort", configPort);
			map.put("baseDir", baseDir);

			MyUtil.replaceToFileSelf(file, map);
		}

		// mongos
		System.out.println("mongos");
		{
			File file = new File(newDir, "mongos/mongo.cfg");

			Map<String, Object> map = new HashMap<>();
			map.put("configIp", configIp);
			map.put("configPort", configPort);
			map.put("mongosBindIp", mongosBindIp);
			map.put("mongosPort", mongosPort);
			map.put("baseDir", baseDir);

			MyUtil.replaceToFileSelf(file, map);
		}

		// shard
		System.out.println("shard");
		{
			File dir = new File(newDir, "shard/s0");
			for (int i = beginShardNumber; i <= endShardNumber; i++) {
				System.out.println("shard" + i);

				File shardDir = MyUtil.copyDir(dir, dir.getParentFile(), "s" + i);

				File file = new File(shardDir, "mongo.cfg");

				int shardPort = beginShardPort + i;

				Map<String, Object> map = new HashMap<>();
				map.put("i", i);
				map.put("shardBindIp", shardBindIp);
				map.put("shardPort", shardPort);
				map.put("baseDir", baseDir);

				MyUtil.replaceToFileSelf(file, map);
			}
			MyUtil.deleteDir(dir);
		}

		// install shard
		System.out.println("install shard");
		StringBuilder installShardStringBuilder = new StringBuilder();
		{
			File file = new File(newDir, "install-shard.txt");
			for (int i = beginShardNumber; i <= endShardNumber; i++) {
				int shardPort = beginShardPort + i;

				Map<String, Object> map = new HashMap<>();
				map.put("i", i);
				map.put("shardIp", shardIp);
				map.put("shardPort", shardPort);
				map.put("baseDir", baseDir);
				map.put("mongodbDirName", mongodbDirName);

				installShardStringBuilder.append(MyUtil.replaceToString(file, map));

				if (i != endShardNumber) {
					installShardStringBuilder.append(System.lineSeparator());
					installShardStringBuilder.append(System.lineSeparator());
				}
			}
			file.delete();
		}

		// install
		System.out.println("install");
		{
			File file = new File(newDir, "install.txt");

			String installShard = installShardStringBuilder.toString();

			StringBuilder addShardStringBuilder = new StringBuilder();
			for (int i = beginShardNumber; i <= endShardNumber; i++) {
				int shardPort = beginShardPort + i;

				addShardStringBuilder.append(String.format("sh.addShard(\"shardReplicaSet%d/%s:%d\")", i, shardIp, shardPort));

				if (i != endShardNumber) {
					addShardStringBuilder.append(System.lineSeparator());
				}
			}
			String addShard = addShardStringBuilder.toString();

			Map<String, Object> map = new HashMap<>();
			map.put("configIp", configIp);
			map.put("configPort", configPort);
			map.put("mongosPort", mongosPort);
			map.put("baseDir", baseDir);
			map.put("mongodbDirName", mongodbDirName);
			map.put("installShard", installShard);
			map.put("addShard", addShard);

			MyUtil.replaceToFileSelf(file, map);
		}

		// stopShard
		StringBuilder stopShardStringBuilder = new StringBuilder();
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			stopShardStringBuilder.append("net stop MongoDBShard" + i);

			if (i != endShardNumber) {
				stopShardStringBuilder.append(System.lineSeparator());
			}
		}
		String stopShard = stopShardStringBuilder.toString();

		// startShard
		StringBuilder startShardStringBuilder = new StringBuilder();
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			startShardStringBuilder.append("net start MongoDBShard" + i);

			if (i != endShardNumber) {
				startShardStringBuilder.append(System.lineSeparator());
			}
		}
		String startShard = startShardStringBuilder.toString();

		// deleteShard
		StringBuilder deleteShardStringBuilder = new StringBuilder();
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			deleteShardStringBuilder.append("sc delete MongoDBShard" + i);

			if (i != endShardNumber) {
				deleteShardStringBuilder.append(System.lineSeparator());
			}
		}
		String deleteShard = deleteShardStringBuilder.toString();

		// delete
		System.out.println("delete");
		{
			File file = new File(newDir, "delete.txt");

			Map<String, Object> map = new HashMap<>();
			map.put("stopShard", stopShard);
			map.put("deleteShard", deleteShard);

			MyUtil.replaceToFileSelf(file, map);
		}

		// stop
		System.out.println("stop");
		{
			File file = new File(newDir, "stop.txt");

			Map<String, Object> map = new HashMap<>();
			map.put("stopShard", stopShard);

			MyUtil.replaceToFileSelf(file, map);
		}

		// start
		System.out.println("start");
		{
			File file = new File(newDir, "start.txt");

			Map<String, Object> map = new HashMap<>();
			map.put("startShard", startShard);

			MyUtil.replaceToFileSelf(file, map);
		}

		// restart
		System.out.println("restart");
		{
			File file = new File(newDir, "restart.txt");

			Map<String, Object> map = new HashMap<>();
			map.put("stopShard", stopShard);
			map.put("startShard", startShard);

			MyUtil.replaceToFileSelf(file, map);
		}

		System.out.println("end...");
	}

	@Test
	public void test02() throws Exception {
		String oldIp = "";
		String newIp = "";
		String oldMongodbHome = "D:/new/sjzt";
		String newMongodbHome = "D:/new/sjzt";

		// readme
		System.out.println("readme");
		{
			File file = new File(this.getClass().getClassLoader().getResource("readme.txt").toURI());

			Map<String, Object> map = new HashMap<>();
			map.put("oldIp", oldIp);
			map.put("newIp", newIp);
			map.put("oldMongodbHome", oldMongodbHome);
			map.put("newMongodbHome", newMongodbHome);

			MyUtil.replaceToFileSelf(file, map);
		}
	}

}
