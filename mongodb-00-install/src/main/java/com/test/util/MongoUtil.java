package com.test.util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.test.domain.MongoDatabaseCallback;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;

@Slf4j
public class MongoUtil {

	public static <T> T adminCallback(String host, int port, MongoDatabaseCallback<T> callback) throws Exception {
		return runCallback(host, port, "admin", callback);
	}

	public static <T> T runCallback(String host, int port, String databaseName, MongoDatabaseCallback<T> callback) throws Exception {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(host, port);

			MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

			T result = callback.doWithMongoDatabase(mongoDatabase);

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}

	public static Document adminCommand(String host, int port, Document commandDoc) throws Exception {
		return runCommand(host, port, "admin", commandDoc);
	}

	public static Document runCommand(String host, int port, String databaseName, Document commandDoc) throws Exception {
		Document resultDoc = runCallback(host, port, databaseName, new MongoDatabaseCallback<Document>() {
			@Override
			public Document doWithMongoDatabase(MongoDatabase mongoDatabase) throws Exception {
				Document resultDoc = mongoDatabase.runCommand(commandDoc);
				return resultDoc;
			}
		});
		return resultDoc;
	}

	public static void shutdown(String host, int port) throws Exception {
		Document commandDoc = new Document("shutdown", true);

		log.info("runCommand shutdown: ==> {} {} {}", host, port, commandDoc.toJson());

		Document resultDoc = adminCommand(host, port, commandDoc);

		log.info("runCommand shutdown: <== {} {} {}", host, port, resultDoc.toJson());
	}

	public static void replSetResizeOplog(String host, int port, long sizeMB) throws Exception {
		Document commandDoc = new Document();
		commandDoc.put("replSetResizeOplog", true);
		commandDoc.put("size", sizeMB);

		log.info("runCommand replSetResizeOplog: ==> {} {} {}", host, port, commandDoc.toJson());

		Document resultDoc = adminCommand(host, port, commandDoc);

		log.info("runCommand replSetResizeOplog: <== {} {} {}", host, port, resultDoc.toJson());
	}

	public static void compact(String host, int port, String collectionName, boolean force) throws Exception {
		Document commandDoc = new Document();
		commandDoc.put("compact", collectionName);
		commandDoc.put("force", force);

		log.info("runCommand compact: ==> {} {} {}", host, port, commandDoc.toJson());

		Document resultDoc = runCommand(host, port, "local", commandDoc);

		log.info("runCommand compact: <== {} {} {}", host, port, resultDoc.toJson());
	}

	public static void logRotate(String host, int port) throws Exception {
		Document commandDoc = new Document();
		commandDoc.put("logRotate", true);

		log.info("runCommand logRotate: ==> {} {} {}", host, port, commandDoc.toJson());

		Document resultDoc = adminCommand(host, port, commandDoc);

		log.info("runCommand logRotate: <== {} {} {}", host, port, resultDoc.toJson());
	}

	public static Document removeShard(String host, int port, String shardId) throws Exception {
		Document commandDoc = new Document();
		commandDoc.put("removeShard", shardId);

		log.info("runCommand removeShard: ==> {} {} {}", host, port, commandDoc.toJson());

		Document resultDoc = adminCommand(host, port, commandDoc);

		log.info("runCommand removeShard: <== {} {} {}", host, port, resultDoc.toJson());

		return resultDoc;
	}

	public static void restartService(String host, int port, String serviceName) throws Exception {
		stopService(host, port, serviceName);
		startService(serviceName);
	}

	public static void stopService(String host, int port, String serviceName) throws Exception {
		try {
			MongoUtil.shutdown(host, port);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		MyUtil.stopService(serviceName);
	}

	public static void startService(String serviceName) throws Exception {
		MyUtil.startService(serviceName);
	}

	public static void compressLogFile(File clusterDir, int beginShardNumber, int endShardNumber) {
		List<File> dirList = new ArrayList<>();
		dirList.add(new File(clusterDir, "config/log"));
		dirList.add(new File(clusterDir, "mongos/log"));
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			dirList.add(new File(clusterDir, "shard/s" + i + "/log"));
		}

		for (File dir : dirList) {
			log.info("compressLogFile: {}", dir.getAbsolutePath());

			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					try {
						compressLogFile(file);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	private static void compressLogFile(File file) throws Exception {
		if (file.isFile() && !file.getName().endsWith(".zip") && file.getName().matches(".+\\.log\\..+")) {
			ZipUtil zipUtil = null;
			try {
				File zipFile = new File(file.getParentFile(), file.getName() + ".zip");

				log.info("compressLogFile: {} -> {}", file.getAbsolutePath(), zipFile.getAbsolutePath());

				zipUtil = new ZipUtil(zipFile, Deflater.BEST_COMPRESSION);
				zipUtil.addEntry(file, file.getName());
				zipUtil.finish();

				file.delete();
			} catch (Exception e) {
				throw e;
			} finally {
				if (zipUtil != null) {
					try {
						zipUtil.finish();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	public static void logRotateAndCompressLogFile(File clusterDir, int beginShardNumber, int endShardNumber) throws Exception {
		String host = "127.0.0.1";
		int beginShardPort = 27000;

		List<Integer> portList = new ArrayList<>();
		portList.add(26999);
		portList.add(27000);
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			portList.add(beginShardPort + i);
		}

		for (int i = 0; i < portList.size(); i++) {
			int port = portList.get(i);

			log.info("logRotateAndCompressLogFile ({}/{})", i + 1, portList.size());

			logRotate(host, port);
		}

		compressLogFile(clusterDir, beginShardNumber, endShardNumber);
	}

}
