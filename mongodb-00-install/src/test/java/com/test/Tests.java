package com.test;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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
		int endShardNumber = 5;

//		String baseDir = "C:/Users/Administrator/Desktop/Setup/Java/mongodb";
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
//		int beginShardNumber = 6;
//		int endShardNumber = 10;

		// copy dir
		System.out.println("copy dir");
		File fromDir = new File(this.getClass().getClassLoader().getResource("install/mongodb-cluster").toURI());
		String newDirName = fromDir.getName() + "-" + beginShardNumber + "-" + endShardNumber;
		File newDir = new File(fromDir.getParent(), newDirName);
		deleteDir(newDir);
		copyDir(fromDir, fromDir.getParentFile(), newDirName);

		// delete .gitignore
		recurseDir(newDir, file -> {
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

			replaceToFileSelf(file, map);
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

			replaceToFileSelf(file, map);
		}

		// shard
		System.out.println("shard");
		{
			File dir = new File(newDir, "shard/s0");
			for (int i = beginShardNumber; i <= endShardNumber; i++) {
				System.out.println("shard" + i);

				File shardDir = copyDir(dir, dir.getParentFile(), "s" + i);

				File file = new File(shardDir, "mongo.cfg");

				int shardPort = beginShardPort + i;

				Map<String, Object> map = new HashMap<>();
				map.put("i", i);
				map.put("shardBindIp", shardBindIp);
				map.put("shardPort", shardPort);
				map.put("baseDir", baseDir);

				replaceToFileSelf(file, map);
			}
			deleteDir(dir);
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

				installShardStringBuilder.append(replaceToString(file, map));

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

			replaceToFileSelf(file, map);
		}

		// delete
		System.out.println("delete");
		{
			File file = new File(newDir, "delete.txt");

			StringBuilder sb = new StringBuilder();
			for (int i = beginShardNumber; i <= endShardNumber; i++) {
				sb.append("rem shard" + i);
				sb.append(System.lineSeparator());
				sb.append(System.lineSeparator());
				sb.append("net stop MongoDBShard" + i);
				sb.append(System.lineSeparator());
				sb.append("sc delete MongoDBShard" + i);

				if (i != endShardNumber) {
					sb.append(System.lineSeparator());
					sb.append(System.lineSeparator());
				}
			}
			String deleteShard = sb.toString();

			Map<String, Object> map = new HashMap<>();
			map.put("deleteShard", deleteShard);

			replaceToFileSelf(file, map);
		}

		System.out.println("end...");
	}

	private File copyDir(File dir, File toDir, String newDirName) throws IOException {
		File toDirNew = new File(toDir, newDirName);
		toDirNew.mkdir();

		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					copyFileToDir(file, toDirNew);
				} else if (file.isDirectory()) {
					copyDir(file, toDirNew, file.getName());
				}
			}
		}

		return toDirNew;
	}

	private File copyFileToDir(File file, File toDir) throws IOException {
		File toFile = new File(toDir, file.getName());
		copyFile(file, toFile);
		return toFile;
	}

	private void copyFile(File file, File toFile) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(file);
			fos = new FileOutputStream(toFile);
			int len;
			byte[] buf = new byte[100 * 1024];
			while ((len = fis.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void replaceToFile(File file, Map<String, Object> map, File toFile) throws IOException {
		Writer writer = new FileWriter(toFile);
		replaceToWriter(file, map, writer);
	}

	private void replaceToFileSelf(File file, Map<String, Object> map) throws IOException {
		File toFile = File.createTempFile(UUID.randomUUID().toString(), ".temp");
		Writer writer = new FileWriter(toFile);
		replaceToWriter(file, map, writer);
		copyFile(toFile, file);
		toFile.delete();
	}

	private String replaceToString(File file, Map<String, Object> map) throws IOException {
		Writer writer = new StringWriter();
		replaceToWriter(file, map, writer);
		return writer.toString();
	}

	private void replaceToWriter(File file, Map<String, Object> map, Writer writer) throws IOException {
		/*
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(file));
			bw = new BufferedWriter(writer);
			int i = 0;
			String line;
			while ((line = br.readLine()) != null) {
				if (i != 0) {
					bw.newLine();
				}
				bw.write(replace(line, map));
				i++;
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		*/
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			StringWriter sw = new StringWriter();
			int len;
			char[] buf = new char[100 * 1024];
			while ((len = fr.read(buf)) != -1) {
				sw.write(buf, 0, len);
			}

			writer.write(replace(sw.toString(), map));
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String replace(String line, Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			line = line.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
		}
		return line;
	}

	private void deleteDir(File dir) {
		emptyDir(dir);
		dir.delete();
	}

	private void emptyDir(File dir) {
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					emptyDir(file);
				}
				file.delete();
			}
		}
	}

	private void recurseDir(File dir, Consumer<File> fileConsumer) {
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				fileConsumer.accept(file);
				if (file.isDirectory()) {
					recurseDir(file, fileConsumer);
				}
			}
		}
	}

}
