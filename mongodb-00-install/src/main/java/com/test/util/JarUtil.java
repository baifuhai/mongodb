package com.test.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtil {

	public static File getCurrentRunningJarFile() {
		String path = JarUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		return new File(path);
	}

	public static File unCompress(File file, String targetEntryName, File toDir) throws Exception {
		JarFile jarFile = new JarFile(file);

		if (targetEntryName != null && !targetEntryName.equals("")) {
			JarEntry targetEntry = jarFile.getJarEntry(targetEntryName);

			if (targetEntry == null) {
				if (!targetEntryName.endsWith("/")) {
					targetEntryName += "/";
					targetEntry = jarFile.getJarEntry(targetEntryName);
				}
			}

			if (targetEntry == null) {
				throw new Exception("entry not found: " + targetEntryName);
			}

			targetEntryName = targetEntry.getName();

			if (targetEntry.isDirectory()) {
				String dirName = targetEntryName.replaceFirst("/$", "").replaceFirst(".+/", "");
				File targetDir = new File(toDir, dirName);

				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();

					String entryName = entry.getName();
					if (entryName.startsWith(targetEntryName)) {
						String entryNameShort = entryName.replace(targetEntryName, "");

						if (entry.isDirectory()) {
							File dir = new File(targetDir, entryNameShort);
							if (!dir.exists()) {
								dir.mkdirs();
							}
						} else {
							File file2 = new File(targetDir, entryNameShort);
							FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), file2);
						}
					}
				}
				return targetDir;
			} else {
				String fileName = targetEntryName.replaceFirst(".+/", "");
				File file2 = new File(toDir, fileName);
				FileUtils.copyInputStreamToFile(jarFile.getInputStream(targetEntry), file2);
				return file2;
			}
		} else {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();

				String entryName = entry.getName();

				if (entry.isDirectory()) {
					File dir = new File(toDir, entryName);
					if (!dir.exists()) {
						dir.mkdirs();
					}
				} else {
					File file2 = new File(toDir, entryName);
					FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), file2);
				}
			}
			return null;
		}

//		JarInputStream jis = null;
//
//		try {
//			jis = new JarInputStream(new FileInputStream(file));
//
//			JarEntry jarEntry;
//			while ((jarEntry = jis.getNextJarEntry()) != null) {
//				if (jarEntry.getName().startsWith("install/")) {
//					System.out.println(jarEntry.getName());
//					if (jarEntry.isDirectory()) {
//						File dir = new File("D:/", jarEntry.getName());
//						if (!dir.exists()) {
//							dir.mkdirs();
//						}
//					} else {
//						File file2 = new File("D:/", jarEntry.getName());
//
//						FileOutputStream fos = null;
//						try {
//							fos = new FileOutputStream(file2);
//
//							IOUtils.copy(jis, fos);
//						} catch (Exception e) {
//							throw e;
//						} finally {
//							if (fos != null) {
//								try {
//									fos.close();
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			if (jis != null) {
//				try {
//					jis.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

	public static File getResource(String name) throws Exception {
		File file = getCurrentRunningJarFile();
		String uuid = UUID.randomUUID().toString();
		File tempFile = File.createTempFile(uuid, ".temp");
		File tempDir = tempFile.getParentFile();
		File targetFile = unCompress(file, name, tempDir);
		tempFile.delete();
		return targetFile;
	}

}
