package com.test.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	private ZipOutputStream zos;

	public ZipUtil(File file, int level) throws FileNotFoundException {
		this(new FileOutputStream(file), level);
	}

	public ZipUtil(OutputStream out, int level) {
		zos = new ZipOutputStream(out);
		zos.setLevel(level);
	}

	public void addEntry(File file, String entryName) throws IOException {
		addEntry(new FileInputStream(file), entryName);
	}

	public void addEntry(InputStream inputStream, String entryName) throws IOException {
		zos.putNextEntry(new ZipEntry(entryName));
		IOUtils.copy(inputStream, zos);
		inputStream.close();
	}

	public void finish() throws IOException {
		zos.flush();
		zos.close();
	}

	public static void unCompress(File zipFile, File toDir) throws Exception {
		ZipInputStream zis = null;

		try {
			zis = new ZipInputStream(new FileInputStream(zipFile));

			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					File dir = new File(toDir, entry.getName());
					if (!dir.exists()) {
						dir.mkdirs();
					}
				} else {
					File file = new File(toDir, entry.getName());
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}

					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(file);

						IOUtils.copy(zis, fos);
					} catch (Exception e) {
						throw e;
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}