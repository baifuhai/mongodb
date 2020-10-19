package com.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class MyUtil {

	public static File copyDir(File dir, File toDir, String newDirName) throws IOException {
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

}
