package com.github.joelgodofwar.mmh.util.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUtil {
	public static final char JAR_SEPARATOR = '/';

	public static void copyFolderFromJar(String folderName, File destFolder, CopyOption option) throws IOException {
		copyFolderFromJar(folderName, destFolder, option, null);
	}

	public static void copyFolderFromJar(String folderName, File destFolder, CopyOption option, PathTrimmer trimmer) throws IOException {
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}

		byte[] buffer = new byte[1024];

		File fullPath = null;
		String path = JarUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (trimmer != null) {
			path = trimmer.trim(path);
		}
		try {
			if (!path.startsWith("file")) {
				path = "file://" + path;
			}

			fullPath = new File(new URI(path));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ZipInputStream zis = new ZipInputStream(new FileInputStream(fullPath));

		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			if (!entry.getName().startsWith(folderName + JAR_SEPARATOR)) {
				continue;
			}

			String fileName = entry.getName();

			if (fileName.charAt(fileName.length() - 1) == JAR_SEPARATOR) {
				File file = new File(destFolder + File.separator + fileName);
				if (file.isFile()) {
					file.delete();
				}
				file.mkdirs();
				continue;
			}

			File file = new File(destFolder + File.separator + fileName);
			if ((option == CopyOption.COPY_IF_NOT_EXIST) && file.exists()) {
				continue;
			}

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);

			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
		}

		zis.closeEntry();
		zis.close();
	}

	/**
	 * Copies a single file from the JAR to the specified destination.
	 *
	 * @param jarFilePath The path of the file inside the JAR (e.g., "heads/entity/cow.json").
	 * @param destPath    The destination path where the file should be saved (e.g., "temp_entity_cow.json").
	 * @throws IOException If an I/O error occurs during the copy process.
	 */
	public static void copyFileFromJar(String jarFilePath, String destPath) throws IOException {
		byte[] buffer = new byte[1024];

		// Get the path to the JAR file
		File fullPath = null;
		String path = JarUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			if (!path.startsWith("file")) {
				path = "file://" + path;
			}
			fullPath = new File(new URI(path));
		} catch (URISyntaxException e) {
			throw new IOException("Failed to determine JAR path: " + e.getMessage(), e);
		}

		// Open the JAR as a ZIP file
		ZipInputStream zis = new ZipInputStream(new FileInputStream(fullPath));
		ZipEntry entry;

		// Look for the specified file in the JAR
		while ((entry = zis.getNextEntry()) != null) {
			if (!entry.getName().equals(jarFilePath)) {
				continue;
			}

			// Found the file, copy it to the destination
			File destFile = new File(destPath);
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}

			try (FileOutputStream fos = new FileOutputStream(destFile)) {
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
			}

			zis.closeEntry();
			zis.close();
			return; // File found and copied, exit the method
		}

		zis.closeEntry();
		zis.close();
		throw new IOException("File not found in JAR: " + jarFilePath);
	}

	public enum CopyOption {
		COPY_IF_NOT_EXIST, REPLACE_IF_EXIST;
	}

	@FunctionalInterface
	public interface PathTrimmer {
		String trim(String original);
	}

	public static void main(String[] ar) throws IOException {
		copyFolderFromJar("SomeFolder", new File(""), CopyOption.REPLACE_IF_EXIST);
	}
}