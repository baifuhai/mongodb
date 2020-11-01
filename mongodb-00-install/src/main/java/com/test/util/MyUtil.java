package com.test.util;

import com.test.domain.CommandResult;
import com.test.domain.MongodbParam;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class MyUtil {

	public static String getHumanFileSize(long length, boolean space) {
		String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB"};

		int i = 0;
		long chuShu = 1;
		while (length / chuShu >= 1024) {
			if (i + 1 > units.length - 1) {
				break;
			}
			i++;
			chuShu *= 1024;
		}

		BigDecimal bd = new BigDecimal(String.valueOf(((double) length) / chuShu)).setScale(2, BigDecimal.ROUND_DOWN);
		return bd.toString().replaceFirst("\\.00$", "") + (space ? " " : "") + units[i];
	}

	public static void generate(File fromDir, File newDir, MongodbParam mongodbParam) throws Exception {
		String mongodbCluster = mongodbParam.getMongodbCluster();
		String mongodbHome = mongodbParam.getMongodbHome();

		String configBindIp = mongodbParam.getConfigBindIp();
		String configIp = mongodbParam.getConfigIp();
		int configPort = mongodbParam.getConfigPort();

		String mongosBindIp = mongodbParam.getMongosBindIp();
		String mongosIp = mongodbParam.getMongosIp();
		int mongosPort = mongodbParam.getMongosPort();

		String shardBindIp = mongodbParam.getShardBindIp();
		String shardIp = mongodbParam.getShardIp();
		int beginShardPort = mongodbParam.getBeginShardPort();

		int beginShardNumber = mongodbParam.getBeginShardNumber();
		int endShardNumber = mongodbParam.getEndShardNumber();

		// copy dir
		System.out.println("copy dir");
		MyUtil.deleteDir(newDir);
		MyUtil.copyDir(fromDir, newDir.getParentFile(), newDir.getName());

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
			map.put("mongodbCluster", mongodbCluster);

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
			map.put("mongodbCluster", mongodbCluster);

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
				map.put("mongodbCluster", mongodbCluster);

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
				map.put("mongodbCluster", mongodbCluster);
				map.put("mongodbHome", mongodbHome);

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
			map.put("mongodbCluster", mongodbCluster);
			map.put("mongodbHome", mongodbHome);
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

		// restart2
		System.out.println("restart2");
		{
			File file = new File(newDir, "restart2.txt");

			StringBuilder sb = new StringBuilder();
			for (int i = beginShardNumber; i <= endShardNumber; i++) {
				sb.append("net stop MongoDBShard" + i);
				sb.append(System.lineSeparator());
				sb.append("net start MongoDBShard" + i);

				if (i != endShardNumber) {
					sb.append(System.lineSeparator());
					sb.append(System.lineSeparator());
				}
			}
			String stopStartShard = sb.toString();

			Map<String, Object> map = new HashMap<>();
			map.put("stopStartShard", stopStartShard);

			MyUtil.replaceToFileSelf(file, map);
		}

		System.out.println("end...");
	}

	public static File copyDir(File dir, File toDir, String newDirName) throws IOException {
		File toDirNew = new File(toDir, newDirName);
		toDirNew.mkdirs();

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

	public static File copyFileToDir(File file, File toDir) throws IOException {
		return copyFileToDir(file, toDir, file.getName());
	}

	public static File copyFileToDir(File file, File toDir, String newFileName) throws IOException {
		File toFile = new File(toDir, newFileName);
		copyFile(file, toFile);
		return toFile;
	}

	public static void copyFile(File file, File toFile) throws IOException {
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

	public static void replaceToFile(File file, Map<String, Object> map, File toFile) throws IOException {
		Writer writer = new FileWriter(toFile);
		replaceToWriter(file, map, writer);
	}

	public static void replaceToFileSelf(File file, Map<String, Object> map) throws IOException {
		File toFile = File.createTempFile(UUID.randomUUID().toString(), ".temp");
		Writer writer = new FileWriter(toFile);
		replaceToWriter(file, map, writer);
		copyFile(toFile, file);
		toFile.delete();
	}

	public static String replaceToString(File file, Map<String, Object> map) throws IOException {
		Writer writer = new StringWriter();
		replaceToWriter(file, map, writer);
		return writer.toString();
	}

	public static void replaceToWriter(File file, Map<String, Object> map, Writer writer) throws IOException {
		replaceToWriterByFunction(file, s -> replace(s, map), writer);
	}

	public static void replaceToFileByRegex(File file, Map<String, Object> map, File toFile) throws IOException {
		Writer writer = new FileWriter(toFile);
		replaceToWriterByRegex(file, map, writer);
	}

	public static void replaceToFileSelfByRegex(File file, Map<String, Object> map) throws IOException {
		File toFile = File.createTempFile(UUID.randomUUID().toString(), ".temp");
		Writer writer = new FileWriter(toFile);
		replaceToWriterByRegex(file, map, writer);
		copyFile(toFile, file);
		toFile.delete();
	}

	public static String replaceToStringByRegex(File file, Map<String, Object> map) throws IOException {
		Writer writer = new StringWriter();
		replaceToWriterByRegex(file, map, writer);
		return writer.toString();
	}

	public static void replaceToWriterByRegex(File file, Map<String, Object> map, Writer writer) throws IOException {
		replaceToWriterByFunction(file, s -> replaceByRegex(s, map), writer);
	}

	public static void replaceToWriterByFunction(File file, Function<String, String> function, Writer writer) throws IOException {
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
				bw.write(function.apply(line));
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

			writer.write(function.apply(sw.toString()));
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

	public static String replace(String line, Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			line = line.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
		}
		return line;
	}

	public static String replaceByRegex(String line, Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			line = line.replaceAll(entry.getKey(), String.valueOf(entry.getValue()));
		}
		return line;
	}

	public static void deleteDir(File dir) {
		emptyDir(dir);
		dir.delete();
	}

	public static void emptyDir(File dir) {
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

	public static void recurseDir(File dir, Consumer<File> fileConsumer) {
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

	public static CommandResult execute(String command) throws Exception {
		FileWriter fw = null;
		try {
			log.info("execute command: {}", command);

			// bat file
			File batFile = File.createTempFile(UUID.randomUUID().toString(), ".bat");

			// command write to bat file
			fw = new FileWriter(batFile);
			fw.write(command);
			fw.flush();
			fw.close();

			// execute bat file
			Process ps = Runtime.getRuntime().exec("\"" + batFile.getAbsolutePath() + "\"");
			ps.waitFor();

			// delete bat file
			batFile.delete();

			// commandResult
			CommandResult commandResult = new CommandResult();

			// exitValue
			int exitValue = ps.exitValue();
			commandResult.setExitValue(exitValue);

			// result
			{
				InputStream in = ps.getInputStream();
				byte[] buf = new byte[1024];
				int len;
				StringBuilder sb = new StringBuilder();
				while ((len = in.read(buf)) != -1) {
					sb.append(new String(buf, 0, len, Charset.forName("GBK")));
				}
				commandResult.setResult(sb.toString());

				log.info("execute command result: {}", commandResult.getResult());
			}

			// errorMessage
			/*if (exitValue != 0) */{
				InputStream in = ps.getErrorStream();
				byte[] buf = new byte[1024];
				int len;
				StringBuilder sb = new StringBuilder();
				while ((len = in.read(buf)) != -1) {
					sb.append(new String(buf, 0, len, Charset.forName("GBK")));
				}
				commandResult.setErrorMessage(sb.toString());

				log.info("execute command errorMessage: {}", commandResult.getErrorMessage());
			}

			return commandResult;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static CommandResult stopService(String serviceName) throws Exception {
		log.info("stopService: {}", serviceName);
		return execute("net stop " + serviceName);
	}

	public static CommandResult startService(String serviceName) throws Exception {
		log.info("startService: {}", serviceName);
		return execute("net start " + serviceName);
	}

	public static CommandResult deleteService(String serviceName) throws Exception {
		log.info("deleteService: {}", serviceName);
		return execute("sc delete " + serviceName);
	}

	public static CommandResult createService(String serviceName, String binPath) throws Exception {
		log.info("createService: {} {}", serviceName, binPath);
		String command = String.format("sc create %s binPath= \"%s\" DisplayName= \"%s\" start= \"auto\"", serviceName, binPath, serviceName);
		return execute(command);
	}

}
